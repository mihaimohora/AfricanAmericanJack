package server;

import deck.Card;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Serverthread implements Runnable{

    private Socket socketClient;
    private Server server;

    private Player player;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private volatile boolean wait;

    private boolean finished;

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean getFinished() {
        return finished;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    Serverthread(Socket socketClient, Server server){

        this.socketClient = socketClient;
        this.server = server;
        wait = true;
        finished = false;
    }
    @Override
    public void run() {
        try {
            String option;
            objectOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(socketClient.getInputStream());
            server.setDealer(new Player(server.deck.getCard(), server.deck.getCard()));
            objectOutputStream.writeObject("Dealer has:" + server.getDealer().getHand().get(0)); // AICI nu merge prea bine. Probabil ca pune in stream dar nu citeste clientul.
            System.out.print("dupa dealer has");//Trimit doar prima carte a dealerului.
            objectOutputStream.flush();
            while(!server.getGameEnded()){
                if(!finished && !wait) {
                    objectOutputStream.writeObject("Enter option: HIT/STAND");
                    objectOutputStream.flush();
                    option = (String) objectInputStream.readObject();

                    if (option.equals("HIT")) {
                        Card newCard = server.deck.getCard();
                        player.addCard(newCard);
                        objectOutputStream.writeObject("You drew" + player.getHand().get(player.getHand().size() - 1));
                        objectOutputStream.flush();
                        if (player.getTotal().isEmpty()) {
                            objectOutputStream.writeObject("BUSTED");
                            objectOutputStream.flush();
                            finished = true;
                        }
                        else
                        {
                            objectOutputStream.writeObject("Dealer has:" + server.getDealer().getHand().get(0)); //Trimit doar prima carte a dealerului.
                            objectOutputStream.flush();

                            objectOutputStream.writeObject("Your hand:" + player.getHand());
                            objectOutputStream.writeObject("Your total:" + player.getTotal());
                        }
                    }

                    if (option.equals("STAND")) {
                        finished = true;
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public ObjectOutputStream getObjectOutputStream(){
        return objectOutputStream;
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
