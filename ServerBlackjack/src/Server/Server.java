package server;

import deck.Card;
import deck.Deck;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;

    private ExecutorService executorService;

    private int port;
    ArrayList<Serverthread> threads;

    Deck deck;

    private Player dealer;
    private volatile boolean gameEnded;

    public Player getDealer() {
        return dealer;
    }

    public void setDealer(Player dealer) { this.dealer = dealer; }

    public boolean getGameEnded() {
        return gameEnded;
    }

    Server() {

        gameEnded = false;
        port = 80;
        threads = new ArrayList<Serverthread>();

        executorService = Executors.newCachedThreadPool();

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        for(int i=0; i<1; i++) //////// AM FACUT 1
        {

            try {

                socket = serverSocket.accept();
                threads.add(new Serverthread(socket, this));
                executorService.execute(threads.get(threads.size()-1));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        deck = new Deck();
        Card card1;
        Card card2;



        for(int i = 0; i<threads.size(); i++){
            card1 = deck.getCard();
            card2 = deck.getCard();
            try {

                Player player = new Player(card1,card2);
                threads.get(i).setPlayer(player);
                threads.get(i).getObjectOutputStream().writeObject("You were dealt: " + player.getHand()); // AICI CRAPA
                threads.get(i).getObjectOutputStream().writeObject("Your sums are:" + player.getTotal());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int i = 0;
        threads.get(i).setWait(false);
        while(i<threads.size() - 1){
            if (threads.get(i).getFinished())
            {
                i++;
                threads.get(i).setWait(false);
            }
        }


        card1 = deck.getCard();
        card2 = deck.getCard();
        Player dealer = new Player(card1, card2);
    }

}
