package server;

import collection.MyTreeSet;
import dao.TicketDao;
import data.Data;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Server {
    //private static ByteBuffer byteBuffer = ByteBuffer.allocate(16384);
    private static ServerSocketChannel serverSocketChannel;
    private static int PORT = 13345;
    private static TicketDao ticketDao;
    private final static int cntClients = 2;
    //private Semaphore semaphore;
    private static final String DB_URL = "jdbc:postgresql://localhost:9999/studs";
    /*
    for helios
    private static final String DB_URL = "jdbc:postgresql://pg:5432/studs";
     */
    private static final String USER;
    private static final String PASS;

    static {
        Console console = System.console();
        if (console != null) {
            USER = console.readLine("Enter login: ");
            PASS = new String(console.readPassword("Enter password: "));
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter login");
            USER = scanner.nextLine();
            System.out.println("Enter password");
            PASS = scanner.nextLine();
        }
    }

    public static void main(String[] args) {

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            try {
                ticketDao = new TicketDao(DB_URL, USER, PASS);
                System.out.println("Server connected to database");
                run();
            } catch (SQLException e) {
                System.out.println("Connection to database failed");
            }

        } catch (IOException e) {
            System.out.println("Some problems");
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
        initialization(treeSet);
        ExecutorService executor = Executors.newFixedThreadPool(cntClients);
        while (ticketDao.getConnection() != null) {
            SocketChannel socket = serverSocketChannel.accept();
            if (ticketDao.getConnection() == null) break;
            System.out.println("Client connected");
            executor.submit(new ConnectionHandler(ticketDao, socket, treeSet));
        }
        executor.shutdown();
    }


}