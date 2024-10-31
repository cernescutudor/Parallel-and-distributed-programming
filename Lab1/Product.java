package Lab1;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Product {
    private final String name;
    private int quantity;
    private final int price;
    private int soldQuantity = 0;
    private final Lock mutex;

    public Product(String name, int quantity, int price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.mutex = new ReentrantLock(false);
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public Lock getMutex() {
        return mutex;
    }

}