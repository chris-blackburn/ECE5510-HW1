import java.io.*;
import java.util.Arrays;

/**
 *  Uses the parallelPrefix function provided by Java to find the prefix sum
 *
 */
public class ParallelPrefixInternal implements IPrefix {
    public void run(String inputFileName, int bufferSize) {
        BufferedReader in;
        BufferedWriter out;
        String nStr;
        int prevSum = 0;

        int chunk[], iread;

        try {
            in = new BufferedReader(new FileReader(inputFileName));
            out = new BufferedWriter(new FileWriter("out.txt"));

            /* we have to read data in as chars and convert */
            chunk = new int[bufferSize];

            while (0 != (iread = IPrefix.getChunk(in, chunk))) {
                Arrays.parallelPrefix(chunk, 0, iread, (n1, n2) -> n1 + n2);

                for (int i = 0; i < iread; i++) {
                    out.write(String.valueOf(chunk[i] + prevSum));
                    out.newLine();
                }

                /* Save the sum for the next chunk */
                prevSum += chunk[iread - 1];
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main (String [] args) {
        new ParallelPrefixInternal().run("in.txt", 1000);
    }
}
