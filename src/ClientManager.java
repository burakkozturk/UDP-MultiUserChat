public class ClientManager {
    public static void main(String[] args) {
        int numClients = Integer.parseInt(args[0]);
        for (int i = 1; i <= numClients; i++) {
            String clientId = "Client" + i;
            UDPClient client = new UDPClient(clientId, "localhost", 4445);
            Thread thread = new Thread(client);  // UDPClient sınıfı Runnable interface'ini implemente etmelidir
            thread.start();  // Thread başlatılıyor
        }
    }
}
