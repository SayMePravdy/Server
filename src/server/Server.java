package server;

import collection.MyTreeSet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import commands.*;
import dao.TicketDao;
import data.Coordinates;
import data.Data;
import data.Ticket;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;

public class Server {
    //private static ByteBuffer byteBuffer = ByteBuffer.allocate(16384);
    private static ServerSocketChannel serverSocketChannel;
    private static Selector selector;
    private static int PORT = 13345;
    private static TicketDao ticketDao;
    private static final String DB_URL = "jdbc:postgresql://localhost:9999/studs";
    /*
    для хелиоса
    private static final String DB_URL = "jdbc:postgresql://pg:5432/studs";
     */
    private static final String USER;
    private static final String PASS;

    static {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter login");
        USER = scanner.nextLine();
        System.out.println("Enter password");
        PASS = scanner.nextLine();
    }

    public static void main(String[] args) throws Exception {

        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            try {
                ticketDao = new TicketDao(DB_URL, USER, PASS);
                System.out.println("Server connected to database");
                run();
            } catch (SQLException e) {
                System.out.println("Connection to database failed");
            }

        } catch (IOException e) {
            System.out.println("Some problems");
        } finally {
            selector.close();
        }
    }

    private static void initialization(MyTreeSet treeSet) {
        try {
            treeSet.addAll(ticketDao.select());
        } catch (SQLException e) {
            System.out.println("Problem with database");
        }

    }

    private static void run() throws IOException {
        MyTreeSet treeSet = new MyTreeSet();
        String ans = "";
        initialization(treeSet);

        while (true) {
            int count = selector.select();
            if (count == 0) {
                continue;
            }
            Set keySet = selector.selectedKeys();
            Iterator it = keySet.iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                it.remove();
                ByteBuffer buffer = ByteBuffer.allocate(65536);
                if (key.isAcceptable()) {
                    accept(key, treeSet);
                    break;
                }
                if (key.isReadable()) {
                    Data data = read(key, buffer);
                    if (data != null) {
                        try {
                            Integer userId = ticketDao.findUser(data.getLogin(), data.getPassword());
                            if (userId != null || data.getCommandName().equals("register")) {
                                Command command = searchCommand(data.getCommandName(), treeSet, userId);
                                ans = command.execute(data.getArguments());
                            } else {
                                ans = "Can't found this user";
                            }

                        } catch (SQLException e) {
                            if (e.getMessage().contains("too long")) {
                                ans = "Your data is too long";
                            } else {
                                if (e.getMessage().contains("duplicate")) {
                                    ans = "User with this login already exists";
                                }
                            }

                        }
                    }
                    break;
                }
                if (key.isWritable()) {
                    write(key, ans, buffer);
                    break;
                }
            }
        }
    }


    private static void accept(SelectionKey key, MyTreeSet treeSet) {
        SocketChannel client;
        try {
            client = serverSocketChannel.accept();
            client.configureBlocking(false);
            client.register(key.selector(), SelectionKey.OP_READ);
            System.out.println("Client connected");

        } catch (IOException e) {
            System.out.println("Client go out");
        }
    }

    private static Data read(SelectionKey key, ByteBuffer byteBuffer) {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            channel.read(byteBuffer);
            Data data = deserialize(byteBuffer);
            channel.configureBlocking(false);
            channel.register(key.selector(), SelectionKey.OP_WRITE);
            byteBuffer.clear();
            return data;
        } catch (IOException | ClassNotFoundException e) {
            try {
                channel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.out.println("Client go out");
        }
        return null;
    }

    private static void write(SelectionKey key, String ans, ByteBuffer byteBuffer) {
        SocketChannel channel = (SocketChannel) key.channel();
        byteBuffer.put(serialize(ans));
        byteBuffer.flip();
        try {
            channel.write(byteBuffer);
            channel.configureBlocking(false);
            channel.register(key.selector(), SelectionKey.OP_READ);
            byteBuffer.clear();
        } catch (IOException e) {
            try {
                channel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.out.println("Client go out");
        }
    }


    private static <T> T deserialize(ByteBuffer byteBuffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        T data = (T) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        byteBuffer.clear();
        return data;
    }

    public static Command searchCommand(String command, MyTreeSet treeSet, Integer userId) {
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


    private static byte[] serialize(String message) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(message);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("Serialize problem");
        }
        return null;
    }


}