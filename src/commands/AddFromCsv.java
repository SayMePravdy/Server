package commands;

import collection.MyTreeSet;
import data.Coordinates;
import data.Event;
import data.Ticket;
import data.TicketType;
import exceptions.InvalidArgument;
import exceptions.NullTicketArgument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AddFromCsv extends AbstractCommand {
    private final static int MIN = 0;
    private final static long MAX_DISCOUNT = 100;
    private final static float MAX_Y = 266f;
    public static int FIRST_TICKET_ID = 1;
    public static int FIRST_EVENT_ID = 1;
    private String ticketName, eventName, comment;
    private float price, y;
    private long discount;
    private int minAge;
    private double x;
    private Integer ticketsCount;
    private TicketType ticketType = null;
    private Event event = null;

    private MyTreeSet treeSet;
    private File file;

    public AddFromCsv(String name, MyTreeSet treeSet, File file) {
        super(name);
        this.treeSet = treeSet;
        this.file = file;
    }

    @Override
    public String execute(List<Object> arguments) {
        StringBuilder answer = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            List<Integer> errorLines = new ArrayList<>();
            int cntTickets = 0;
            int line = 1;
            while (scanner.hasNext()) {
                Ticket ticket = getTicketFromCSV(scanner.nextLine());
                if (ticket != null) {
                    treeSet.add(ticket);
                    cntTickets++;
                } else {
                    errorLines.add(line);
                }
                line++;
            }
            if (cntTickets != 0) {
                answer.append("Added " + cntTickets + " tickets");
            }
            int size = errorLines.size();
            if (size != 0) {
                answer.append("\nIncorrect data in file on lines: ");
                for (int i = 0; i < size - 1; i++) {
                    answer.append(errorLines.get(i) + ", ");
                }
                answer.append(errorLines.get(size-1) + "\n");
            }
            return answer.toString();
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            return "File not found";
        }
    }

    private Ticket getTicketFromCSV(String data) {
        String[] args = data.split(",");
        try {
            checkName(args[0]);
            event = null;
            ticketType = null;
            ticketName = args[0];
            x = Double.parseDouble(args[1]);
            y = checkY(args[2]);
            price = checkPrice(args[3]);
            discount = checkDiscount(args[4]);
            checkComment(args[5]);
            comment = args[5];
            if (args.length > 6) {
                if (args.length == 7) {
                    ticketType = checkTicketType(args[6]);
                } else {
                    if (args.length == 10) {
                        ticketType = checkTicketType(args[6]);
                        checkName(args[7]);
                        eventName = args[7];
                        minAge = Integer.parseInt(args[8]);
                        ticketsCount = checkTicketsCount(args[9]);
                        event = new Event(FIRST_EVENT_ID++, eventName, minAge, ticketsCount);
                    } else {
                        if (args.length == 9) {
                            checkName(args[6]);
                            eventName = args[6];
                            minAge = Integer.parseInt(args[7]);
                            ticketsCount = checkTicketsCount(args[8]);
                            event = new Event(FIRST_EVENT_ID++, eventName, minAge, ticketsCount);
                        } else {
                            return null;
                        }
                    }
                }
            }
        } catch (InvalidArgument | NullTicketArgument | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
        return new Ticket(FIRST_TICKET_ID++, ticketName, new Coordinates(x, y), ZonedDateTime.now(), price, discount, comment, ticketType, event);
    }

    private void checkName(String data) throws NullTicketArgument {
        if (data.isEmpty()) {
            throw new NullTicketArgument("Incorrect name");
        }
    }

    private float checkY(String data) throws InvalidArgument, NumberFormatException {
        float y = Float.parseFloat(data);
        if (y > MAX_Y) {
            throw new InvalidArgument(String.format("y-coordinate must be less than %f", MAX_Y));
        }
        return y;
    }

    private float checkPrice(String data) throws InvalidArgument, NumberFormatException {
        float price = Float.parseFloat(data);
        if (price <= MIN) {
            throw new InvalidArgument(String.format("Price must be more then %d", MIN));
        }
        return price;
    }

    private long checkDiscount(String data) throws InvalidArgument, NumberFormatException {
        long discount = Long.parseLong(data);
        if (discount <= MIN || discount > MAX_DISCOUNT) {
            throw new InvalidArgument(String.format("Discount can be from %d to %d", MIN, MAX_DISCOUNT));
        }
        return discount;
    }

    private void checkComment(String data) throws InvalidArgument {
        if (data.isEmpty()) {
            throw new InvalidArgument("Incorrect comment");
        }

    }

    private int checkTicketsCount(String data) throws NumberFormatException, NullTicketArgument {
        int ticketsCount = Integer.parseInt(data);
        if (ticketsCount <= MIN) {
            throw new NullTicketArgument(String.format("Tickets count must be more than %d", MIN));
        }
        return ticketsCount;
    }

    private TicketType checkTicketType(String data) throws NullTicketArgument {
        switch (data) {
            case "VIP":
                return TicketType.VIP;
            case "USUAL":
                return TicketType.USUAL;
            case "BUDGETARY":
                return TicketType.BUDGETARY;
            case "CHEAP":
                return TicketType.CHEAP;
            default:
                throw new NullTicketArgument("Could not find this ticket type");
        }
    }
}
