package swiftx.transfer.receiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// This class defines
public class FileReceiverServer {
    public static void start(int port) throws IOException {
        try (
                ServerSocket serverSocket = new ServerSocket(port);
        ) {
            System.out.println("Listening on port " + port);


            // Initializing thread
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new FileReceiveTask(socket)).start();
            }
        }

    }
}
