package server;

import collection.MyTreeSet;
import commands.*;
import data.Data;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Server {
    private static ByteBuffer byteBuffer = ByteBuffer.allocate(16384);
    private static ServerSocketChannel serverSocketChannel;
    private static Selector selector;
    private static int PORT = 3345;
    private final static int cntReconnect = 100;

    public static void main(String[] args) throws Exception {

        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            //for (int i = 0; i < cntReconnect; i++) {
                run();
               // System.out.println("Connection is closed");
            //}
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Some problems");
        } finally {
            selector.close();
        }
    }

    private static void accept(SelectionKey key) throws IOException {
        //ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(key.selector(), SelectionKey.OP_READ);
//        Socket client = serverSocketChannel.socket().accept();
//        SocketChannel channel = client.getChannel();
//        channel.configureBlocking(false);
//        channel.register(selector, SelectionKey.OP_READ);
    }

    private static Data read(SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel channel = (SocketChannel) key.channel();
        channel.read(byteBuffer);
        byteBuffer.flip();
        Data data = deserialize();
        channel.configureBlocking(false);
        channel.register(key.selector(), SelectionKey.OP_WRITE);
        byteBuffer.clear();
        return data;
    }

    private static void write(SelectionKey key, String ans) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        byteBuffer.put(serialize(ans));
        channel.write(byteBuffer);
        byteBuffer.flip();
        channel.configureBlocking(false);
        channel.register(key.selector(), SelectionKey.OP_READ);
        byteBuffer.clear();
    }


    private static void run() throws IOException, ClassNotFoundException {
        MyTreeSet treeSet = new MyTreeSet();
        NavigableSet<File> scripts = new TreeSet<>();
        String ans = "";

        while (true) {
            int count = selector.select();
            if (count == 0) {
//                //System.out.println("cnt = 0");
                continue;
            }
            Set keySet = selector.selectedKeys();
            Iterator it = keySet.iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                it.remove();
                if (key.isAcceptable()) {
                    accept(key);
                    break;
                }
                if (key.isReadable()) {
                    Data data = read(key);
                    try {
                        Command command = searchCommand(data.getCommandName(), treeSet, scripts);
                        ans = command.execute(data.getArguments());
                        //byteBuffer.flip();
                        //byteBuffer.put(serialize(ans));
                        //byteBuffer.flip();
                    } catch (NullPointerException e) {
                        //System.out.println("Connection is closed");
                    }
                    break;
                }
                if (key.isWritable()) {
                    write(key, ans);
                    break;
                }
            }
        }
    }


    private static Data deserialize() throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteBuffer.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Data data = (Data) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        byteBuffer.clear();
        return data;
    }

    public static Command searchCommand(String command, MyTreeSet treeSet, NavigableSet<File> scripts) {
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
                return new Command() {
                    @Override
                    public String execute(List<Object> arguments) {
                        return null;
                    }
                };
        }
    }


    private static byte[] serialize(String message) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(message);
        //dataOutputStream.writeUTF(message);
        byte[] buffer = byteArrayOutputStream.toByteArray();
        objectOutputStream.flush();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        return buffer;
    }


}