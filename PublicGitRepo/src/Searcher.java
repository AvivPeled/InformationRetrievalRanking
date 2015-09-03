import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class Searcher {
	

   IndexSearcher searcher;

   @SuppressWarnings("deprecation")
public Searcher( RAMDirectory idx) throws IOException {

	    searcher = new IndexSearcher(idx);
   }
   
   /**
    * Searches for the given string in the "content" field
    */
   public void search(String queryString)
           throws ParseException, IOException {
	   SimpleAnalyzer  analyzer = new SimpleAnalyzer(Version.LUCENE_36);
      // Build a Query object
	   QueryParser queryParser = new QueryParser(Version.LUCENE_36,"content", analyzer);
            
      Query query = queryParser.parse(queryString);
 
 
      int hitsPerPage = 10;
      // Search for the query
      TopScoreDocCollector collector = TopScoreDocCollector.create(5 * hitsPerPage, false);
      searcher.search(query, collector);
 
      ScoreDoc[] hits = collector.topDocs().scoreDocs;
 
      int hitCount = collector.getTotalHits();
      System.out.println(hitCount + " total matching documents");
 
      // Examine the Hits object to see if there were any matches
 
      if (hitCount == 0) {
         System.out.println(
                 "No matches were found for \"" + queryString + "\"");
      } else {
         System.out.println("Hits for \"" +
                 queryString + "\" were found in quotes by:");
 
         // Iterate over the Documents in the Hits object
         for (int i = 0; i < hits.length; i++) {
            ScoreDoc scoreDoc = hits[i];
            int docId = scoreDoc.doc;
            float docScore = scoreDoc.score;
            System.out.println("docId: " + docId + "\t" + "docScore: " + docScore);
 
            Document doc = searcher.doc(docId);
 
            // Print the value that we stored in the "title" field. Note
            // that this Field was not indexed, but (unlike the
            // "contents" field) was stored verbatim and can be
            // retrieved.
            System.out.println("  " + (i + 1) + ". " + doc.get("title"));
            System.out.println("Content: " + doc.get("content"));            
         }
      }
      System.out.println();
   }

  /* public Document getDocument(ScoreDoc scoreDoc) 
      throws CorruptIndexException, IOException{
      return indexSearcher.doc(scoreDoc.doc);	
   }
*/
   public void close() throws IOException{
	    searcher.close();
	 //   indexDirectory.close(); 
   }
}