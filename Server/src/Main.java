import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {

    static  ExecutorService executeSocket = Executors.newFixedThreadPool(2);


    //static ExecutorService executeIt = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        //ConnectMySQL.send("insert into users (`email`,`password`,`name`,`surname`,`purchased`,`date`,`spent`,`bonus`,`role`,`activated`,`code`) values ('test','123123','test','test',0,date('2020-11-01'),0,0, 'user', 0,11111);");

        //System.out.println(ConnectMySQL.get("SELECT name, surname FROM users WHERE `name` = 'testss';"));
        try {
            ServerSocket server = new ServerSocket(3345);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Server socket created, command console reader for listen to server commands");
            // стартуем цикл при условии что серверный сокет не закрыт

            executeSocket.execute(new AcceptSocket(server));

            while (!server.isClosed()) {
                // проверяем поступившие комманды из консоли сервера если такие
                // были
                if (br.ready()) {
                    System.out.println("Main Server found any messages in channel, let's look at them.");
                    // если команда - quit то инициализируем закрытие сервера и
                    // выход из цикла раздачии нитей монопоточных серверов
                    String serverCommand = br.readLine();
                    if (serverCommand.equalsIgnoreCase("quit")) {
                        System.out.println("Main Server initiate exiting...");
                        server.close();
                        break;
                    }
                }

                // если комманд от сервера нет то становимся в ожидание
                // подключения к сокету общения под именем - "clientDialog" на
                // серверной стороне
                //Socket client = server.accept();

                // после получения запроса на подключение сервер создаёт сокет
                // для общения с клиентом и отправляет его в отдельную нить
                // в Runnable(при необходимости можно создать Callable)
                // монопоточную нить = сервер - MonoThreadClientHandler и тот
                // продолжает общение от лица сервера
                //executeIt.execute(new MonoThreadClientHandler(client));
                //System.out.print("Connection accepted.");
            }

            // закрытие пула нитей после завершения работы всех нитей
            //executeIt.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            executeSocket.shutdown();
        }
    }
}