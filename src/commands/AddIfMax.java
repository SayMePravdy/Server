package commands;


import dao.TicketDao;
import server.Server;
import collection.MyTreeSet;
import data.Ticket;

import java.sql.SQLException;
import java.util.List;

public class AddIfMax extends AbstractCommand {
    private MyTreeSet myTreeSet;
    private int userId;

    public AddIfMax(String name, MyTreeSet myTreeSet, TicketDao ticketDao, int userId) {
        super(name, ticketDao);
        this.userId = userId;
        this.myTreeSet = myTreeSet;
    }

    @Override
    public String execute(List<Object> arguments) throws SQLException {
        Ticket ticket = (Ticket) arguments.get(0);
        if (ticket != null) {
            if (myTreeSet.isMax(ticket)) {
                ticket.setId(ticketDao.insertTicket(ticket, userId));
                myTreeSet.add(ticket);
                return "Ticket added";
            } else {
                return "Element isn't maximal";
            }
        } else {
            return "Incorrect data in script";
        }
    }
}
