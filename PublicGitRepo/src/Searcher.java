import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.PorterStemmer;

public class Searcher {

	IndexSearcher searcher;
	FileWriter outputFileWriter;
	boolean improvedFlag;
	@SuppressWarnings("deprecation")
	public Searcher(RAMDirectory idx, String[] docs,FileWriter outputFileWriter,boolean improvedFlag) throws IOException {
		IndexReader reader = IndexReader.open(idx);
		searcher = new IndexSearcher(reader);
		this.outputFileWriter = outputFileWriter;
		StopWords.Instance.calculateFrequency(docs, idx);
		this.improvedFlag = improvedFlag;
	}

	private String removeStopWordsAndImprove(String queryString) throws Exception
	{
		queryString = StopWords.Instance.removeStopWords(queryString);
		if(improvedFlag==true)
		{
			queryString=stemTerm(queryString);
		}
		return queryString;
	}
	/**
	 * Searches for the given string in the "content" field
	 * 
	 * @throws Exception
	 */
	public void search(int queryNumber, String queryString) throws Exception {
		String newLine = System.getProperty("line.separator");
		queryString=removeStopWordsAndImprove(queryString);
		
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		// Build a Query object
		QueryParser queryParser = new QueryParser(Version.LUCENE_36, "content",
				analyzer);

		Query query = queryParser.parse(queryString);
		int hitsPerPage = 10;
		// Search for the query
		TopScoreDocCollector collector = TopScoreDocCollector.create(5 * hitsPerPage, true);
		searcher.search(query, collector);

		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		int hitCount = collector.getTotalHits();
		System.out.println(hitCount + " total matching documents");

		// Examine the Hits object to see if there were any matches

		if (hitCount == 0) {
			System.out.println("No matches were found for \"" + queryString
					+ "\"");
			outputFileWriter.write("q"+queryNumber+",dummy,1"+ newLine);
		} else {
			System.out.println("Hits for \"" + queryString
					+ "\" were found in quotes by:");

			// Iterate over the Documents in the Hits object
			for (int i = 0; i < hits.length; i++) {
				ScoreDoc scoreDoc = hits[i];
				int docId = scoreDoc.doc;
				float docScore = scoreDoc.score;
				outputFileWriter.write("q"+queryNumber+",doc"+docId+","+(i+1)+ newLine);
				System.out.println("docId: " + docId + "\t" + "docScore: "
						+ docScore);

				Document doc = searcher.doc(docId);

			}
		}
		System.out.println();
	}

	/*
	 * public Document getDocument(ScoreDoc scoreDoc) throws
	 * CorruptIndexException, IOException{ return
	 * indexSearcher.doc(scoreDoc.doc); }
	 */
	private String stemTerm (String text) {
		return Stemmer.Stem(text, "English");
	}
	public void close() throws IOException {

	}
     

}
