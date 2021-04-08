package commands;

import dao.TicketDao;

public abstract class AbstractCommand implements Command{
    private String name;
    protected TicketDao ticketDao;

    public AbstractCommand(String name) {
        this.name = name;
    }

    public AbstractCommand(String name, TicketDao ticketDao) {
        this.name = name;
        this.ticketDao = ticketDao;
    }

    public String getName() {
        return name;
    }

}
