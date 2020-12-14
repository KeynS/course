package sample;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class User {
    public static String email;
    public static String name;
    public static String surname;
    public static String telephone;
    public static int purchased;
    public static String date;
    public static int spent;
    public static int bonus;
    public static String role;
    public static boolean activated;

    public User(String email, String name, String surname, String telephone, int purchased, String date, int spent, int bonus, String role, boolean activated) throws ParseException {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.telephone = telephone;
        this.purchased = purchased;
        this.date = new SimpleDateFormat("dd.MM.yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        this.spent = spent;
        this.bonus = bonus;
        this.role = role;
        this.activated = activated;
    }
}
