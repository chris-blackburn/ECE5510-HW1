import java.io.*;
import java.util.Arrays;

/**
 *  Finds the prefix sum using a parallel algorithm
 *
 */
public class ParallelPrefix implements IPrefix {
    /* Minimum size to stop reducing (how many elements to start calculating sequentially). */
    private int minGran;

    /* Recursive function that modifies the array in place to 'build' the binary tree. One the first call returns, the
     * input array will have (for each smallest chunk) the sum of all element up to the end of that chunk. For example:
     * minimum granularity = 4; [1, 2, 3, 4, 5, 6, 7, 8] -> [1, 2, 3, 10, 5, 6, 7, 36] */
    private void bottomUp(int chunk[], int start, int end) {
        int mid, sum = 0;

        if (end - start <= minGran) {
            for (int i = start; i < end; i++) {
                /* This gives us the sum of the elements from start to end */
                sum += chunk[i];
            }

            /* Modify the array in place to record all the sums and return */
            chunk[end] += sum;
            return;
        }

        mid = start + ((end - start) >> 1);
        bottomUp(chunk, start, mid);
        bottomUp(chunk, mid + 1, end);

        /* Update the sum of the range start -> end in place */
        chunk[end] += chunk[mid];
    }

    /* Recursive function that modifies the array in place to carry down all the fromLeft values. For example:
     * minimum granularity = 4; [1, 2, 3, 10] -> [0, 1, 3, 6]*/
    private void topDown(int chunk[], int start, int end, int fromLeft) {
        int mid, leftSum, history[];

        if (end - start <= minGran) {
            leftSum = chunk[start];
            history = new int[] { chunk[start], 0 };

            chunk[start] = fromLeft;
            for (int i = start + 1; i < end; i++) {
                leftSum += chunk[i];

                history[1] = chunk[i];
                chunk[i] = chunk[i - 1] + history[0];
                history[0] = history[1];
            }

            chunk[end] = fromLeft + leftSum;
            return;
        }

        mid = start + ((end - start) >> 1);
        leftSum = chunk[mid];
        topDown(chunk, start, mid, fromLeft);
        topDown(chunk, mid + 1, end, fromLeft + leftSum);
    }

    public void run(String inputFileName, int bufferSize) {
        BufferedReader in;
        BufferedWriter out;
        int ncpu = Runtime.getRuntime().availableProcessors();
        int prevSum = 0, endSum;

        int chunk[], iread;

        try {
            in = new BufferedReader(new FileReader(inputFileName));
            out = new BufferedWriter(new FileWriter("out.txt"));

            /* we have to read data in as chars and convert */
            chunk = new int[bufferSize];
            minGran = Math.max(bufferSize / ncpu, 64);

            while (0 != (iread = IPrefix.getChunk(in, chunk))) {
                /* Bottom-up */
                bottomUp(chunk, 0, iread - 1);
                /* Since my result is shifted over, and since we already have the final sum, we want to record it to be
                 * space efficient. */
                endSum = chunk[iread - 1];

                /* Top-down */
                topDown(chunk, 0, iread - 1, 0);

                /* Write back. */
                for (int i = 1; i < iread; i++) {
                    out.write(String.valueOf(chunk[i] + prevSum));
                    out.newLine();
                }

                /* Write the last value of this chunk */
                out.write(String.valueOf(endSum + prevSum));
                out.newLine();

                /* Save the sum for the next chunk */
                prevSum += endSum;
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main (String [] args) {
        new ParallelPrefix().run("in.txt", 1000);
    }


}

