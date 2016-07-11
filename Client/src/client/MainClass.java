package client;

import client.ClientClass;

import java.io.IOException;

public class MainClass {
    public static void main (String[] argc)
    {
        ClientClass client = new ClientClass("192.168.1.1", 69); // momentan ar trebui sa fim conectati
        client.play();
    }
}
