import java.io.BufferedReader;
import java.io.IOException;

public interface IPrefix {
    /* Gets a chunk of integers from a buffered reader */
    static int getChunk(BufferedReader in, int chunk[]) throws IOException {
        int i;
        String nStr;

        for (i = 0; i < chunk.length; i++) {
            if (null == (nStr = in.readLine())) {
                return i;
            }

            /* Assuming the input is clean... */
            chunk[i] = Integer.parseInt(nStr);
        }

        return i;
    }

    void run(String inputFileName, int bufferSize);
}
