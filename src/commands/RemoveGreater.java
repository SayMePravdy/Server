package commands;

import dao.TicketDao;
import server.Server;
import collection.MyTreeSet;
import data.Ticket;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class RemoveGreater extends AbstractCommand {
    private MyTreeSet myTreeSet;
    private int userId;

    public RemoveGreater(String name, MyTreeSet myTreeSet, TicketDao ticketDao, int userId) {
        super(name, ticketDao);
        this.userId = userId;
        this.myTreeSet = myTreeSet;
    }

    @Override
    public String execute(List<Object> arguments) throws SQLException {
        Ticket ticket = (Ticket) arguments.get(0);
        if (ticket != null) {
            Set<Ticket> userTickets = ticketDao.select(userId);
            for (Ticket t : userTickets) {
                if (t.compareTo(ticket) > 0) {
                    ticketDao.deleteTicket(t);
                    myTreeSet.remove(t);

                }
            }
            return "Tickets deleted";
        } else {
            return "Incorrect data in script";
        }
    }
}
