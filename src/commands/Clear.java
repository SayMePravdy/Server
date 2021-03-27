package commands;

import server.Server;
import collection.MyTreeSet;

import java.util.List;

public class Clear extends AbstractCommand{
    private MyTreeSet myTreeSet;

    public Clear(String name, MyTreeSet myTreeSet) {
        super(name);
        this.myTreeSet = myTreeSet;
    }

    @Override
    public String execute(List<Object> arguments) {
        myTreeSet.clear();
        return "Collection is empty";
    }
}
