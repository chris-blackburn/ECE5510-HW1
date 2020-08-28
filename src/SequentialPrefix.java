/**
 * Finds the prefix sum using a sequential algorithm and writes it to out,txt
 *
 */
public class SequentialPrefix implements IPrefix {

    public void run(String inputFileName, int bufferSize) {


    }

    public static void main (String [] args) {
        new SequentialPrefix().run("in.txt", 1000);
    }

}
