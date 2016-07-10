package client;

import java.io.IOException;

public class MainClass {
    public static void main (String[] argc) throws IOException, ClassNotFoundException {

        ClientClass client = new ClientClass("127.0.0.1", 80); // momentan ar trebui sa fim conectati
        client.run();
        client.play();
    }
}