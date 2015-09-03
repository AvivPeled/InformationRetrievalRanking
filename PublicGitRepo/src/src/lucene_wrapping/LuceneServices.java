package lucene_wrapping;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import services.DocumentFrequency;
import services.Stopwords;
import datatypes.TermVector;
import datatypes.TermVectorWithCosineSimilarity;
import datatypes.TfidfQueryResult;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GuyK on 30/08/2015.
 */
public class LuceneServices {
    public static LuceneServices Instance = new LuceneServices();

    private Directory mRamDirectory;

    private LuceneServices()
    {
        mRamDirectory = new RAMDirectory();
    }

    public void indexDataFile(String filename) throws IOException {
        DataFileLuceneIndexer indexer = new DataFileLuceneIndexer(mRamDirectory);
        indexer.indexFile(filename);

        IndexReader ir = IndexReader.open(mRamDirectory);

        DocumentFrequency.Instance.compute(ir);
        Stopwords.Instance.compute(ir);


        ir.close();
    }

    public TfidfQueryResult queryCosineDistance(String queryString) throws IOException {
        ArrayList<TermVectorWithCosineSimilarity> cosineSimilarities = new ArrayList<TermVectorWithCosineSimilarity>();

        TermVector queryVector = TermVector.CreateFromQueryString(queryString);
        ArrayList<TermVector> documentVectors = getTermVectors();
        for(TermVector vector : documentVectors)
        {
            double cosineSimilarity = queryVector.cosineSimilarity(vector);
            if(cosineSimilarity > 0)
            {
                cosineSimilarities.add(new TermVectorWithCosineSimilarity(vector, cosineSimilarity));


            }
        }

        TfidfQueryResult result = new TfidfQueryResult(cosineSimilarities);

        return result;
    }

    private ArrayList<TermVector> getTermVectors() throws IOException {
        ArrayList<TermVector> result = new ArrayList<TermVector>();
        IndexReader ir = IndexReader.open(mRamDirectory);

        for(int docNum=0; docNum < ir.numDocs(); docNum++) {
            TermFreqVector tfv = ir.getTermFreqVector(docNum,LuceneConstants.CONTENTS);
            if (tfv == null) {
                // ignore empty fields
                continue;
            }

            result.add(TermVector.CreateFromLuceneTermFreqWithProcess(tfv,docNum+1,false));
        }
        return result;
    }

    public void queryRawString(String searchQuery) throws IOException, ParseException {
        IndexReader reader = IndexReader.open(mRamDirectory, true);
        IndexSearcher searcher = new IndexSearcher(reader);

        long startTime = System.currentTimeMillis();

        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
        QueryParser queryParser = new QueryParser(Version.LUCENE_36,LuceneConstants.CONTENTS,analyzer);
        Query query = queryParser.parse(searchQuery);

        TopDocs hits = searcher.search(query,100);

        if(hits.totalHits>0) {
            long endTime = System.currentTimeMillis();
            System.out.println("TOTAL " + hits.totalHits + " DOCUMENTS FOUND (Time :" + (endTime - startTime) + "ms)");
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.printf("FOUND in Document: #%d \n", (scoreDoc.doc + 1));
            }
            searcher.close();
        }
    }


}
