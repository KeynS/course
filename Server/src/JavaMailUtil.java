import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class JavaMailUtil {

    public static void sendMail(String rec, String code, String name) throws MessagingException {
        //System.out.println("prep");
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        String myAccountEmail = "bitmarket.info@gmail.com";
        String password = "bitmarket2020";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, password);
            }
        });

        Message message = prepareMessage(session, myAccountEmail, rec, code, name);

        Transport.send(message);
        System.out.println("complete");
    }

    private static Message prepareMessage(Session session, String myAccountEmail, String rec, String code, String name){
        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myAccountEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(rec));
            if(code.substring(0,4).equals("buy:")){
                message.setSubject("Спасибо за покупку в интернет магазине BitMarket");
                message.setText(code.substring(4, code.length()));
            }else{
                message.setSubject("Код активации аккаунта в BitMarket");
                message.setText("Здравствуйте, " + name + ".\nВы зарегистрировались в интернет магазине BitMarket.\n\n\nВаш код активации: " + code);
            }

            return message;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
