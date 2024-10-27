import java.util.Map;
import java.util.HashMap;

public class Bill {
    private Map<Product, Integer> products = new HashMap<>();
    private int totalPrice = 0;

    public void addProduct(Product product, int quantity) {
        if (products.containsKey(product)) {
            quantity += products.get(product);
        }
        else {
            products.put(product, quantity);
        }

        totalPrice += product.getPrice() * quantity;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

}
