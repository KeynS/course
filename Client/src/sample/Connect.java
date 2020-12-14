package sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public final class Connect {

    private static DataOutputStream oos;
    private static DataInputStream ois;

    public static void send(String message){
        try {
            oos.writeUTF(message);
            oos.flush();
        } catch (Exception e) {
        }
    }


    public static String get(){
        try {
            return ois.readUTF();
        } catch (IOException e){
        }
        return null;
    }

    public static boolean Initialization(){
        try {
            Socket socket = new Socket("localhost", 3345);
            //System.out.println("Client connected to socket");
            //Thread.sleep(2000);
            oos = new DataOutputStream(socket.getOutputStream());
            ois = new DataInputStream(socket.getInputStream());
            return true;
            //System.out.println("Client oos & ois initialized");
        } catch (Exception e) {
            return false;
        }
    }

}