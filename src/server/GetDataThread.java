package server;

import collection.MyTreeSet;
import commands.*;
import dao.TicketDao;
import data.Data;
import org.postgresql.util.PSQLException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.List;

public class GetDataThread extends Thread {
    private SocketChannel socket;
    private TicketDao ticketDao;
    private MyTreeSet treeSet;
    private ConnectionHandler handler;

    public GetDataThread(TicketDao ticketDao, MyTreeSet treeSet, SocketChannel socket, ConnectionHandler handler) {
        this.ticketDao = ticketDao;
        this.treeSet = treeSet;
        this.socket = socket;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(65536);
            socket.read(buffer);
            Data data = null;
            try {
                data = deserialize(buffer);
            } catch (ClassNotFoundException e) {
            }
            String ans = "";
            try {
                if (data != null) {
                    if (ticketDao.getConnection() == null) return;
                    Integer userId = ticketDao.findUser(data.getLogin(), data.getPassword());
                    if (userId != null || data.getCommandName().equals("register")) {
                        Command command = searchCommand(data.getCommandName(), userId);
                        ans = command.execute(data.getArguments());
                    } else {
                        ans = "Can't found this user";
                    }
                } else {
                    ans = "Transport or other problem";
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("too long")) {
                    ans = "Your data is too long";
                } else {
                    if (e.getMessage().contains("duplicate")) {
                        ans = "User with this login already exists";
                    } else {
                        //e.printStackTrace();
                        if (e.getMessage().contains("бэкенду")) {
                            ans = "Database crashed, exit";
                            ticketDao.setConnection(null);
                        }
                        handler.setStop(true);
                    }
                }
            }
            SendDataThread sendDataThread = new SendDataThread(ans, socket);
            sendDataThread.start();
        } catch (IOException e) {
            handler.setStop(true);
            //System.out.println("Problem with connection");
        }
    }


    private Command searchCommand(String command, Integer userId) {
        switch (command) {
            case "help":
                return new Help("help");
            case "info":
                return new Info("info", treeSet);
            case "show":
                return new Show("show", treeSet);
            case "add":
                return new Add("add", treeSet, ticketDao, userId);
            case "update":
                return new Update("update", treeSet, ticketDao, userId);
            case "remove_by_id":
                return new RemoveById("remove_by_id", treeSet, ticketDao, userId);
            case "clear":
                return new Clear("clear", treeSet, ticketDao, userId);
            case "add_if_max":
                return new AddIfMax("add_if_max", treeSet, ticketDao, userId);
            case "add_if_min":
                return new AddIfMin("add_if_min", treeSet, ticketDao, userId);
            case "remove_greater":
                return new RemoveGreater("remove_greater", treeSet, ticketDao, userId);
            case "sum_of_discount":
                return new SumOfDiscount("sum_of_discount", treeSet);
            case "max_by_comment":
                return new MaxByComment("max_by_comment", treeSet);
            case "print_unique_price":
                return new PrintUniquePrice("print_unique_price", treeSet);
            case "exit":
                return new Exit("exit");
            case "register":
                return new Register("register", ticketDao);
            default:
                return new Command() {
                    @Override
                    public String execute(List<Object> arguments) {
                        return null;
                    }
                };
        }
    }

    private <T> T deserialize(ByteBuffer byteBuffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        T data = (T) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        byteBuffer.clear();
        return data;
    }
}
