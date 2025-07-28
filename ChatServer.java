import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private static final int PORT = 12345;
    private static Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private String userName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Enter your name: ");
                userName = in.readLine();
                broadcast(userName + " has joined the chat!");

                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.equalsIgnoreCase("exit")) break;
                    broadcast(userName + ": " + msg);
                }
            } catch (IOException e) {
                System.out.println("Connection error with " + userName);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {}
                clients.remove(this);
                broadcast(userName + " has left the chat.");
            }
        }

        void broadcast(String message) {
            for (ClientHandler client : clients) {
                client.out.println(message);
            }
        }
    }
}
