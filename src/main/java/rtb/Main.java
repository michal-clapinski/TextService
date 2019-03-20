package rtb;


public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Missing argument");
            System.exit(1);
        }

        System.out.println("Starting preprocessing, please wait...");
        Server server = new Server(args[0]);
        server.StartServing();

        System.out.println("Server started, press Enter to stop");
        System.in.read();

        server.StopServing();
        System.out.println("Server stopped");
    }
}
