public class Program {


    public static void NaiveMultiplication(int length, int noThreads) {
        int[] A = new int[length];
        int[] B = new int[length];
        for (int i = 0; i < length; i++) {
            A[i] = (int) (Math.random() * 100);
            B[i] = (int) (Math.random() * 100);
        }

        NaiveMultiplication nm = new NaiveMultiplication(A, B);
        long startTime = System.currentTimeMillis();
        nm.multiply();
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken for serial multiplication: " + (endTime - startTime) + "ms");

        NaiveMultiplication nm2 = new NaiveMultiplication(A, B);
        Thread[] threads = new Thread[noThreads];
        startTime = System.currentTimeMillis();

        for (int i = 0; i < noThreads; i++) {
            int threadIndex = i;
            threads[i] = new Thread(() -> {
                nm2.multiplyParallel(threadIndex, noThreads);
            });
            threads[i].start();
        }
        for (int i = 0; i < noThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        endTime = System.currentTimeMillis();
        System.out.println("Time taken for parallel multiplication: " + (endTime - startTime) + "ms");

    }
    public static void main(String[] args) {
        
        NaiveMultiplication(100000, 10);
        
    }

}
