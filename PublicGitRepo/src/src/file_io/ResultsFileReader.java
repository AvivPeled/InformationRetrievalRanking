package file_io;

import datatypes.GoldResults;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GuyK on 31/08/2015.
 */
public class ResultsFileReader {
    private String mFileName;
    private BufferedReader mReader;

    public ResultsFileReader(String filename)
    {
        mFileName = filename;
    }



    public void open() throws IOException {
        mReader = new BufferedReader(new FileReader(mFileName));
    }


    public GoldResults readResults() throws IOException {
        ArrayList<String> lines = new ArrayList<String>();
        String line = mReader.readLine();
        while(line!=null) {
            lines.add(line);
            line = mReader.readLine();
        }

        return GoldResults.CreateFromLines(lines);
    }

    public static GoldResults createFromFile(String filename) throws IOException {
        ResultsFileReader reader = new ResultsFileReader(filename);
        reader.open();
        return reader.readResults();
    }
}
