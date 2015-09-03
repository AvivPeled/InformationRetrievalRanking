package datatypes;

import org.apache.lucene.index.TermFreqVector;
import services.DocumentFrequency;
import services.QueryUtils;
import services.Stopwords;

import java.util.HashMap;
import java.util.Map;

public class TermVector{

    public static boolean DEBUG_MODE = false;

    private Map<String, Integer> mTermsFrequencyMap;
    private Integer mOriginatingDocumentID;
    private Map<String, Double> mNormalizedTerms;
    private Map<String, Double> mTfIdfScore;

    public static TermVector CreateFromQueryString(String query){
        TermVector termVector = new TermVector();

        String[] words = query.split(" ");
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String rawWord : words) {
            String word = QueryUtils.Instance.normalize(rawWord);
            if(Stopwords.Instance.isStopWord(word)==false) {
                Integer n = map.get(word);
                n = (n == null) ? 1 : ++n;
                map.put(word, n);
            }
        }

        termVector.mTermsFrequencyMap = map;
        termVector.mOriginatingDocumentID = -1;

        termVector.computeTfidfScore();
        termVector.normalize();

        return termVector;
    }

    public static TermVector CreateFromLuceneTermFreqWithProcess(TermFreqVector tfVectors, int originatingDocumentIndex, boolean includeStopWords)
    {
        TermVector termVector = CreateFromLuceneTermFreq(tfVectors, originatingDocumentIndex, includeStopWords);
        termVector.computeTfidfScore();
        termVector.normalize();
        return termVector;
    }

    public static TermVector CreateFromLuceneTermFreq(TermFreqVector tfVectors, int originatingDocumentIndex, boolean includeStopWords)
    {
        TermVector termVector = new TermVector();

        //extract entries
        String terms[] = tfVectors.getTerms();
        int freqs[] = tfVectors.getTermFrequencies();
        int totalEntries = terms.length;

        //build a map from them
        Map<String, Integer> termFrequencyMap = new HashMap<String, Integer>();
        for(int i=0; i<totalEntries;i++)
        {
            String word = terms[i];
            if(includeStopWords || Stopwords.Instance.isStopWord(word)==false) {
                Integer frequency = freqs[i];
                termFrequencyMap.put(word, frequency);
            }
        }

        termVector.mOriginatingDocumentID = originatingDocumentIndex;
        termVector.mTermsFrequencyMap = termFrequencyMap;



        return termVector;
    }

    private void computeTfidfScore() {
        mTfIdfScore = new HashMap<String, Double>();

        for(Map.Entry<String,Integer> entry : mTermsFrequencyMap.entrySet()) {
            String term = entry.getKey();
            int termFrequencyInDocument = entry.getValue();
            Double log10_TF = 1 + Math.log10(termFrequencyInDocument);

            int frequencyOverAllDocuments = DocumentFrequency.Instance.getTermDocumentFrequency(term);
            int documentsCount = DocumentFrequency.Instance.getDocumentsCount();

            if(termFrequencyInDocument>0 && frequencyOverAllDocuments>0) {
                Double log10_IDF = Math.log10((double)documentsCount / (double)frequencyOverAllDocuments);
                Double final_TFIDF_Score = log10_TF * log10_IDF;
                mTfIdfScore.put(term, final_TFIDF_Score);

                if(DEBUG_MODE)
                    System.out.println("TFIDF_DEBUG: DocID=" + mOriginatingDocumentID + ", TermFreq=" + term + ", TF=" + termFrequencyInDocument + ", log10_TF=" + log10_TF +
                       ", DocFreq=" + frequencyOverAllDocuments +  ", log10_IDF=" + log10_IDF + ", TF.IDF=" + final_TFIDF_Score);
            }
            else
            {
                mTfIdfScore.put(term, 0d);
            }
        }

    }

    public void normalize() {
        if(DEBUG_MODE) {
            System.out.println("NORMALIZING DocumentID=" + mOriginatingDocumentID);
            System.out.println("----------------------------------------------------------");
        }


        double sum = 0;
        for(Map.Entry<String,Double> entry : mTfIdfScore.entrySet()) {
            sum += Math.pow(entry.getValue(),2);
        }

        double squaredSum = Math.sqrt(sum);

        mNormalizedTerms = new HashMap<String, Double>();
        for(Map.Entry<String,Double> entry : mTfIdfScore.entrySet()) {
            double value = entry.getValue();
            double valueDividedBySqrtSum = value /squaredSum;

            mNormalizedTerms.put(entry.getKey(),valueDividedBySqrtSum);

            if(DEBUG_MODE) {
                System.out.println("TFIDF_DEBUG: DocID=" + mOriginatingDocumentID + ", Term=" + entry.getKey() + ", Before_Normalization=" + entry.getValue() + ", After_Normalization=" + valueDividedBySqrtSum);
            }
        }
        if(DEBUG_MODE) {
            System.out.println();
        }
    }

    public double cosineSimilarity(TermVector rhs)
    {
        if(DEBUG_MODE) {
            System.out.println("COMPUTING COSINE SIMILARITY WITH: RHS_DocID=" + rhs.mOriginatingDocumentID);
            System.out.println("------------------------------------------------------------------");
        }

        Double cosineSum = 0d;
        for(Map.Entry<String,Double> lhs : mNormalizedTerms.entrySet()) {
            if(rhs.getNormalizedMap().containsKey(lhs.getKey()))
            {
                Double lhsNormalizedValue = lhs.getValue();
                Double rhsNormalizedValue = rhs.getNormalizedMap().get(lhs.getKey());
                Double mult = lhsNormalizedValue*rhsNormalizedValue;
                cosineSum += mult;

                if(DEBUG_MODE) {
                    System.out.println("TFIDF_DEBUG: RHS_DocID=" + rhs.mOriginatingDocumentID + ", Term=" + lhs.getKey() + ", lhsNormalizedValue=" + lhsNormalizedValue + ", rhsNormalizedValue=" + rhsNormalizedValue);
                }
            }
        }

        if(DEBUG_MODE) {
            System.out.println("TFIDF_DEBUG: RHS_DocID=" + rhs.mOriginatingDocumentID + ", CosineSum=" + cosineSum + "\n");
        }

        return cosineSum;
    }


    public Map<String,Integer> getTermsMap() {
        return mTermsFrequencyMap;
    }

    public Map<String,Double> getNormalizedMap() {
        return mNormalizedTerms;
    }



    public int getDocumentID() {
        return mOriginatingDocumentID;
    }
}

