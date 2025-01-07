import mpi.MPI;
import java.util.Arrays;
//javac -cp .:lib/mpj.jar PolynomialMultiplication.java
//mpjrun.sh -np 4 -cp .:lib/mpj.jar PolynomialMultiplication
public class PolynomialMultiplication {

    

    public static int[] karatsuba(int[] A, int[] B) {
        int n = Math.max(A.length, B.length);
        n = (int) Math.pow(2, Math.ceil(Math.log(n) / Math.log(2))); // Ensure n is a power of 2

        int[] a = Arrays.copyOf(A, n); // Extend A to length n
        int[] b = Arrays.copyOf(B, n); // Extend B to length n

        int[] result = karatsubaRecursive(a, b);
        return trimLeadingZeros(result);
    }

    // Recursive function for Karatsuba algorithm
    private static int[] karatsubaRecursive(int[] A, int[] B) {
        int n = A.length;

        // Base case: simple multiplication
        if (n == 1) {
            return new int[] { A[0] * B[0] };
        }

        int half = n / 2;

        // Split the polynomials
        int[] A0 = Arrays.copyOfRange(A, 0, half);
        int[] A1 = Arrays.copyOfRange(A, half, n);
        int[] B0 = Arrays.copyOfRange(B, 0, half);
        int[] B1 = Arrays.copyOfRange(B, half, n);

        // Compute the three recursive multiplications
        int[] P0 = karatsubaRecursive(A0, B0); // Low terms
        int[] P1 = karatsubaRecursive(addPolynomials(A0, A1), addPolynomials(B0, B1)); // Cross terms
        int[] P2 = karatsubaRecursive(A1, B1); // High terms

        // Combine the results
        int[] result = new int[2 * n - 1];

        // Add P0 (low terms)
        addToResult(result, P0, 0);

        // Add P2 (high terms)
        addToResult(result, P2, 2 * half);

        // Add (P1 - P0 - P2) to the middle terms
        int[] middle = subtractPolynomials(P1, addPolynomials(P0, P2));
        addToResult(result, middle, half);

        return result;
    }

    // Add two polynomials
    private static int[] addPolynomials(int[] A, int[] B) {
        int n = Math.max(A.length, B.length);
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = (i < A.length ? A[i] : 0) + (i < B.length ? B[i] : 0);
        }
        return result;
    }

    // Subtract two polynomials
    private static int[] subtractPolynomials(int[] A, int[] B) {
        int n = Math.max(A.length, B.length);
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = (i < A.length ? A[i] : 0) - (i < B.length ? B[i] : 0);
        }
        return result;
    }

    // Add a polynomial to the result at a specific offset
    private static void addToResult(int[] result, int[] toAdd, int offset) {
        for (int i = 0; i < toAdd.length; i++) {
            result[i + offset] += toAdd[i];
        }
    }

    // Remove leading zeros from the polynomial
    private static int[] trimLeadingZeros(int[] poly) {
        int i = poly.length - 1;
        while (i > 0 && poly[i] == 0) {
            i--;
        }
        return Arrays.copyOf(poly, i + 1);
    }
    

    private static int[] multiplyRegular(int[] a, int[] b) {
        int[] result = new int[a.length + b.length - 1];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                result[i + j] += a[i] * b[j];
            }
        }
        return result;
    }

    public static void main(String[] args) {

        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int length = 100;
        int[] polyA = new int[length];
        int[] polyB = new int[length];
        for (int i = 1; i < length; i++) {
            polyA[i] = i;
            polyB[i] = i;
        }

        int lengthA = polyA.length;
        int lengthB = polyB.length;
        int resultLength = lengthA + lengthB - 1;

        // Calculate sendcounts and displacements
        int[] sendcounts = new int[size];
        int[] displs = new int[size];
        int chunkSize = (lengthA + size - 1) / size; // Divide polyA among processes
        for (int i = 0; i < size; i++) {
            sendcounts[i] = Math.min(chunkSize, lengthA - i * chunkSize);
            displs[i] = i * chunkSize;
        }

        // Allocate local arrays
        int[] aChunk = new int[sendcounts[rank]];
        int[] fullPolyB = new int[lengthB];
        System.arraycopy(polyB, 0, fullPolyB, 0, lengthB);

        // Scatter polyA and broadcast polyB
        MPI.COMM_WORLD.Scatterv(polyA, 0, sendcounts, displs, MPI.INT, aChunk, 0, sendcounts[rank], MPI.INT, 0);
        MPI.COMM_WORLD.Bcast(fullPolyB, 0, lengthB, MPI.INT, 0);

        // Measure start time
        long startTime = System.nanoTime();

        int[] localResult = karatsuba(aChunk, fullPolyB);
        // Uncomment to use regular multiplication instead of Karatsuba
        // int[] localResult = multiplyRegular(aChunk, fullPolyB);

        // Measure end time
        long endTime = System.nanoTime();

        // Calculate elapsed time
        long elapsedTime = endTime - startTime;

        if (rank == 0) {
            System.out.println("Time taken (nanoseconds): " + elapsedTime);
            System.out.println("Time taken (milliseconds): " + (elapsedTime / 1_000_000.0));
        }

        int[] alignedResult = new int[resultLength];
        int offset = displs[rank];
        System.arraycopy(localResult, 0, alignedResult, offset, localResult.length);

        int[] globalResult = new int[resultLength];
        MPI.COMM_WORLD.Reduce(alignedResult, 0, globalResult, 0, resultLength, MPI.INT, MPI.SUM, 0);

        if (rank == 0) {
            //System.out.println("Final Result: " + Arrays.toString(globalResult));
        }

        MPI.Finalize();
    }
}
