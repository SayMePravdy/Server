package server;

import collection.MyTreeSet;
import commands.*;
import data.Data;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {
    //private static ByteBuffer byteBuffer = ByteBuffer.allocate(16384);
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
            run();
        } catch (IOException e) {
            System.out.println("Some problems");
        } finally {
            selector.close();
        }
    }

    private static void run() throws IOException {
        MyTreeSet treeSet = new MyTreeSet();
        String ans = "";
        ServerInput input;
        File file;
        boolean firstAccept = true;

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
                    file = accept(key, buffer);
                    if (file != null) {
                        if (firstAccept) {
                            input = new ServerInput(treeSet, file);
                            input.start();
                            File finalFile = file;
                            Runtime.getRuntime().addShutdownHook(new Thread() {
                                @Override
                                public void run() {
                                    try (FileWriter writer = new FileWriter(finalFile)) {
                                        treeSet.save(writer);
                                    } catch (IOException e) {
                                        System.out.println("File not found");
                                    }

                                }
                            });
                            firstAccept = false;
                        }

                    }
                    break;
                }
                if (key.isReadable()) {
                    Data data = read(key, buffer);
                    try {
                        Command command = searchCommand(data.getCommandName(), treeSet);
                        ans = command.execute(data.getArguments());
                    } catch (NullPointerException e) {
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

    private static File accept(SelectionKey key, ByteBuffer byteBuffer)  {
        SocketChannel client;
        try {
            client = serverSocketChannel.accept();
            client.read(byteBuffer);
            client.configureBlocking(false);
            client.register(key.selector(), SelectionKey.OP_READ);
            System.out.println("Client connected");
            File file = deserialize(byteBuffer);
            return file;
        } catch (IOException e) {
            System.out.println("Client go out");
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    private static Data read(SelectionKey key, ByteBuffer byteBuffer)  {
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
        T data;
        try {
            data = (T) objectInputStream.readObject();
        } catch (IOException e) {
            data = null;
        }
        byteArrayInputStream.close();
        objectInputStream.close();
        byteBuffer.clear();
        return data;
    }

    public static Command searchCommand(String command, MyTreeSet treeSet) {
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


    private static byte[] serialize(String message) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)){
            objectOutputStream.writeObject(message);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("Serialize problem");
        }
        return null;
    }


}