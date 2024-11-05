package lab3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lab3.MatrixMultip;

public class Program {


    public int[][] generateMatrix(int rows, int cols) {
        int[][] matrix = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (int) (Math.random() * 10);
            }
        }
        return matrix;
    }

    public static void main(String[] args) {
        int[][] m1 = new Program().generateMatrix(10, 10);
        int[][] m2 = new Program().generateMatrix(10, 10);

        MatrixMultip matrixMultip = new MatrixMultip(m1, m2, 4);
        long startTime = System.nanoTime();
        Thread[] threads = new Thread[matrixMultip.tasks];
        for (int i = 0; i < matrixMultip.tasks; i++) {
            final int threadID = i;
            threads[i] = new Thread(() -> {
                System.out.println("Thread " + threadID + " started.");
                matrixMultip.threadCalculate(threadID);
                System.out.println("Thread " + threadID + " finished.");
            });
            threads[i].start();
        }

        for (int i = 0; i < matrixMultip.tasks; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.nanoTime();
        long durationThreads = endTime - startTime;
        System.out.println("Time taken with threads: " + durationThreads + " ns");

        matrixMultip.printResult();

        // Using thread pool
        startTime = System.nanoTime();
        ExecutorService executor = Executors.newFixedThreadPool(matrixMultip.tasks);
        for (int i = 0; i < matrixMultip.tasks; i++) {
            final int threadID = i;
            executor.submit(() -> {
                System.out.println("Thread " + threadID + " started.");
                matrixMultip.threadCalculate(threadID);
                System.out.println("Thread " + threadID + " finished.");
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endTime = System.nanoTime();
        long durationThreadPool = endTime - startTime;
        System.out.println("Time taken with thread pool: " + durationThreadPool + " ns");

        matrixMultip.printResult();

        //Print in milliseconds
        System.out.println("Time taken with threads: " + durationThreads / 1000000 + " ms");
        System.out.println("Time taken with thread pool: " + durationThreadPool / 1000000 + " ms");

}
}