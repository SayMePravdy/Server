package commands;

import server.Server;
import collection.MyTreeSet;
import data.Ticket;

import java.util.List;

public class RemoveGreater extends AbstractCommand {
    private MyTreeSet myTreeSet;

    public RemoveGreater(String name, MyTreeSet myTreeSet) {
        super(name);
        this.myTreeSet = myTreeSet;
    }

    @Override
    public String execute(List<Object> arguments) {
        Ticket ticket = (Ticket) arguments.get(0);
        if (ticket != null) {
            myTreeSet.removeGreater(ticket);
            return "Tickets deleted";
        } else {
            return "Incorrect data in script";
        }
    }
}
