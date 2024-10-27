
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.ArrayList;

public class generateRandom {

    public void generateProductsToFile( String filename, int numProducts) {
        try {
            FileWriter fileWriter = new FileWriter(filename);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            Random rand = new Random();
            printWriter.println(numProducts);
            for (int i = 0; i < numProducts; i++) {
                String name = "Product" + i;
                int price = rand.nextInt(100)+1;
                int quantity = rand.nextInt(100)+1;
                printWriter.println(name + "," + price + "," + quantity);
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Bill> generateRandomBills( ArrayList<Product> products, int numBills) {
        ArrayList<Bill> bills = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < numBills; i++) {
            Bill bill = new Bill();
            int numProducts = rand.nextInt(5) + 1;
            for (int j = 0; j < numProducts; j++) {
                int productIndex = rand.nextInt(products.size());
                Product product = products.get(productIndex);
                int quantity = rand.nextInt(product.getQuantity())/2 + 1;
                bill.addProduct(product, quantity);
            }
            bills.add(bill);
        }
        return bills;
    }


}
