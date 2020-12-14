package sample;

public class logProduct {
    private int id;
    private String name;
    private int value;
    private double cash;
    private int bonus;
    private String date;

    public logProduct(int id, String name, int value, double cash, String date) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.cash = value * cash;
        this.date = date;
        this.bonus = (int)(this.cash * 0.1f);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
