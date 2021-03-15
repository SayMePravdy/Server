package commands;


import server.Server;
import collection.MyTreeSet;

import java.util.List;

public class PrintUniquePrice extends AbstractCommand {
    private MyTreeSet myTreeSet;

    public PrintUniquePrice(String name, MyTreeSet myTreeSet) {
        super(name);
        this.myTreeSet = myTreeSet;
    }

    @Override
    public void execute(List<Object> arguments) {
        Server.sendMessage(myTreeSet.uniquePrices().toString());
    }
}
