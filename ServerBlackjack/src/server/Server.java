package server;

import deck.Card;
import deck.Deck;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
    private ServerSocket serverSocket;
    private Socket socket;

    private ExecutorService executorService;

    private int port;

    private ArrayList<Serverthread> threads;
    private Player dealer;

    private volatile boolean gameEnded;
    Deck deck;

    public Player getDealer()
    {
        return dealer;
    }

    public void setDealer(Player dealer)
    {
        this.dealer = dealer;
    }

    public boolean getGameEnded()
    {
        return gameEnded;
    }

    public Server()
    {
        gameEnded = false;
        port = 9797;
        threads = new ArrayList<Serverthread>();

        executorService = Executors.newCachedThreadPool();

        try {
            serverSocket = new ServerSocket(port); // pornire server
            System.out.println("Server started. Waiting for players");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start()
    {
        for(int i=0; i<3; i++) //ne conectam toti si jucam
        {
            try {

                socket = serverSocket.accept(); // apel blocant. asteptam client sa se conecteze
                threads.add(new Serverthread(socket, this));
                executorService.execute(threads.get(threads.size()-1));
                System.out.println("Player " + (i + 1) + " connected");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Game started: ");

        sendCards();
        clientsPlay();

        System.out.println("all players finished playing");

        dealerPlay();

        System.out.println("Dealer's hand :" + dealer.getHand());
        System.out.println("Dealer's total:" + dealer.getTotal());

        sendResults();
        gameEnded = true;
    }

    public void sendCards() //initializarea jocului. trimitere 2 carti catre clienti si dealer
    {
        deck = new Deck();
        Card card1;
        Card card2;

        System.out.println("Sending their cards");
        for(int i = 0; i<threads.size(); i++){
            card1 = deck.getCard();
            card2 = deck.getCard();
            try {

                Player player = new Player(card1,card2);
                threads.get(i).setPlayer(player);
                threads.get(i).getObjectOutputStream().writeObject("You were dealt: ");
                threads.get(i).getObjectOutputStream().flush();

                threads.get(i).getObjectOutputStream().writeObject(player.getHand().get(0));
                threads.get(i).getObjectOutputStream().flush();

                threads.get(i).getObjectOutputStream().writeObject(player.getHand().get(1));
                threads.get(i).getObjectOutputStream().flush();

                threads.get(i).getObjectOutputStream().writeObject("Your sums are: ");
                threads.get(i).getObjectOutputStream().flush();

                for(int j=0;j<player.getTotal().size(); j++)
                {
                    threads.get(i).getObjectOutputStream().writeObject(player.getTotal().get(j));
                    threads.get(i).getObjectOutputStream().flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        card1 = deck.getCard();
        card2 = deck.getCard();

        dealer = new Player(card1, card2);

        System.out.println("Sending dealer card");
        for(int i = 0; i< threads.size(); i++)
        {
            try {
                threads.get(i).getObjectOutputStream().writeObject("Dealer has: ");
                threads.get(i).getObjectOutputStream().flush();

                threads.get(i).getObjectOutputStream().writeObject(dealer.getHand().get(0));
                threads.get(i).getObjectOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void clientsPlay() //clientii joaca pe rand
    {
        int i = 0;
        threads.get(i).setWait(false);
        System.out.println("Player " + (i + 1) + "'s turn") ;
        while(i < threads.size() - 1 || !threads.get(threads.size() - 1).getFinished())
        {
            if ( i < threads.size() - 1 && threads.get(i).getFinished())
            {
                i++;
                threads.get(i).setWait(false);
                System.out.println("Player " + (i + 1) + "'s turn") ;
            }
        }
    }

    public void dealerPlay() //dealer-ul isi joaca mana
    {

        while(!dealer.getTotal().isEmpty() && dealer.getTotal().get(0) < 17 )
        {
            dealer.addCard(deck.getCard());
        }

    }

    public void sendResults() // trimitem rezultatele jucatorilor
    {
        for(int i = 0; i<threads.size(); i++)
        {
            if(!threads.get(i).getPlayer().getTotal().isEmpty())
            {
                if(!dealer.getTotal().isEmpty())
                {

                    int dealerTotal = dealer.getTotal().get(dealer.getTotal().size() - 1);
                    int playerTotal = threads.get(i).getPlayer().getTotal().get(threads.get(i).getPlayer().getTotal().size() - 1);
                    try {
                        threads.get(i).getObjectOutputStream().writeObject("Dealer has: ");
                        threads.get(i).getObjectOutputStream().flush();

                        threads.get(i).getObjectOutputStream().writeObject(dealerTotal);
                        threads.get(i).getObjectOutputStream().flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(dealerTotal > playerTotal)
                    {
                        try {
                            threads.get(i).getObjectOutputStream().writeObject("You Lost");
                            threads.get(i).getObjectOutputStream().flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (dealerTotal == playerTotal) {
                        try {
                            threads.get(i).getObjectOutputStream().writeObject("Draw");
                            threads.get(i).getObjectOutputStream().flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        try {
                            threads.get(i).getObjectOutputStream().writeObject("You Win");
                            threads.get(i).getObjectOutputStream().flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    try {
                        threads.get(i).getObjectOutputStream().writeObject("Dealer BUSTED! You Win!");
                        threads.get(i).getObjectOutputStream().flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
