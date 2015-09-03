package file_io;

import java.io.*;

/**
 * Created by GuyK on 30/08/2015.
 */
public class ResultsWriter {

    private String mFileName;
    private BufferedWriter mWriter;

    public ResultsWriter(String filename) throws IOException {
        mFileName = filename;
        mWriter = new BufferedWriter(new FileWriter(mFileName));
    }

    public void writeLine(String line) throws IOException {
        mWriter.write(line);
    }

    public void close() throws IOException {
        mWriter.close();
    }

}
