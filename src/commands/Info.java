package commands;

import server.Server;
import collection.MyTreeSet;

import java.util.List;

public class Info extends AbstractCommand{
    private MyTreeSet myTreeSet;

    public Info(String name, MyTreeSet myTreeSet) {
        super(name);
        this.myTreeSet = myTreeSet;
    }

    @Override
    public String execute(List<Object> arguments) {
        return myTreeSet.showInfo();
    }
}
