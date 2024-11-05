
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class NaiveMultiplication {

    private int[] A;
    private int[] B;
    private int[] result;



    public NaiveMultiplication(int[] A, int[] B) {
        this.A = A;
        this.B = B;
        this.result = new int[A.length + B.length + 1];

    }

    public void multiply() {
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B.length; j++) {
                result[i + j] += A[i] * B[j];
            }
        }
    }

    public void multiplyParallel(int threadID, int numThreads) {
        int[] localResult = new int[A.length + B.length + 1];
        
        // Divide the work based on threadID and numThreads
        for (int i = threadID; i < A.length; i += numThreads) {
            for (int j = 0; j < B.length; j++) {
                localResult[i + j] += A[i] * B[j];
            }
        }
        
        // Merge local results into the global result with synchronized access
        synchronized (result) {
            for (int k = 0; k < result.length; k++) {
                result[k] += localResult[k];
            }
        }
    }

    public int[] getResult() {
        return result;
    }

    


}