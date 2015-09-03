package services;

import datatypes.TermVector;
import lucene_wrapping.LuceneConstants;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GuyK on 30/08/2015.
 */
public class DocumentFrequency {

    public static DocumentFrequency Instance = new DocumentFrequency();
    private DocumentFrequency()
    {

    }

    private HashMap<String, Integer> mWordDocumentsCount;
    private int mDocumentCounter;

    public void compute(IndexReader ir) throws IOException {

        mWordDocumentsCount = new HashMap<String, Integer>();

        for(int docNum=0; docNum < ir.numDocs(); docNum++) {
            TermFreqVector tfv = ir.getTermFreqVector(docNum, LuceneConstants.CONTENTS);
            TermVector vector = TermVector.CreateFromLuceneTermFreq(tfv, docNum + 1, true);

            for(Map.Entry<String, Integer> entry : vector.getTermsMap().entrySet()){

                String word = entry.getKey();
                Integer count = mWordDocumentsCount.get(word);
                count = (count==null) ? 1 : count + 1;

                mWordDocumentsCount.put(entry.getKey(),count);
            }

            mDocumentCounter++;
        }
    }

    public int getTermDocumentFrequency(String term)
    {
        Integer integer = mWordDocumentsCount.get(term);
        if(integer==null)
            return 0;
        return integer;
    }

    public int getDocumentsCount() {
        return mDocumentCounter;
    }
}

