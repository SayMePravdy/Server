package commands;


import server.Server;
import collection.MyTreeSet;
import data.Ticket;

import java.util.List;


public class Add extends AbstractCommand {
    private static int FIRST_ID = 1;

    public static void setFirstId(int firstId) {
        FIRST_ID = firstId;
    }

    public static void setFirstEventId(int firstEventId) {
        FIRST_EVENT_ID = firstEventId;
    }

    private static int FIRST_EVENT_ID = 1;
    private MyTreeSet myTreeSet;
    //private Server server;

    public Add(String name, MyTreeSet myTreeSet) {
        super(name);
        this.myTreeSet = myTreeSet;
    }

    @Override
    public String execute(List<Object> arguments) {
        Ticket ticket = (Ticket)arguments.get(0);
        if (ticket != null) {
            ticket.setId(FIRST_ID++);
            if (ticket.getEvent() != null) {
                ticket.getEvent().setId(FIRST_EVENT_ID++);
            }
            myTreeSet.add(ticket);
           return "Ticket added";
        } else {
            return "Incorrect data in file";
        }
    }
}
