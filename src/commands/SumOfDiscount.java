package commands;

import server.Server;
import collection.MyTreeSet;

import java.util.List;

public class SumOfDiscount extends AbstractCommand {
    private MyTreeSet myTreeSet;

    public SumOfDiscount(String name, MyTreeSet myTreeSet) {
        super(name);
        this.myTreeSet = myTreeSet;
    }

    @Override
    public void execute(List<Object> arguments) {
        Server.sendMessage("" + myTreeSet.sumDiscount());
    }
}
