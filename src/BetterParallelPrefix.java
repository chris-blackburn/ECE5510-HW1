import java.io.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 *  Improved version of ParallelPrefix
 *
 */
public class BetterParallelPrefix implements IPrefix {
    /* Minimum size to stop reducing (how many elements to start calculating sequentially). */
    private int minGran;
    ForkJoinPool pool = ForkJoinPool.commonPool();

    private class BottomUp extends RecursiveAction {
        private int chunk[];
        private int start;
        private int end;

        public BottomUp(int chunk[], int start, int end) {
            this.chunk = chunk;
            this.start = start;
            this.end = end;
        }

        /* Recursive function that modifies the array in place to 'build' the binary tree. One the first call returns, the
         * input array will have (for each smallest chunk) the sum of all element up to the end of that chunk. For example:
         * minimum granularity = 4; [1, 2, 3, 4, 5, 6, 7, 8] -> [1, 2, 3, 10, 5, 6, 7, 36] */
        public void compute() {
            int mid, sum = 0;
            BetterParallelPrefix.BottomUp left, right;

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
            left = new BetterParallelPrefix.BottomUp(chunk, start, mid);
            right = new BetterParallelPrefix.BottomUp(chunk, mid + 1, end);

            this.invokeAll(left, right);

            /* Update the sum of the range start -> end in place */
            chunk[end] += chunk[mid];
        }
    }

    private class TopDown extends RecursiveAction {
        private int chunk[];
        private int start;
        private int end;
        private int fromLeft;

        public TopDown(int chunk[], int start, int end, int fromLeft) {
            this.chunk = chunk;
            this.start = start;
            this.end = end;
            this.fromLeft = fromLeft;
        }

        /* Recursive function that modifies the array in place to carry down all the fromLeft values. For example:
         * minimum granularity = 4; [1, 2, 3, 10] -> [0, 1, 3, 6] */
        public void compute() {
            int mid, leftSum, history[];
            BetterParallelPrefix.TopDown left, right;

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
            left = new BetterParallelPrefix.TopDown(chunk, start, mid, fromLeft);
            right = new BetterParallelPrefix.TopDown(chunk, mid + 1, end, fromLeft + leftSum);

            this.invokeAll(left, right);
        }
    }

    public void run(String inputFileName, int bufferSize) {
        BufferedReader in;
        BufferedWriter out;
        int ncpu = Runtime.getRuntime().availableProcessors();
        int prevSum = 0, endSum;

        int chunk[], iread;

        BetterParallelPrefix.BottomUp bu;
        BetterParallelPrefix.TopDown td;

        try {
            in = new BufferedReader(new FileReader(inputFileName));
            out = new BufferedWriter(new FileWriter("out.txt"));

            /* we have to read data in as chars and convert */
            chunk = new int[bufferSize];
            minGran = Math.max(bufferSize / ncpu, 64);

            while (0 != (iread = IPrefix.getChunk(in, chunk))) {
                /* Bottom-up, just start on main thread */
                bu = new BetterParallelPrefix.BottomUp(chunk, 0, iread - 1);
                bu.compute();

                /* Since my result is shifted over, and since we already have the final sum, we want to record it to be
                 * space efficient. */
                endSum = chunk[iread - 1];

                /* Top-down */
                td = new BetterParallelPrefix.TopDown(chunk, 0, iread - 1, 0);
                td.compute();

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
        new BetterParallelPrefix().run("in.txt", 1000);
    }
}
