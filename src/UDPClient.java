import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient implements Runnable {
    private DatagramSocket socket;
    private InetAddress address;
    private int serverPort;
    private String clientId;

    public UDPClient(String clientId, String serverAddress, int serverPort) {
        this.clientId = clientId;
        this.serverPort = serverPort;
        try {
            this.socket = new DatagramSocket();
            this.address = InetAddress.getByName(serverAddress);
            System.out.println(clientId + " initialized.");
        } catch (Exception e) {
            System.out.println("Error initializing " + clientId + ": " + e.getMessage());
        }
    }

    private void sendMessage(String message) {
        try {
            byte[] buf = (clientId + ": " + message).getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, serverPort);
            socket.send(packet);
            System.out.println("Message sent: " + message);
        } catch (Exception e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    private void listenForMessages() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            while (true) {
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                if (!received.startsWith(clientId)) { // Ignore own messages
                    System.out.println("Received: " + received);
                }
            }
        } catch (Exception e) {
            System.out.println("Error receiving message: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        Thread listenerThread = new Thread(this::listenForMessages);
        listenerThread.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String inputLine;
            while (true) {
                System.out.println(clientId + " - Enter your message (type 'exit' to quit):");
                inputLine = reader.readLine();
                if ("exit".equalsIgnoreCase(inputLine)) {
                    break;
                }
                sendMessage(inputLine);
            }
        } catch (Exception e) {
            System.out.println(clientId + " error reading from keyboard: " + e.getMessage());
        } finally {
            socket.close();
            System.out.println(clientId + " closed its connection.");
        }
    }


    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java UDPClient <Client ID> <Server Address> <Server Port>");
            return;
        }
        String clientId = args[0];
        String serverAddress = args[1];
        int serverPort = Integer.parseInt(args[2]);
        new Thread(new UDPClient(clientId, serverAddress, serverPort)).start();
    }
}
