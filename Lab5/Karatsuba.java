// public class Karatsuba {

//     private static final long BASE_CASE_THRESHOLD = 100;

//     public static long multiply(long x, long y) {
//         // Base case: if numbers are small, multiply directly
//         if (x < BASE_CASE_THRESHOLD || y < BASE_CASE_THRESHOLD) {
//             return x * y;
//         }

//         // Determine the number of bits in the larger of the two numbers
//         int n = Long.SIZE - Long.numberOfLeadingZeros(Math.max(x, y));
//         int halfN = n / 2;

//         // Split x and y into high and low parts based on bit shifts
//         long highX = x >> halfN;
//         long lowX = x & ((1L << halfN) - 1);
//         long highY = y >> halfN;
//         long lowY = y & ((1L << halfN) - 1);

//         // Recursive calls to multiply the parts
//         long z0 = multiply(lowX, lowY);
//         long z1 = multiply(lowX + highX, lowY + highY);
//         long z2 = multiply(highX, highY);

//         // Combine the results using Karatsuba's formula
//         return (z2 << (2 * halfN)) + ((z1 - z2 - z0) << halfN) + z0;
//     }
// }


import java.util.Stack;

public class Karatsuba {

    private static final long BASE_CASE_THRESHOLD = 1000;

    private static class Task {
        long x, y;
        long result;
        int n;           // The bit length of the current task
        boolean isReady; // Indicates if the result is ready for combination

        Task(long x, long y, int n) {
            this.x = x;
            this.y = y;
            this.n = n;
            this.isReady = (x < BASE_CASE_THRESHOLD || y < BASE_CASE_THRESHOLD);
            this.result = 0;
        }
    }

    public static long multiply(long x, long y) {
        if (x < BASE_CASE_THRESHOLD || y < BASE_CASE_THRESHOLD) {
            return x * y;  // Base case: small enough numbers are multiplied directly
        }

        Stack<Task> stack = new Stack<>();
        stack.push(new Task(x, y, getBitLength(Math.max(x, y))));

        long finalResult = 0;

        while (!stack.isEmpty()) {
            Task task = stack.pop();

            // If this task is ready, accumulate its result
            if (task.isReady) {
                finalResult += task.result;
                continue;
            }

            int halfN = task.n / 2;
            long highX = task.x >> halfN;
            long lowX = task.x & ((1L << halfN) - 1);
            long highY = task.y >> halfN;
            long lowY = task.y & ((1L << halfN) - 1);

            // Break down the current task into subtasks
            Task z0 = new Task(lowX, lowY, halfN);
            Task z2 = new Task(highX, highY, halfN);
            Task z1 = new Task(lowX + highX, lowY + highY, halfN);

            // Mark the main task as ready for combination after subtasks
            task.isReady = true;

            // First, we push back the main task (for later combination of results)
            stack.push(task);

            // Then, push the subtasks in reverse order (so z0 is processed first)
            stack.push(z2);
            stack.push(z1);
            stack.push(z0);
        }

        return finalResult;
    }

    private static int getBitLength(long number) {
        return Long.SIZE - Long.numberOfLeadingZeros(number);
    }
}
