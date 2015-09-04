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
	static String dataDir = "Data";
	String indexDir = "Index";
	Indexer indexer;
	static RAMDirectory idx;

	public static void main(String[] args) {
		LuceneTester tester;
		try {
			// HighFreqTerms f;
			StopWords stopWords=new StopWords();
			idx = new RAMDirectory();
			CreateInputFiles createInputFiles = new CreateInputFiles(
					parameterFilePath);
			
			createInputFiles.Create(dataDir);
			String[] docs = createInputFiles.getDocs();
			tester = new LuceneTester();
			
			tester.createIndex(docs,stopWords);
			stopWords.calculateFrequency(docs, idx);
			ReadingParameterFile parameterFile = new ReadingParameterFile(
					parameterFilePath);
			parameterFile.readFile();
			FileWriter outputFileWriter = new FileWriter(parameterFile.getOutputFileName());
			ReadingQueriesFile queriesFile = new ReadingQueriesFile(
					parameterFile.getQueryFileName());
			queriesFile.readFile();
   
			String[] dict = queriesFile.getDictonaryNumberQueryToQuery();
			for(int i=0; i<dict.length; i++)
			{
				System.out.println(dict[i]);
				tester.search(i+1, dict[i],docs,stopWords, outputFileWriter);
			}
			
		outputFileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createIndex(String[] docs, StopWords stopWords) throws Exception {
		indexer = new Indexer(idx,docs,stopWords);
		indexer.createIndex(docs);
		indexer.close();
		
	}

	private void search(int queryNumber, String searchQuery, String [] docs, StopWords stopWords, FileWriter outputFileWriter) throws Exception {
		Searcher searcher = new Searcher(idx, docs,stopWords, outputFileWriter);
		searcher.search(queryNumber, searchQuery);
		searcher.close();

	}
}