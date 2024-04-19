import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
    private DatagramSocket socket;
    private List<String> clientAddresses = new ArrayList<>();
    private byte[] buf = new byte[1024];

    public UDPServer(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (IOException e) {
            System.out.println("Socket could not be opened, or the socket could not bind to the specified local port.");
        }
    }

    public void listen() {
        System.out.println("Server is listening...");
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received message: " + received);

                // Kayıt veya güncelleme
                String clientAddress = packet.getAddress().toString() + ":" + packet.getPort();
                if (!clientAddresses.contains(clientAddress)) {
                    clientAddresses.add(clientAddress);
                }

                // Mesajı diğer tüm istemcilere gönder
                for (String addr : clientAddresses) {
                    String[] splitAddr = addr.split(":");
                    InetAddress address = InetAddress.getByName(splitAddr[0].replace("/", ""));
                    int port = Integer.parseInt(splitAddr[1]);
                    if (!clientAddress.equals(addr)) { // Mesajı gönderen istemciye gönderme
                        packet = new DatagramPacket(packet.getData(), packet.getLength(), address, port);
                        socket.send(packet);
                    }
                }
            } catch (IOException e) {
                System.out.println("IO exception occurred.");
                break;
            }
        }
        socket.close();
    }

    public static void main(String[] args) {
        int port = 4445;
        UDPServer server = new UDPServer(port);
        server.listen();
    }
}
