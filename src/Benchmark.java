/**
 *  Benchmark different version of the prefix sum algorithm
 *
 */
public class Benchmark {

    /* The input file we would like to use */
    public static String inputFileName = "in.txt";

    /* Total number of elements to hold in memory at a time */
    public static int [] bufferSizes = {1000}; //, 10000, 100000};

    /* Find the average of numRuns # of runs for each type of prefix sum */
    public static int numRuns = 10;

    public static void main(String [] args) {

        IPrefix [] prefixSums = {new SequentialPrefix(), new ParallelPrefixInternal(), new BetterParallelPrefix()};
        System.out.printf("Running benchmarks...\n");

        /* Run for various buffer sizes  */
        for (int bufferSize : bufferSizes) {
            System.out.printf("Using buffer size of %d...\n\n", bufferSize);

            for (IPrefix s : prefixSums) {
                System.out.printf("Running %s...\n", s.getClass().getName());

                /* TODO: Calculate results */
                long averageTime = 0;
                float speedup = 0;

                /* Print results */
                System.out.printf("Average time: %o\n", averageTime);
                System.out.printf("Speedup: %.2f\n", speedup);
                System.out.println("----------------------------------------------------------\n");
            }
        }
    }
}
