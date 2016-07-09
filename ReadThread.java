import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Cretu Calinn on 7/9/2016.
 */

public class ReadThread implements Runnable{


    private Socket socket;
    private ObjectInputStream objectInputStream ;
    private ClientClass client;

        ReadThread(Socket socket, ClientClass client)
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

                objectInputStream = new ObjectInputStream(socket.getInputStream()); // aici se opreste
                message = (String) objectInputStream.readObject();
                System.out.println("Run From Read Inside while");
                System.out.println(message);
                if (message.equals("BUSTED"))
                {
                    client.setEndGame(true);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        }

    }

