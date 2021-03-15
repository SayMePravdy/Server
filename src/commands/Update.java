package commands;


import server.Server;
import collection.MyTreeSet;
import data.Ticket;

import java.util.List;

public class Update extends AbstractCommand{
    private MyTreeSet myTreeSet;

    public Update(String name, MyTreeSet myTreeSet) {
        super(name);
        this.myTreeSet = myTreeSet;
    }

    @Override
    public void execute(List<Object> arguments) {
        int id = (int) arguments.get(0);
        if (id != -1) {
            if (!myTreeSet.remove(id)) {
                Server.sendMessage("Element with your id not found");
            } else {
                Ticket ticket = (Ticket) arguments.get(1);
                if (ticket != null){
                    myTreeSet.add(ticket);
                    Server.sendMessage("Ticket updated");
                }
                else {
                    Server.sendMessage("Incorrect data in script");
                }
            }
        } else {
            Server.sendMessage("Incorrect id");
        }
    }
}
