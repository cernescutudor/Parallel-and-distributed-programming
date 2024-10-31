package lab3;

public class MatrixMultip {
    int[][] m1;
    int[][] m2;
    int result[][];
    int tasks;

    public MatrixMultip(int[][] m1, int[][] m2, int tasks) {
        this.m1 = m1;
        this.m2 = m2;
        this.tasks = tasks;
        result = new int[m1.length][m2[0].length];
    }


    public int calculate(int row, int col)
    {
        int n = m1[0].length;
        
        // Initialize the value of the result element at (row, col)
        int resultElement = 0;

        // Calculate the dot product of the row of matrixA and the column of matrixB
        for (int k = 0; k < n; k++) {
            resultElement += m1[row][k] * m2[k][col];
        }

        return resultElement;
    }

    public void threadCalculate(int threadID) {
        int position = threadID;
        for(int i = 0; i < m1.length; i++) {
            while( position < m2[0].length) {
                result[i][position] = calculate(i, position);
                position += tasks;
            }
            position -= m2[0].length;
        }
    }

    public void printResult() {
        for(int i = 0; i < m1.length; i++) {
            for(int j = 0; j < m2[0].length; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }
    }
}
