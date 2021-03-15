package data;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {
    private static final long serialVersionUID = 1L;

    private String commandName;
    private List<Object> arguments;

    public Data(String commandName, List<Object> arguments) {
        this.commandName = commandName;
        this.arguments = arguments;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }
}
