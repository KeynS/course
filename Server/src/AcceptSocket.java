import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AcceptSocket implements Runnable{

    private ServerSocket server;
    static ExecutorService executeIt = Executors.newFixedThreadPool(2);

    AcceptSocket(ServerSocket server){
        this.server = server;
    }

    @Override
    public void run() {
        try {
            while (!server.isClosed()) {
            Socket client = server.accept();
            executeIt.execute(new MonoThreadClientHandler(client));
            System.out.println("Connection accepted.");
            }
            // закрытие пула нитей после завершения работы всех нитей
            executeIt.shutdown();
        } catch (IOException ignored) {

        }

    }

    //Socket client = server.accept();
    //executeIt.execute(new MonoThreadClientHandler(client));
    //                System.out.print("Connection accepted.");
}
