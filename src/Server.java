import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private Set<PrintWriter> clients = new HashSet<>();

    public static void main(String[] args) throws IOException {
        new Server().startServer(8000);
    }

    public void startServer(int port) throws IOException {
        System.out.println("Starting server on port " + port);

        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket socket = serverSocket.accept();
            String nickname = "User"; // 示例，实际中应由客户端传递
            new Thread(new ClientHandler(socket, nickname)).start();
        }
    }

    class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ClientHandler(Socket socket, String nickname) {
            this.socket = socket;
            this.nickname = nickname;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                out = new PrintWriter(socket.getOutputStream(), true);
                clients.add(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    System.out.println("Received " + message);
                    for (PrintWriter writer : clients) {
                        if (writer != out) {
                            writer.println(message);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + nickname);
            } finally {
                clients.remove(out);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 新增方法，用于服务器向所有客户端发送自定义消息
        public void sendMessageToAll(String message) {
            for (PrintWriter writer : clients) {
                writer.println("[SERVER]: " + message);
            }
        }
    }
}