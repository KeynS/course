package sample;

public class Product {
    private int id;
    private String name;
    private double price;
    private int count;
    private String desc;
    private boolean trend;
    private int categoryId;
    private String url;

    public Product(int id, String name, double price, int count, String desc, boolean trend, int categoryId, String url) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.count = count;
        this.desc = desc;
        this.trend = trend;
        this.categoryId = categoryId;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", count=" + count +
                ", desc='" + desc + '\'' +
                ", trend=" + trend +
                ", categoryId=" + categoryId +
                ", url='" + url + '\'' +
                '}';
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isTrend() {
        return trend;
    }

    public void setTrend(boolean trend) {
        this.trend = trend;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
