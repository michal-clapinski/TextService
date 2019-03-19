package rtb;


public class Main {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.Start();
        } catch(Exception e) {
            // implement it better
        }
    }
}
