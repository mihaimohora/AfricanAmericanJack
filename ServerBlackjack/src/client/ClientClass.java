package client;

import deck.Card;

import java.io.*;
import java.net.*;
import java.io.*;
import java.util.Scanner;


public class ClientClass{

    private Socket socket; // pentru conectare la server
    // informatii pt socket
    private int port ;
    private String ip;
    private ObjectInputStream input; // pentru a primi mesase de la server
    private ObjectOutputStream output; // pentru a trimite mesaje la server

    private Object messageReceived;
    private String messageSent;


    ClientClass(String ipToGet, int portToGet)
    {
        port = portToGet;
        ip = ipToGet;
    }

    public void connect()
    {
        try {
            socket = new Socket(ip, port);
            System.out.println("Server connection done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUpStreams() {

        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("The streams are set");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReading()
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    messageReceived = input.readObject();
                    while(messageReceived != null)
                    {
                        if (messageReceived instanceof Card)
                        {
                            System.out.println("You were dealt" + (Card) messageReceived);
                        }
                        if (messageReceived instanceof Integer)
                        {
                            System.out.println("Dealer has so far the total:" + (Integer) messageReceived);
                        }
                        if (messageReceived instanceof String)
                        {
                            String message = messageReceived.toString();
                            if (message.equals("BUSTED"))
                            {
                                System.out.println("Bust! You Lost");
                                break;
                            }
                            if (message.equals("WIN") || message.equals("LOSE") || message.equals("DRAW"))
                            {
                                System.out.println(message);
                                break;
                            }

                            if(message.equals("Enter option: HIT/STAND"))
                            {
                                System.out.println(message);
                            }
                        }
                        messageReceived = input.readObject();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void writeMessage()
    {
        Scanner input = new Scanner(System.in);
        while(true)
        {
            if(messageReceived instanceof String)
            {
                String message = messageReceived.toString();
                if(message.equals("It's your turn! HIT or STAND"))
                {
                    messageSent = input.next();
                    try {
                        output.writeObject(messageSent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(message.equals("BUSTED") || message.equals("Please wait for the results"))
                    break;

            }
        }
    }

    public void play()
    {
        connect();
        setUpStreams();
        startReading();
        writeMessage();
    }

    public void run() {
    }
}
