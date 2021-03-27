package commands;

import server.Server;

import java.util.List;

public class Exit extends AbstractCommand {

    public Exit(String name) {
        super(name);
    }

    @Override
    public String execute(List<Object> arguments) {
        return "exit";
    }
}
