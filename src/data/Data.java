package data;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {
    private static final long serialVersionUID = 1L;

    private String commandName;
    private List<Object> arguments;
    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Data(String commandName, List<Object> arguments, String login, String password) {
        this.commandName = commandName;
        this.arguments = arguments;
        this.login = login;
        this.password = password;
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

    public void setArguments() {
        arguments.add(login);
        arguments.add(password);
    }
}
