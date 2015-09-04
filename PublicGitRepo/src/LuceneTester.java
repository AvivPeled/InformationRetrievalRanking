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

	static String parameterFilePath;
	Indexer indexer;
	static RAMDirectory idx;

	public static void main(String[] args) {
		LuceneTester tester;
		try {
			// HighFreqTerms f;
			parameterFilePath = args[0];
			boolean improvedAlgo=false;
			idx = new RAMDirectory();
			CreateInputFiles createInputFiles = new CreateInputFiles(parameterFilePath);
	
			String[] docs = createInputFiles.getDocs();
			tester = new LuceneTester();
			ReadingParameterFile parameterFile = new ReadingParameterFile(parameterFilePath);
			parameterFile.readFile();

			FileWriter outputFileWriter = new FileWriter(parameterFile.getOutputFileName());
			ReadingQueriesFile queriesFile = new ReadingQueriesFile(parameterFile.getQueryFileName());

			queriesFile.readFile();


			if(parameterFile.getRetrievalAlgorithmType()=="improved")
			{
				improvedAlgo=true;
				StopWords.Instance.setNumber_stop_words(25);
				for(int i=0; i<docs.length;i++)
				{
					docs[i]=Stemmer.Stem(docs[i], "English");
				}	
			}
			
			tester.createIndex(docs);
			//stopWords.calculateFrequency(docs, idx);
		
   
			String[] dict = queriesFile.getDictonaryNumberQueryToQuery();
			for(int i=0; i<dict.length; i++)
			{
				System.out.println(dict[i]);
				
				tester.search(i+1, dict[i],docs, outputFileWriter, improvedAlgo);
			}
			System.out.println(dict[3]);
			
			

			
		outputFileWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createIndex(String[] docs) throws Exception {
		indexer = new Indexer(idx,docs);
		indexer.createIndex(docs);
		indexer.close();
		
	}


	private void search(int queryNumber, String searchQuery, String [] docs, FileWriter outputFileWriter, boolean improvedAlgo) throws Exception {
		Searcher searcher = new Searcher(idx, docs, outputFileWriter, improvedAlgo);
		searcher.search(queryNumber, searchQuery);

		searcher.close();

	}
}