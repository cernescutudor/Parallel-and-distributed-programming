import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

public class Program {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) {

        int numBills = 100;
        String filename = "products.txt";
        int numThreads = 10;

        Inventory inventory = new Inventory();
        inventory.readProductsFromFile(filename);
        ArrayList<Product> products = inventory.getProducts();
        ArrayList<Bill> bills = new generateRandom().generateRandomBills(products, numBills);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                Thread.sleep((long) (Math.random() * 100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            inventory.integrityCheck();

        }, 300, 600, TimeUnit.MILLISECONDS);

        for (int i = 0; i < bills.size(); i++) {
            final int billIndex = i;
            Bill bill = bills.get(billIndex);
            executor.submit(() -> {
                int threadNumber = (int) (Thread.currentThread().getId() % numThreads);
                System.out.println(ANSI_WHITE+ "Thread " + threadNumber + " started processing bill " + billIndex);
                if (!inventory.processBill(bill)) {
                    System.out.println(ANSI_RED+"Failed to process bill " + billIndex + ": Not enough stock for some products.");
                }
                System.out.println(ANSI_GREEN+ "Thread " + threadNumber + " finished processing bill " + billIndex);
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        inventory.finalCheck();
        scheduler.shutdown();

    }

}
