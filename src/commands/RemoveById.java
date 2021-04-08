package commands;


import dao.TicketDao;
import data.Ticket;
import server.Server;
import collection.MyTreeSet;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class RemoveById extends AbstractCommand {
    private MyTreeSet myTreeSet;
    private int userId;

    public RemoveById(String name, MyTreeSet myTreeSet, TicketDao ticketDao, int userId) {
        super(name, ticketDao);
        this.userId = userId;
        this.myTreeSet = myTreeSet;
    }

    @Override
    public String execute(List<Object> arguments) throws SQLException {
        int id = (int) arguments.get(0);
        if (id != -1) {
            Set<Ticket> userTickets = ticketDao.select(userId);
            boolean found = false;
            Ticket t = null;
            for (Ticket ticket : userTickets) {
                if (ticket.getId() == id) {
                    t = ticket;
                    found = true;
                    break;
                }
            }
            if (!found || !myTreeSet.findId(id)) {
                return "Element with id which you enter not found";
            } else {
                ticketDao.deleteTicket(t);
                myTreeSet.remove(t);
                return "Ticket deleted";
            }
        } else {
            return "Incorrect id";
        }
    }
}
