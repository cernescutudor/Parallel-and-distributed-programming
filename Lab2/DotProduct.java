
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DotProduct {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    private int[] v1 = { 1, 5, 10, 20 };
    private int[] v2 = { 0, 2, 3, 4 };
    private int sum = 0;
    private int product;
    private boolean available = false;
    final Lock lock = new ReentrantLock();
    final Condition condition = lock.newCondition();

    public void Producer() {
        for (int i = 0; i < v1.length; i++) { 
            lock.lock();
            try {
                while (available) {
                    condition.await();
                }
                product = v1[i] * v2[i];
                available = true;
                System.out.println(ANSI_GREEN+ "Producer produced- " + product);
                condition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public void Consumer() {
        for (int i = 0; i < v1.length; i++) {
            lock.lock();
            try {
                while (!available) {
                    condition.await();
                }
                sum += product;
                available = false;
                System.out.println(ANSI_RED+ "Consumer consumed- " + product + " Sum: " + sum);
                condition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}