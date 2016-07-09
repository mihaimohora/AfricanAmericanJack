import java.io.IOException;


public class MainClass {
    public static void main (String[] argc) throws IOException, ClassNotFoundException
    {
        ClientClass client = new ClientClass("78.96.97.246", 80); // momentan ar trebui sa fim conectati
        client.playGame();
    }
}
