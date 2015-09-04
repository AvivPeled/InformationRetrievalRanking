import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;

import readingInputFiles.CreateInputFiles;
import readingInputFiles.ReadingParameterFile;
import readingInputFiles.ReadingQueriesFile;

public class LuceneTester {

	static String parameterFilePath = "parameterFile.txt";
	Indexer indexer;
	static RAMDirectory idx;

	public static void main(String[] args) {
		LuceneTester tester;
		try {
			// HighFreqTerms f;
			boolean improvedAlgo=false;
			idx = new RAMDirectory();
			CreateInputFiles createInputFiles = new CreateInputFiles(parameterFilePath);
	
			String[] docs = createInputFiles.getDocs();
			tester = new LuceneTester();
			
			tester.createIndex(docs);
			//stopWords.calculateFrequency(docs, idx);
			ReadingParameterFile parameterFile = new ReadingParameterFile(parameterFilePath);
			parameterFile.readFile();
			ReadingQueriesFile queriesFile = new ReadingQueriesFile(parameterFile.getQueryFileName());
			queriesFile.readFile();
   
			String[] dict = queriesFile.getDictonaryNumberQueryToQuery();
			System.out.println(dict[3]);
			if(parameterFile.getRetrievalAlgorithmType()=="improved")
			{
				improvedAlgo=true;
			}
			tester.search(dict[3],docs,improvedAlgo);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createIndex(String[] docs) throws Exception {
		indexer = new Indexer(idx,docs);
		indexer.createIndex(docs);
		indexer.close();
		
	}

	private void search(String searchQuery, String [] docs,boolean improvedAlgo) throws Exception {
		Searcher searcher = new Searcher(idx, docs, improvedAlgo);
		searcher.search(searchQuery);
		searcher.close();

	}
}