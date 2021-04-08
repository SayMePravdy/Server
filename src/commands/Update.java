package commands;


import dao.TicketDao;
import server.Server;
import collection.MyTreeSet;
import data.Ticket;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class Update extends AbstractCommand{
    private MyTreeSet myTreeSet;
    private int userId;

    public Update(String name, MyTreeSet myTreeSet, TicketDao ticketDao, int userId) {
        super(name, ticketDao);
        this.userId = userId;
        this.myTreeSet = myTreeSet;
    }

    @Override
    public String execute(List<Object> arguments) throws SQLException {
        int id = (int) arguments.get(0);
        if (id != -1) {
            if (!myTreeSet.findId(id)) {
                return "Element with your id not found";
            } else {
                Ticket ticket = (Ticket) arguments.get(1);
                if (ticket != null){
                    Set<Ticket> userTickets = ticketDao.select(userId);
                    for (Ticket t : userTickets) {
                        if (t.getId() == id) {
                            ticket.setId(id);
                            ticketDao.updateTicket(t);
                            myTreeSet.remove(id);
                            myTreeSet.add(ticket);
                            return "Ticket updated";
                        }
                    }
                    return "You haven't rights to change this ticket";
                }
                else {
                    return "Incorrect data in script";
                }
            }
        } else {
            return "Incorrect id";
        }
    }
}
