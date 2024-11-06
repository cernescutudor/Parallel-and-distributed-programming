import javax.sound.midi.SysexMessage;

public class PolynomMultiplication {

    private long[] A;
    private long[] B;
    private long[] result;
    private Karatsuba karatsuba = new Karatsuba();



    public PolynomMultiplication(long[] A, long[] B) {
        this.A = A;
        this.B = B;
        this.result = new long[A.length + B.length + 1];

    }

    public void naiveNultiply() {
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B.length; j++) {
                result[i + j] += A[i] * B[j];
            }
        }
    }

    public void naiveMultiplyParallel(int threadID, int numThreads) {
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

    public void karatsubaMultiply() {
        
        for(int i = 0; i < A.length; i++) {
            for(int j = 0; j < B.length; j++) {
                result[i + j] = Karatsuba.multiply( A[i], B[j]);
            }

        }
    }

    public void karatsubaMultiplyParallel(int threadID, int numThreads) {
        long[] localResult = new long[A.length + B.length + 1];
        
        // Divide the work based on threadID and numThreads
        for (int i = threadID; i < A.length; i += numThreads) {
            for (int j = 0; j < B.length; j++) {
                localResult[i + j] = Karatsuba.multiply( A[i], B[j]);
            }
        }
        
        // Merge local results into the global result with synchronized access
        synchronized (result) {
            for (int k = 0; k < result.length; k++) {
                result[k] += localResult[k];
            }
        }
    }

    public long[] getResult() {
        return result;
    }

    public void resetResult() {
        result = new long[A.length + B.length + 1];
    }

    


}