import com.mysql.jdbc.* ;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import javax.mail.MessagingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MonoThreadClientHandler implements Runnable {

    private final Socket clientDialog;
    DataOutputStream oos;
    DataInputStream ois;
    private String accEmail ="";

    public MonoThreadClientHandler(Socket client) throws IOException {
        clientDialog = client;
        oos = new DataOutputStream(clientDialog.getOutputStream());
        ois = new DataInputStream(clientDialog.getInputStream());
        System.out.println("New client connected");
    }

    public void send(String message) {
        try {
        oos.writeUTF(message);
        oos.flush();
         } catch (IOException e) {}
    }

    public String get(){
        try {
            return ois.readUTF();
        } catch (IOException e) {
            return null;
        }
    }

    void registration(){
        try{
            String surname = ois.readUTF();
            String name = ois.readUTF();
            String telephone = ois.readUTF();
            String email = ois.readUTF();
            String password = ois.readUTF();
            String role = "user";
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            String date = sqlDate.toString();

            String str = ConnectMySQL.get("SELECT id FROM users WHERE email = '" + email + "';");
            if(str.equals("error")) {
                System.out.println("Ошибка SQL");
                oos.writeUTF("SQLError");
                oos.flush();
                return;
            }
            else if(str.length() != 0) {
                System.out.println("такая почта уже есть");
                oos.writeUTF("code#1");
                oos.flush();
                return;
            }


            str = ConnectMySQL.get("SELECT id FROM users WHERE telephone = '" + telephone + "';");
            if(str.equals("error")) {
                System.out.println("Ошибка SQL");
                oos.writeUTF("SQLError");
                oos.flush();
                return;
            }
            else if(str.length() != 0) {
                System.out.println("такой телефон уже есть");
                oos.writeUTF("code#2");
                oos.flush();
                return;
            }
            int codeRand = (int) (1000 + Math.random()*8999);
            if(!ConnectMySQL.send("INSERT INTO `database`.`users` (`email`, `password`, `name`, `surname`, `telephone`, `date`, `role`, `code`) VALUES ('"+ email +"', '"+password+"', '"+name+"', '"+surname+"', '"+telephone+"', '"+date+"', '"+role+"', "+codeRand+");")){
                System.out.println("Ошибка SQL");
                oos.writeUTF("SQLError");
                oos.flush();
                return;
            } else{
                accEmail = email;
                System.out.println("Успешная регистрация");
                String code = Integer.toString(codeRand);
                oos.writeUTF("code#3");
                oos.flush();
                JavaMailUtil.sendMail(email, code, name);
                return;
            }

        } catch(IOException e) {
            System.out.println("Ошибка при отправке");
        } catch (MessagingException e) {
            System.out.println("Ошибка при отправке сообщения на почту");
            e.printStackTrace();
        }
    }

    void authorization(){
        try {
            String email = ois.readUTF();
            accEmail = email;
            String password = ois.readUTF();
            String str = ConnectMySQL.get("SELECT email,name,surname,telephone,purchased,date,spent,bonus,role,activated FROM users Where email = '" + email + "' AND password = '" + password + "';");
            if(str.equals("error")) {
                oos.writeUTF("code#1");
                oos.flush();
                return;
            }
            if(str.length() == 0) {
                oos.writeUTF("code#2");
                oos.flush();
                return;
            }
            oos.writeUTF(str);
            oos.flush();
            accEmail = email;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    void recode(){
        String email = "";
        try {
            email = ois.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("отправлено");
            oos.writeUTF("code#1"); //успешно
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str = ConnectMySQL.get("SELECT name, code FROM users Where email = '" + email + "';");
        String[] splitStr = str.split(";");
        String code = splitStr[0];
        String name = splitStr[1];
        try {
            JavaMailUtil.sendMail(email, code, name);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    void codeCheck() {
        String email = "";
        String code = "";
        try {
            email = ois.readUTF();
            code = ois.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str = ConnectMySQL.get("SELECT code FROM users WHERE email = '" + email + "';");
        String curCode = (str.split(";"))[0];
        System.out.println(code + " ----- " + curCode);
        try {
            if(code.equals(curCode)) {
                //sovpadaet
                if(!ConnectMySQL.send("UPDATE users SET activated = 1, code = NULL WHERE email = '" + email + "';")){
                    System.err.println("не удалось обновить бд");
                }
                oos.writeUTF("code#1");
            } else{
               //ne sovpadaet
              oos.writeUTF("code#2");
            }
            oos.flush();

        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int getFileNumber(){
        File dir = new File("D:/img"); //path указывает на директорию
        File[] arrFiles = dir.listFiles();
        String oldUrl = arrFiles[arrFiles.length - 1].toString();
        oldUrl = oldUrl.substring(oldUrl.lastIndexOf('\\'), oldUrl.length());
        int numb = Integer.parseInt(oldUrl.substring(1, oldUrl.lastIndexOf('.')));
        numb++;
        return numb;
    }

    private static String moveFile(String src) {
        Path result = null;
        String dest = "D:/img/" + getFileNumber() + "." + src.substring(src.lastIndexOf('.') + 1, src.length());
        try {
            Files.move(Paths.get(src), Paths.get(dest));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(result != null) {
            return src;
        }else{
            return dest;
        }
    }



    void createNewProduct(){
        /*
        Connect.send("createNewProduct");
        Connect.send(nameField.getText());
        Connect.send(priceField.getText());
        Connect.send(countField.getText());
        Connect.send(selectBox.getValue());
        Connect.send(descriptionArea.getText());
        Connect.send(urlField.getText());
        Connect.send("" + trendSelect.isSelected());
        */
        String name = get().replace(';', ',').replace('`', '"').replace('\'', '"');

        String price =  get();
        String count = get();
        String category = get();
        String description = get().replace(';', ',').replace('\'', '"').replace('`', '"');
        String url = get();
        String trend = get();

        File file = new File(url);
        if(url.length() == 0) {
            url = "D:/img/0.jpg";
        }
        else if(!file.exists()){
            url = "D:/img/0.jpg";
        }
        else {
            url = moveFile(url);
        }

        int categoryId = Integer.parseInt(ConnectMySQL.get("SELECT id FROM category where name = '"+category+"'").split(";")[0]);


/*
        System.out.println(name);
        System.out.println(price);
        System.out.println(count);
        System.out.println(description);
        System.out.println(categoryId);
        System.out.println(url);
        System.out.println(trend);
*/
        String s = "INSERT INTO product (name, price, count, description, trend, category_id, url) " +
                "VALUES ('"+name+"', "+price+", "+count+", '"+description+"', "+trend+", "+categoryId+", '"+url+"');";
        //System.out.println(s);
        if(!ConnectMySQL.send(s)){
            send("#code2"); // если не записало
            return;
        } else{
            s = "SELECT id FROM product WHERE name='"+name+"' AND price="+price+" AND count=" +
                    count+" AND description='"+description+"' AND category_id=" + categoryId +";";
            //System.out.println(s);
            String str = ConnectMySQL.get(s);
            int productId = Integer.parseInt((str.split(";")[0]));
            s = "INSERT INTO number_of_purchases (count, product_id) VALUES (0, "+productId+")";
            //System.out.println(s);
            if(!ConnectMySQL.send(s)){
                send("#code3"); //если в таблицу numb_of_purch не записало
                return;
            }
            send("#code1"); //успешно
        }
    }

    void deleteProducts(){ // отправлять надо так "id1,id2,id3,id4"
        String str = get();
        //str = str.substring(0, str.length() - 1);
        // (думал сначала сделать запрос "id1,id2,id3,id4,"
        // c запятой в конце, потом понял что
        // убрать ее изи на клиенте и так)
        String s = "DELETE FROM product WHERE id IN ("+str+");";
        System.out.println(s);
        if(!ConnectMySQL.send(s)) {
            send("#code2"); //не удалось удалить (но что-то могло удалиться
            return;
        }
        else {
            send("#code1"); //успешно(все удалилось)
            return;
        }
    }

    void viewAllProduct(){
        String str = ConnectMySQL.get("SELECT * FROM product");
        System.out.println(str);
        if(str.equals("error")) {
            send("#code1"); //ошибка
        } else send(str);
    }

    void editProduct(){
        String id = get();
        String name = get().replace(';', ',').replace('`', '"').replace('\'', '"');
        String price =  get();
        String count = get();
        String category = get();
        String description = get().replace(';', ',').replace('\'', '"').replace('`', '"');
        String url = get();
        String trend = get();

        File file = new File(url);
        if(url.length() == 0) {
            url = "D:/img/0.jpg";
        }
        else if(!file.exists()){
            url = "D:/img/0.jpg";
        }
        else if(url.contains("D:/img/")){
            //оставляем тот-же
        }
        else {
            url = moveFile(url);
        }

        int categoryId = Integer.parseInt(ConnectMySQL.get("SELECT id FROM category where name = '"+category+"'").split(";")[0]);

        String s = "update product set name='"+name+"', price="+price+", count = "+count+", description='"+description+"'" +
                ", trend = "+trend+", category_id = "+categoryId+", url = '"+url+"' where id = "+id+";";

        if(ConnectMySQL.send(s)) {
            send("#code1");// complete
        } else{
            send("#code2"); //err;
        }
    }

    void buy(){
        String userId;
        int length = Integer.parseInt(get());
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> oldMap = new HashMap<>();
        HashMap<String, Integer> deltaMap = new HashMap<>();
        int price = 0;
        for(int i = 0; i < length; i++){
            String key = get();
            String value = get();
            String price1 = get();
            price += Integer.parseInt(price1.substring(0, price1.indexOf("."))) * Integer.parseInt(value);
            map.put(key, value);
        }

        String card = get();
        String address = get();
        String calendar = get();
        String time = get() + ":" + get();

        System.out.println(card + " " + address + " " + calendar + " " + time + " " + price);

        String id_str = "";
        for(Map.Entry<String, String> t : map.entrySet()){
            id_str += t.getKey() + ", ";
        }
        id_str = id_str.substring(0, id_str.length() - 2);
        String s = "select id, count FROM product WHERE id IN ("+id_str+")";
        String[] rows = ConnectMySQL.get(s).split("\n");
        if(rows.length == 0)
            send("#code2");

        //  System.out.println(rows.toString());
        for(int i = 0; i <  rows.length; i++) {
            String[] row = rows[i].split(";");
            oldMap.put(row[0], row[1]);
        }

        int kol = 0;

        for(Map.Entry<String, String> t : oldMap.entrySet()){
            int delta = Integer.parseInt(t.getValue()) - Integer.parseInt(map.get(t.getKey()));
            if(delta < 0) kol++;
            deltaMap.put(t.getKey(), delta);
        }

        send(""+kol);
        if(kol != 0) {
            for (int i = 0; i < kol; i++) {
                for (Map.Entry<String, Integer> t : deltaMap.entrySet()) {
                    if (t.getValue() < 0) {
                        send(t.getKey());
                        send(oldMap.get(t.getKey()));
                    }
                }
            }
        } else {
            s = "select id from users where email = '"+accEmail+"';";
            userId = ConnectMySQL.get(s).split(";")[0];
            int plusPurchased = 0, plusSpent = price, plusBonus = 0;
            for (Map.Entry<String, Integer> t : deltaMap.entrySet()) {
                s = "update product set count = "+t.getValue()+" where id = "+t.getKey()+";";
                ConnectMySQL.send(s);
                s = "insert `log`(count, product_id, users_id) values ("+map.get(t.getKey())+", "+t.getKey()+", "+userId+")";
                System.out.println(s);
                ConnectMySQL.send(s);
                plusPurchased += Integer.parseInt(map.get(t.getKey()));

            }
            plusBonus = (int) (plusSpent * 0.1);
            //7777 7777 7777 7777
            //System.out.println(accEmail);

            String message = "buy:Здравствуйте, ваш заказ бы принят. Оплата: "; //ддля отправки по почте
            if(card.equals("none")) message+="при доставке наличными.\n";
            else message+="произведена картой("+card.substring(0, 4)+" **** ****"+ card.substring(14, card.length()) +").\n";
            message+="Доставка по адресу: " + address + ", на " + calendar + "\n";
            message+="Список товаров:\n";
            int count = 1;
            for (Map.Entry<String, String> t : map.entrySet()) {
                message+="№"+ count + ", Наименование товара: " +
                        ConnectMySQL.get("select name from product where id = "+t.getKey()+";").split(";")[0] +
                        ", количество: " + t.getValue() + "шт., цена(за шт.): " +
                        ConnectMySQL.get("select price from product where id = "+t.getKey()+";").split(";")[0] + "рублей.\n";
                count++;
            }

            for (Map.Entry<String, String> t : map.entrySet()) {
                s = "select count from number_of_purchases where product_id = "+t.getKey()+";";
                int oldCount = Integer.parseInt(ConnectMySQL.get(s).split(";")[0]);
                int tmp = Integer.parseInt(t.getValue()) + oldCount;
                s = "update number_of_purchases set count = "+tmp+" where product_id = "+t.getKey()+";";
                ConnectMySQL.send(s);
            }

            System.out.println(message);


            System.out.println(plusBonus + " " + plusPurchased+  ' ' + plusSpent );
            int oldBonus, oldPutc, oldSpent;
            s = "SELECT purchased, spent, bonus FROM users where id = "+userId+";";
            System.out.println(s);
            String[] sql = ConnectMySQL.get(s).split(";");
            oldPutc = Integer.parseInt(sql[0]);
            oldSpent = Integer.parseInt(sql[1]);
            oldBonus = Integer.parseInt(sql[2]);
            System.out.println(s);
            int temp1 = oldPutc + plusPurchased;
            int temp2 = oldSpent+plusSpent;
            int temp3 = oldBonus+plusBonus;
            s = "update users set purchased = "+temp1+", spent = "+temp2+", bonus = "+temp3+" where id = "+userId+";";
            ConnectMySQL.send(s);

            try {
                JavaMailUtil.sendMail(accEmail, message, "unknown");
            } catch (MessagingException e) {
                e.printStackTrace();
            }


        }
    }

    //
    void statistic(){
        int[] c = new int[5];

        for(int index = 0; index < 5; index++){
            String s = "Select `number_of_purchases`.count from `number_of_purchases`, `product` where " +
                      "`number_of_purchases`.product_id = `product`.id AND `product`.category_id = "+(index+1)+";";
            System.out.println(s);
            String message = ConnectMySQL.get(s);
            if(message.length() == 0) continue;
            String[] sql = message.split("\n");
            for(int i = 0; i < sql.length; i++){
                c[index] += Integer.parseInt(sql[i].split(";")[0]);
            }
        }

        for (int t: c) {
            send(""+t);
        }
    }

    void spros(){
        send(ConnectMySQL.get("Select product_id, count FROM number_of_purchases"));
    }

    void getUserLog(){
        String email = get();
        String s = "select `log`.product_id, `product`.name, `log`.count, `product`.price, `log`.date from log," +
                " product, users where `log`.product_id = `product`.id and `log`.users_id = " +
                "`users`.id and `users`.email = '"+email+"'";
        String sql = ConnectMySQL.get(s);
        send(sql);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = get();
                if(message == null) {
                    System.out.println("client disconnected");
                    break;
                }

                switch (message){
                    case "authorization":
                        authorization();
                        break;
                    case "registration":
                        registration();
                        break;
                    case "recode":
                        recode();
                        break;
                    case "codeCheck":
                        codeCheck();
                        break;
                    case "createNewProduct":
                        createNewProduct();
                        break;
                    case "deleteProducts":
                        deleteProducts();
                        break;
                    case "viewAllProduct":
                        viewAllProduct();
                        break;
                    case "editProduct":
                        editProduct();
                        break;
                    case "buy":
                        buy();
                        break;
                    case "statistic":
                        statistic();
                        break;
                    case "spros":
                        spros();
                        break;
                    case "getuserlog":
                        getUserLog();
                        break;
                    default:
                        System.out.println("client message = " + message);
                        break;
                }
//                send(ConnectMySQL.get("SELECT name, surname FROM users;"));
            }
        } finally {
            try { oos.close(); } catch (IOException e) { e.printStackTrace(); }
            try { ois.close(); } catch (IOException e) { e.printStackTrace(); }
            try { clientDialog.close(); } catch (IOException e) { e.printStackTrace(); }

            System.out.println("Closing connections & channels - DONE.");
        }
    }
}