package commands;


import server.Server;
import collection.MyTreeSet;

import java.util.List;

public class RemoveById extends AbstractCommand {
    private MyTreeSet myTreeSet;

    public RemoveById(String name, MyTreeSet myTreeSet) {
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
                Server.sendMessage("Ticket deleted");
            }
        } else {
            Server.sendMessage("Incorrect id");
        }
    }
}
