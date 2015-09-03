package datatypes;

import file_io.ResultsWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by GuyK on 30/08/2015.
 */
public class TfidfQueryResult {

    ArrayList<TermVectorWithCosineSimilarity> mVectorWithCosineSimilarities;
    public TfidfQueryResult(ArrayList<TermVectorWithCosineSimilarity> vectorWithCosineSimilarities)
    {
        mVectorWithCosineSimilarities = vectorWithCosineSimilarities;

        sort();
    }

    public ArrayList<TermVectorWithCosineSimilarity> getVectorWithCosineSimilarities()
    {
        return mVectorWithCosineSimilarities;
    }

    public void print()
    {
        System.out.println("COSINE SIMILARITY REPORT");
        System.out.println("------------------------");
        for(TermVectorWithCosineSimilarity t : mVectorWithCosineSimilarities)
        {
            System.out.println("Document #" + t.TermVector.getDocumentID() + " : " + t.CosineSimilarity);
        }

        System.out.println();
    }

    public void sort()
    {
        mVectorWithCosineSimilarities.sort(new CosineComparator());
    }

    public void writeResultLines(Query query, ResultsWriter writer) throws IOException {
        int queryID = query.QueryID;

        if(mVectorWithCosineSimilarities.size()==0)
        {
            String line = "q" + queryID + "," + "dummy" + "," + 1 + System.getProperty("line.separator");
            writer.writeLine(line);
        }
        else {
            int rank = 1;
            for (TermVectorWithCosineSimilarity entry : mVectorWithCosineSimilarities) {
                int documentID = entry.TermVector.getDocumentID();

                String line = "q" + queryID + "," + "doc" + documentID + "," + rank + System.getProperty("line.separator");
                writer.writeLine(line);
                rank++;
            }
        }
    }
}

class CosineComparator implements Comparator<TermVectorWithCosineSimilarity>
{
    public int compare(TermVectorWithCosineSimilarity c1, TermVectorWithCosineSimilarity c2)
    {
        return c2.CosineSimilarity.compareTo(c1.CosineSimilarity);
    }
}
