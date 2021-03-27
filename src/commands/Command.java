package commands;

import java.util.List;

public interface Command {
    String execute(List<Object> arguments);
}
