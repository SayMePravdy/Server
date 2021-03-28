package server;

import collection.MyTreeSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ServerInput extends Thread{
    private MyTreeSet myTreeSet;
    private File file;


    public ServerInput(MyTreeSet myTreeSet, File file) {
        this.myTreeSet = myTreeSet;
        this.file = file;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String command = scanner.nextLine();
            if (command.equals("save")) {
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    myTreeSet.save(fileWriter);
                    System.out.println("Collection is saved to " + file);
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Command \"" + command + "\" doesn't exists");
            }
        }
    }
}
