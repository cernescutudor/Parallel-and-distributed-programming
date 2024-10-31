package Lab1;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.BufferedReader;

public class Inventory {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private int totalMoney = 0;
    final Lock moneyLock = new ReentrantLock(false);
    final Lock billLock = new ReentrantLock(false);

    private ArrayList<Bill> processedBills = new ArrayList<>();
    private ArrayList<Product> products = new ArrayList<>();

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void addMoney(int money) {
        moneyLock.lock();
        try {
            totalMoney += money;
        } finally {
            moneyLock.unlock();
        }
    }

    public void addBill(Bill bill) {
        billLock.lock();
        try {
            processedBills.add(bill);
        } finally {
            billLock.unlock();
        }
    }

    public synchronized boolean processBill(Bill bill) {
        try {
            Thread.sleep((long) (Math.random() * 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Product> productList = new ArrayList<>(bill.getProducts().keySet());

        productList.sort((p1, p2) -> Integer.compare(System.identityHashCode(p1), System.identityHashCode(p2)));
        for (Product product : productList) {
            Lock mutex = product.getMutex();
            mutex.lock();
        }

        try {
            for (Map.Entry<Product, Integer> entry : bill.getProducts().entrySet()) {
                Product product = entry.getKey();
                int quantitySold = entry.getValue();
                if (product.getSoldQuantity() + quantitySold > product.getQuantity()) {
                    return false;
                }

            }
            for (Map.Entry<Product, Integer> entry : bill.getProducts().entrySet()) {
                Product product = entry.getKey();
                int quantitySold = entry.getValue();
                product.setSoldQuantity(product.getSoldQuantity() + quantitySold);
            }

            addBill(bill);
            addMoney(bill.getTotalPrice());
            return true;

        } finally {
            for (Product product : productList) {
                product.getMutex().unlock();
            }
        }

    }

    public synchronized void integrityCheck() {

        try {
            Thread.sleep((long) (Math.random() * 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(ANSI_CYAN + "Performing periodic integrity check...");
            for (Product product : products) {
                product.getMutex().lock();
            }
            moneyLock.lock();
            billLock.lock();
            int totalSold = 0;
            for (Bill bill : processedBills) {
                totalSold += bill.getTotalPrice();
            }
            if (totalSold == totalMoney) {
                System.out.println("Integrity check: All bills processed correctly.");
            } else {
                System.out.println("Integrity check: Error: Money is missing.");
            }

        } finally {
            moneyLock.unlock();
            billLock.unlock();

            for (Product product : products) {
                product.getMutex().unlock();
            }
        }
    }

    public void finalCheck() {
        int totalSold = 0;
        for (Bill bill : processedBills) {
            totalSold += bill.getTotalPrice();
        }
        if (totalSold == totalMoney) {
            System.out.println("All bills processed correctly.");
        } else {
            System.out.println("Error: Money is missing.");
        }
    }

    public void readProductsFromFile(String filename) {
        try {
            products = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            int numProducts = Integer.parseInt(reader.readLine());
            System.out.println("Number of products: " + numProducts);
            for (int i = 0; i < numProducts; i++) {
                String line = reader.readLine();
                String[] parts = line.split(",");
                String name = parts[0];
                int price = Integer.parseInt(parts[1]);
                int quantity = Integer.parseInt(parts[2]);
                Product product = new Product(name, quantity, price);
                products.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
