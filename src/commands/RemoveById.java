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
    public String execute(List<Object> arguments) {
        int id = (int) arguments.get(0);
        if (id != -1) {
            if (!myTreeSet.remove(id)) {
                return "Element with your id not found";
            } else {
                return "Ticket deleted";
            }
        } else {
            return "Incorrect id";
        }
    }
}
