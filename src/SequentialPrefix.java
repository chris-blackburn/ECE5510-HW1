import java.io.*;

/**
 * Finds the prefix sum using a sequential algorithm and writes it to out,txt
 *
 */
public class SequentialPrefix implements IPrefix {
    public void run(String inputFileName, int bufferSize) {
        BufferedReader in;
        BufferedWriter out;
        String nStr;
        int sum = 0;

        try {
            in = new BufferedReader(new FileReader(inputFileName));
            out = new BufferedWriter(new FileWriter("out.txt"));

            while (null != (nStr = in.readLine())) {
                sum += Integer.parseInt(nStr);
                out.write(String.valueOf(sum));
                out.newLine();
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main (String [] args) {
        new SequentialPrefix().run("in.txt", 1000);
    }
}
