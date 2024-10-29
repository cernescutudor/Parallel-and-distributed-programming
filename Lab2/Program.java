package Lab2;


public class Program {

    public static void main(String[] args) {
        DotProduct dp = new DotProduct();
        Thread producer = new Thread(() -> dp.Producer());
        Thread consumer = new Thread(() -> dp.Consumer());
        producer.start();
        consumer.start();
    }
    
}
