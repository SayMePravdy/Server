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
    public String execute(List<Object> arguments) {
        try {
            return myTreeSet.maxComment().toString();
        } catch (NullPointerException e) {
            return "Collection is empty";
        }

    }
}
