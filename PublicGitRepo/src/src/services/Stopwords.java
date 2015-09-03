package services;


import datatypes.TermVector;
import lucene_wrapping.LuceneConstants;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;

import java.io.IOException;
import java.util.*;

/**
 * Created by GuyK on 30/08/2015.
 */
public class Stopwords {

    public int STOPWORDS_TOP_WORDS_COUNT = 20;
    public static Stopwords Instance = new Stopwords();
    private  HashMap<String, Integer> mWordCountMap;
    private ArrayList<String> mStopWords;

    private  Stopwords()
    {
        mWordCountMap = new HashMap<String, Integer>();
    }


    public void compute(IndexReader ir) throws IOException {
        for(int docNum=0; docNum < ir.numDocs(); docNum++) {
            TermFreqVector tfv = ir.getTermFreqVector(docNum, LuceneConstants.CONTENTS);
            TermVector vector = TermVector.CreateFromLuceneTermFreq(tfv, docNum + 1, true);
            for(Map.Entry<String, Integer> entry : vector.getTermsMap().entrySet()){
                Integer count = mWordCountMap.get(entry.getKey());
                count = (count==null) ? entry.getValue() : count + entry.getValue();
                mWordCountMap.put(entry.getKey(),count);
            }
        }

        mStopWords = buildStopwordsList(STOPWORDS_TOP_WORDS_COUNT);
        printStopWords();
    }

    private ArrayList<String> buildStopwordsList(int topCount) {
        if(topCount==0)
            return new ArrayList<String>();

        ArrayList<String> result = new ArrayList<String>();
        Map<String, Integer> sortByValue = MapUtil.sortByValueDescending(mWordCountMap);
        int count = 0;

        for(Map.Entry<String,Integer> entry : sortByValue.entrySet())
        {
            result.add(entry.getKey());
            count++;

            if(count==topCount)
                break;
        }

        return result;
    }

    private void printStopWords() {
        System.out.print("StopWords: [");
        for(String word : mStopWords)
        {
            System.out.print(word + ", ");
        }
        System.out.println("]");
    }

    public boolean isStopWord(String s){
        boolean res = mStopWords.contains(s);
        return res;
    }
}

class MapUtil
{
    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValueDescending(Map<K, V> map)
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue());
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}
