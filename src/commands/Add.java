package commands;


import dao.TicketDao;
import server.Server;
import collection.MyTreeSet;
import data.Ticket;

import java.sql.SQLException;
import java.util.List;


public class Add extends AbstractCommand {

    private MyTreeSet myTreeSet;
    private int userId;
    //private Server server;

    public Add(String name, MyTreeSet myTreeSet, TicketDao ticketDao, int userId) {
        super(name, ticketDao);
        this.userId = userId;
        this.myTreeSet = myTreeSet;
    }

    @Override
    public String execute(List<Object> arguments) throws SQLException {
        Ticket ticket = (Ticket)arguments.get(0);
        if (ticket != null) {
            ticket.setId(ticketDao.insertTicket(ticket, userId));
            myTreeSet.add(ticket);
            return "Ticket added";
        } else {
            return "Incorrect data in file";
        }
    }
}
