package server;

import deck.Card;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Serverthread implements Runnable
{
    private Socket socketClient;
    private Server server;

    private Player player;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private volatile boolean wait;

    private volatile boolean finished;

    public ObjectOutputStream getObjectOutputStream()
    {

        return objectOutputStream;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }


    public boolean getFinished()
    {
        return finished;
    }

    public void setWait(boolean wait)
    {
        this.wait = wait;
    }

    Serverthread(Socket socketClient, Server server)
    {
        this.socketClient = socketClient;
        this.server = server;
        wait = true;
        finished = false;
    }

    @Override
    public void run()
    {
        try
        {
            String option;
            setUpStreams();

            while(!server.getGameEnded())
            {
                if(!finished && !wait)
                {
                    objectOutputStream.writeObject("Enter option: HIT/STAND");
                    objectOutputStream.flush();

                    option = (String) objectInputStream.readObject();
                    System.out.println(option);

                    if (option.equals("HIT"))
                    {
                        Card newCard = server.deck.getCard();
                        player.addCard(newCard);

                        objectOutputStream.writeObject("You drew:");
                        objectOutputStream.flush();

                        objectOutputStream.writeObject(player.getHand().get(player.getHand().size() - 1));
                        objectOutputStream.flush();

                        if (player.getTotal().isEmpty()) {
                            objectOutputStream.writeObject("BUSTED");
                            objectOutputStream.flush();
                            finished = true;
                        }
                        else
                        {
                            objectOutputStream.writeObject("Your hand:");

                            for(int i = 0; i<player.getHand().size(); i++)
                            {
                                objectOutputStream.writeObject(player.getHand().get(i));
                                objectOutputStream.flush();
                            }

                            objectOutputStream.writeObject("Your sums are:");
                            objectOutputStream.flush();

                            for (int i = 0; i<player.getTotal().size(); i++)
                            {
                                objectOutputStream.writeObject(player.getTotal().get(i));
                                objectOutputStream.flush();
                            }
                        }
                    }

                    if (option.equals("STAND"))
                    {
                        finished = true;
                        objectOutputStream.writeObject("Please wait for the results");
                        objectOutputStream.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        close();

    }
    public void setUpStreams()
    {
        try {
            objectOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(socketClient.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close(){
        try {
            objectInputStream.close();
            objectOutputStream.close();
            socketClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
