package dao;

import data.Coordinates;
import data.Event;
import data.Ticket;
import data.TicketType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

public class TicketDao {
    private final String DB_URL;
    private final String USER;
    private final String PASS;
    private Connection connection;

    public TicketDao(String DB_URL, String USER, String PASS) throws SQLException{
        this.DB_URL = DB_URL;
        this.USER = USER;
        this.PASS = PASS;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver successfully connected");


        connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);


    }

    private Ticket getTicket(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String name = resultSet.getString(2);
        double x = resultSet.getDouble(3);
        float y = resultSet.getFloat(4);
        ZonedDateTime date = resultSet.getTimestamp(5).toLocalDateTime().atZone(ZoneId.of("+03:00"));
        float price = resultSet.getFloat(6);
        long discount = resultSet.getLong(7);
        String comment = resultSet.getString(8);
        TicketType ticketType = parseToTicketType(resultSet.getString(9));
        Event event = null;
        if (resultSet.getObject(10, Integer.class) != null) {
            event = new Event(resultSet.getInt(12), resultSet.getString(13), resultSet.getInt(14), resultSet.getInt(15));

        }
        return new Ticket(id, name, new Coordinates(x, y), date, price, discount, comment, ticketType, event);
    }

    private void updateEvent(Event event) throws SQLException {
        String sql = "UPDATE events SET name = ?, min_age = ?, tickets_count = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, event.getName());
        preparedStatement.setInt(2, event.getMinAge());
        preparedStatement.setInt(3, event.getTicketsCount());
        preparedStatement.setInt(4, event.getId());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void updateTicket(Ticket ticket) throws SQLException {
        String sql = "UPDATE tickets SET name = ?, x = ?, y = ?, creation_date = ?, price = ?, discount = ?, comment = ?, ticket_type = ? " +
                "WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        connection.setAutoCommit(false);
        try{
            if (ticket.getEvent() != null) {
                updateEvent(ticket.getEvent());
            }
            setTicketStatement(preparedStatement, ticket);
            preparedStatement.setInt(9, ticket.getId());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            System.out.println("Smth wrong with update database");
        } finally {
            connection.setAutoCommit(true);
        }
        preparedStatement.close();
    }

    private void setTicketStatement(PreparedStatement preparedStatement, Ticket ticket) throws SQLException {
        preparedStatement.setString(1, ticket.getName());
        preparedStatement.setDouble(2, ticket.getCoordinates().getX());
        preparedStatement.setFloat(3, ticket.getCoordinates().getY());
        preparedStatement.setTimestamp(4, Timestamp.from(ticket.getCreationDate().toInstant()));
        preparedStatement.setFloat(5, ticket.getPrice());
        preparedStatement.setLong(6, ticket.getDiscount());
        preparedStatement.setString(7, ticket.getComment());
        if (ticket.getType() != null) {
            preparedStatement.setString(8, ticket.getType().toString());
        } else {
            preparedStatement.setObject(8, null);
        }
    }


    public Integer insertTicket(Ticket ticket, int userId) throws SQLException {
        String sql = "INSERT INTO tickets (name, x, y, creation_date, price, discount, comment, ticket_type, event_id, user_id) " +
                "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING id";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try{
            connection.setAutoCommit(false);
            Integer eventId = null;
            if (ticket.getEvent() != null) {
                eventId = insertEvent(ticket.getEvent());
                ticket.getEvent().setId(eventId);
            }
            setTicketStatement(preparedStatement, ticket);
            preparedStatement.setObject(9, eventId);
            preparedStatement.setInt(10, userId);
            preparedStatement.execute();
            ResultSet last_updated_ticket = preparedStatement.getResultSet();
            last_updated_ticket.next();
            int last_updated_ticket_id = last_updated_ticket.getInt(1);
            return last_updated_ticket_id;
        } catch (SQLException e) {
            connection.rollback();
            System.out.println("Smth wrong with add to database");
            e.printStackTrace();
            return null;
        } finally {
            connection.setAutoCommit(true);
            preparedStatement.close();
        }

    }

    public void deleteTicket(Ticket ticket) throws SQLException {
        String ticketSql = "DELETE FROM tickets WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(ticketSql);
        connection.setAutoCommit(false);
        try {
            preparedStatement.setInt(1, ticket.getId());
            preparedStatement.executeUpdate();
            if (ticket.getEvent() != null) {
                String eventSql = "DELETE FROM events WHERE id = " + ticket.getEvent().getId();
                preparedStatement = connection.prepareStatement(eventSql);
            }
            preparedStatement.close();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            System.out.println("Smth wrong with database");
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private int insertEvent(Event event) throws SQLException {
        String sql = "INSERT INTO events (name, min_age, tickets_count) " +
                "Values (?, ?, ?) " +
                "RETURNING id";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, event.getName());
        preparedStatement.setInt(2, event.getMinAge());
        preparedStatement.setInt(3, event.getTicketsCount());
        preparedStatement.execute();
        ResultSet last_updated_event = preparedStatement.getResultSet();
        last_updated_event.next();
        int last_updated_event_id = last_updated_event.getInt(1);
        preparedStatement.close();
        return last_updated_event_id;
    }

    public void insertUser(String login, String password) throws SQLException {
        String sql = "INSERT INTO users (login, password) " +
                "Values (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, hashPassword(password));
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public Set<Ticket> select(int userId) throws SQLException {
        String sql = "SELECT * FROM tickets t " +
                "LEFT JOIN events e ON t.event_id = e.id " +
                "JOIN users u ON t.user_id = u.id WHERE u.id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();
        Set<Ticket> userTickets = new HashSet<>();
        while (resultSet.next()) {
            userTickets.add(getTicket(resultSet));
        }
        return userTickets;
    }

    public Set<Ticket> select() throws SQLException {
        String sql = "SELECT * FROM tickets t " +
                "LEFT JOIN events e ON t.event_id = e.id ";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        Set<Ticket> userTickets = new HashSet<>();
        while (resultSet.next()) {
            userTickets.add(getTicket(resultSet));
        }
        return userTickets;
    }

    public Connection getConnection() {
        return connection;
    }

    private TicketType parseToTicketType(String ticketType) {
        if (ticketType == null)
            return null;
        switch (ticketType) {
            case "VIP":
                return TicketType.VIP;
            case "USUAL":
                return TicketType.USUAL;
            case "BUDGETARY":
                return TicketType.BUDGETARY;
        }
        return TicketType.CHEAP;
    }

    public Integer findUser(String login, String password) throws SQLException {
        String sql = "SELECT id FROM users WHERE login = ? AND password = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, hashPassword(password));
        ResultSet resultSet = preparedStatement.executeQuery();
        Integer id = null;
        if (resultSet.next()) {
            id = resultSet.getObject(1, Integer.class);
        }
        return id;
    }

    private String hashPassword(String password) {
        String sha1 = password;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            sha1 = new String(md.digest(password.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Problem with hashing! Password isn't hashed");
        }
        return sha1;
    }
}
