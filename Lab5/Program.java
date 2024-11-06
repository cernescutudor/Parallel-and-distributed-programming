public class Program {


    public static void NaiveMultiplication(int length, int noThreads) {
        long[] A = new long[length];
        long[] B = new long[length];
        for (int i = 0; i < length; i++) {
            A[i] = (int) (Math.random() * 1000000);
            B[i] = (int) (Math.random() * 1000000);
        }

        PolynomMultiplication nm = new PolynomMultiplication(A, B);
        long startTime = System.currentTimeMillis();
        nm.naiveNultiply();
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken for serial multiplication: " + (endTime - startTime) + "ms");

        nm.resetResult();
        Thread[] threads = new Thread[noThreads];
        startTime = System.currentTimeMillis();

        for (int i = 0; i < noThreads; i++) {
            int threadIndex = i;
            threads[i] = new Thread(() -> {
                nm.naiveMultiplyParallel(threadIndex, noThreads);
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

        // nm.resetResult();
        // long startTimeKaratsuba = System.currentTimeMillis();
        // nm.karatsubaMultiply();
        // long endTimeKaratsuba = System.currentTimeMillis();
        // System.out.println("Time taken for Karatsuba multiplication: " + (endTimeKaratsuba - startTimeKaratsuba) + "ms");

        // nm.resetResult();
        // Thread[] threadsKaratsuba = new Thread[noThreads];
        // startTimeKaratsuba = System.currentTimeMillis();
        // for (int i = 0; i < noThreads; i++) {
        //     int threadIndex = i;
        //     threadsKaratsuba[i] = new Thread(() -> {
        //         nm.karatsubaMultiplyParallel(threadIndex, noThreads);
        //     });
        //     threadsKaratsuba[i].start();
        // }
        // for (int i = 0; i < noThreads; i++) {
        //     try {
        //         threadsKaratsuba[i].join();
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        // }
        // endTimeKaratsuba = System.currentTimeMillis();
        // System.out.println("Time taken for parallel Karatsuba multiplication: " + (endTimeKaratsuba - startTimeKaratsuba) + "ms");

    }

    
    public static void main(String[] args) {
        
        NaiveMultiplication(100000, 10);
        
        
    }

}
