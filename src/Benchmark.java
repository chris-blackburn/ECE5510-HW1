/**
 *  Benchmark different version of the prefix sum algorithm
 *
 */
public class Benchmark {

    /* The input file we would like to use */
    public static String inputFileName = "in.txt";

    /* Total number of elements to hold in memory at a time */
    public static int [] bufferSizes = {100, 1000, 10000};

    /* Find the average of numRuns # of runs for each type of prefix sum */
    public static int numRuns = 300;

    public static void main(String [] args) {
        /* Sequential prefix should run first */
        IPrefix [] prefixSums = {new SequentialPrefix(), new ParallelPrefix(), new ParallelPrefixInternal(), new BetterParallelPrefix()};
        System.out.println("CPUs: " + Runtime.getRuntime().availableProcessors());
        System.out.printf("Running benchmarks (%d runs)...\n", numRuns);

        /* Run for various buffer sizes  */
        for (int bufferSize : bufferSizes) {
            System.out.printf("** Using buffer size of %d...\n\n", bufferSize);
            long sequentialTime = 0;

            for (IPrefix s : prefixSums) {
                System.out.printf("Running %s...\n", s.getClass().getName());

                long totalTime = 0, sqTotalTime = 0, averageTime = 0;
                double speedup = 0, stdDev = 0;

                long startTime, runTime;

                for (int i = 0; i < numRuns; i++) {
                    startTime = System.currentTimeMillis();
                    s.run(inputFileName, bufferSize);
                    runTime = (System.currentTimeMillis() - startTime);

                    totalTime += runTime;
                    sqTotalTime += (runTime * runTime);
                }

                averageTime = totalTime / numRuns;

                /* Speedup of sequential is 1 */
                if (s instanceof SequentialPrefix) {
                    speedup = 1;
                    sequentialTime = averageTime;
                } else {
                    speedup = (sequentialTime * 1.0) / averageTime;
                }

                stdDev = Math.sqrt((1.0 * sqTotalTime / numRuns) - (averageTime * averageTime));

                /* Print results */
                System.out.printf("Average time (ms): %o\n", averageTime);
                System.out.printf("Standard deviation: %.2f\n", stdDev);
                System.out.printf("Speedup: %.2f\n", speedup);
                System.out.println("----------------------------------------------------------\n");
            }
        }
    }
}
