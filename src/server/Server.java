package server;

import collection.MyTreeSet;
import commands.*;
import data.Data;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Server {
    private static boolean hasClient = false;
    private static ObjectInputStream in;
    private static DataOutputStream out;
    private static ServerSocketChannel serverSocket;
    private static Selector selector;
    private final static int cntReconnect = 100;

    public static void main(String[] args) throws Exception {

        try {
            selector = Selector.open();
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);

            serverSocket.bind(new InetSocketAddress(3345));

            for (int i = 0; i < cntReconnect; i++) {
                run();
                System.out.println("Connection is closed");
            }

        } catch (IOException | NumberFormatException e) {
            //e.printStackTrace();
        }
    }

    private static void accept() {

    }

    private static void read() {

    }

    private static void write() {

    }

    public static void clientExit() {
        Server.hasClient = false;
    }

    private static void run() throws Exception {
        SocketChannel client = serverSocket.accept();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        //Socket client = socket.accept();


        System.out.println("Connection accepted");
        hasClient = true;

        in = new ObjectInputStream(client.socket().getInputStream());
        out = new DataOutputStream(client.socket().getOutputStream());
        MyTreeSet treeSet = new MyTreeSet();
        NavigableSet<File> scripts = new TreeSet<>();

        while (client.isConnected() && hasClient) {
            Data data = getData();
            try {
                Command command = searchCommand(data.getCommandName(), treeSet, scripts);
                command.execute(data.getArguments());
            } catch (NullPointerException e) {
                //System.out.println("Connection is closed");
                break;
            }
        }
    }

    public static Command searchCommand(String command, MyTreeSet treeSet, NavigableSet<File> scripts) throws Exception {
        switch (command) {
            case "help":
                return new Help("help");
            case "info":
                return new Info("info", treeSet);
            case "show":
                return new Show("show", treeSet);
            case "add":
                return new Add("add", treeSet);
            case "update":
                return new Update("update", treeSet);
            case "remove_by_id":
                return new RemoveById("remove_by_id", treeSet);
            case "clear":
                return new Clear("clear", treeSet);
            case "add_if_max":
                return new AddIfMax("add_if_max", treeSet);
            case "add_if_min":
                return new AddIfMin("add_if_min", treeSet);
            case "remove_greater":
                return new RemoveGreater("remove_greater", treeSet);
            case "sum_of_discount":
                return new SumOfDiscount("sum_of_discount", treeSet);
            case "max_by_comment":
                return new MaxByComment("max_by_comment", treeSet);
            case "print_unique_price":
                return new PrintUniquePrice("print_unique_price", treeSet);
            case "exit":
                return new Exit("exit");
            default:
                throw new Exception("");
        }
    }

    public static void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {

        }
    }

    public static Data getData() {
        try {
            return (Data) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
        }
        return null;
    }

}