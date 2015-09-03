package file_io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by GuyK on 30/08/2015.
 */
public class QueriesFileReader {
    private String mFileName;
    private BufferedReader mReader;
    private int mNextEntryIdx;

    public QueriesFileReader(String filename)
    {
        mFileName = filename;
    }

    public void open() throws IOException {
        mReader = new BufferedReader(new FileReader(mFileName));
        readCurrnetLine();
    }

    private int readCurrnetLine() throws IOException {
        String line = mReader.readLine();
        if(line.contains(".I")) //sanity checkup
        {
            line = line.substring(3);
            mNextEntryIdx = Integer.parseInt(line);
        }
        return mNextEntryIdx;
    }

    public String nextQuery() throws IOException {
        if(mNextEntryIdx ==-1) {
            //System.out.println("Reached END OF FILE");
            return null;
        }

        String dotWValidation = mReader.readLine();
        if(!dotWValidation.contains(".W")) //sanity part 2
        {
            System.out.printf("Query %d, Not Containing '.W' sign", mNextEntryIdx);
        }

        int linesRead=0;
        StringBuilder sb = new StringBuilder();
        while(true)
        {
            String currnentLine = mReader.readLine();

            if(currnentLine==null) {
                mNextEntryIdx = -1;
                break;
            }

            if(currnentLine.contains(".I")) {
                currnentLine = currnentLine.substring(3);
                mNextEntryIdx = Integer.parseInt(currnentLine);
                break;
            }

            linesRead++;
            sb.append(currnentLine + "\n");
        }

        return sb.toString();

    }
}
