package commands;

import dao.TicketDao;
import data.Ticket;
import server.Server;
import collection.MyTreeSet;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Clear extends AbstractCommand{
    private MyTreeSet myTreeSet;
    private int userId;

    public Clear(String name, MyTreeSet myTreeSet, TicketDao ticketDao, int userId) {
        super(name, ticketDao);
        this.userId = userId;
        this.myTreeSet = myTreeSet;
    }

    @Override
    public String execute(List<Object> argument) throws SQLException {
        Set<Ticket> userTickets = ticketDao.select(userId);
        for (Ticket ticket : userTickets) {
            ticketDao.deleteTicket(ticket);
            myTreeSet.remove(ticket);
        }
        return "Tickets deleted";
    }
}
