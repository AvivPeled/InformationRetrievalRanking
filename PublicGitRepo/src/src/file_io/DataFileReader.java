package file_io;

import lucene_wrapping.LuceneConstants;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by GuyK on 30/08/2015.
 */
public class DataFileReader {
    private String mFileName;
    private BufferedReader mReader;
    private int mNextEntryIdx;

    public DataFileReader(String filename)
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

    public Document nextDocument() throws IOException {
        if(mNextEntryIdx ==-1) {
            //System.out.println("Reached END OF FILE");
            return null;
        }

        Document document = new Document();

        //System.out.printf("Processing Document %d: ", mNextEntryIdx);

        String dotWValidation = mReader.readLine();
        if(!dotWValidation.contains(".W")) //sanity part 2
        {
            System.out.printf("Document %d, Not Containing '.W' sign", mNextEntryIdx);
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

        document.add(new Field(LuceneConstants.CONTENTS,sb.toString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
        //System.out.printf("Read %d Lines\n", linesRead);

        return document;

    }
}
