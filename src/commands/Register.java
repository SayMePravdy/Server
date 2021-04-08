package commands;

import dao.TicketDao;

import java.sql.SQLException;
import java.util.List;

public class Register extends AbstractCommand{

    public Register(String name, TicketDao ticketDao) {
        super(name, ticketDao);
    }

    @Override
    public String execute(List<Object> arguments) throws SQLException {
        ticketDao.insertUser((String) arguments.get(0), (String) arguments.get(1));
        return "User sign up";
    }
}
