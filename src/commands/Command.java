package commands;

import java.sql.SQLException;
import java.util.List;

public interface Command {
    String execute(List<Object> arguments) throws SQLException;
}
