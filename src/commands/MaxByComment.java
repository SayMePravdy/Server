package commands;

import server.Server;
import collection.MyTreeSet;

import java.util.List;

public class MaxByComment extends AbstractCommand {
    private MyTreeSet myTreeSet;

    public MaxByComment(String name, MyTreeSet myTreeSet) {
        super(name);
        this.myTreeSet = myTreeSet;
    }

    @Override
    public void execute(List<Object> arguments) {
        try {
            Server.sendMessage(myTreeSet.maxComment().toString());
        } catch (NullPointerException e) {
            Server.sendMessage("Collection is empty");
        }

    }
}
