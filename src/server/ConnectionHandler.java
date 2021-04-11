package server;

import collection.MyTreeSet;
import dao.TicketDao;

import java.nio.channels.SocketChannel;

public class ConnectionHandler implements Runnable{
    private TicketDao ticketDao;
    private SocketChannel socket;
    private MyTreeSet treeSet;

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    private boolean stop = false;

    public ConnectionHandler(TicketDao ticketDao, SocketChannel socket, MyTreeSet treeSet) {
        this.ticketDao = ticketDao;
        this.socket = socket;
        this.treeSet = treeSet;
    }


    @Override
    public void run() {
        while (!stop) {
            GetDataThread getDataThread = new GetDataThread(ticketDao, treeSet, socket, this);
            if (ticketDao.getConnection() == null) break;
            getDataThread.start();
            try {
                getDataThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (ticketDao.getConnection() == null) break;
        }
    }
}
