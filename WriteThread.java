import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Cretu Calinn on 7/9/2016.
 */
public class WriteThread implements Runnable{

    private ObjectOutputStream objectOutputStream ;
    private Socket socket;
    private ClientClass client;

    WriteThread(Socket socket, ClientClass client)
    {
        this.client = client;
        this.socket = socket;
    }
    @Override
    public void run() {

        try {

            String message;
            Scanner scan = new Scanner (System.in);
            while (!client.isEndGame())
            {

                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                message = scan.nextLine();
                if (message.equals("STAND"))
                {
                    client.setEndGame(true);
                }
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
