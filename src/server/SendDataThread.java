package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SendDataThread extends Thread {

    private String ans;
    private SocketChannel socket;

    public SendDataThread(String ans, SocketChannel socketChannel) {
        this.ans = ans;
        this.socket = socketChannel;
    }

    @Override
    public void run() {
        try{
            ByteBuffer buffer = ByteBuffer.allocate(65536);
            buffer.put(serialize(ans));
            buffer.flip();
            socket.write(buffer);
            buffer.clear();
            //socket.close();
        } catch (IOException e) {
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
