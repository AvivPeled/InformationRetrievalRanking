
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
	
   static String parameterFilePath="parameterFile.txt";
   static String  dataDir= "Data";
   String  indexDir= "Index";
   Indexer indexer;
   static RAMDirectory idx;
   public static void main(String[] args)  {
      LuceneTester tester;
      try {
    	  
    	  idx = new RAMDirectory();
    	  CreateInputFiles createInputFiles=new CreateInputFiles(parameterFilePath);
  		  createInputFiles.Create(dataDir);
  		  String [] docs=createInputFiles.getDocs();
          tester = new LuceneTester();
          tester.createIndex(docs);
         
          ReadingParameterFile parameterFile = new ReadingParameterFile(parameterFilePath);
  		  parameterFile.readFile();
          ReadingQueriesFile queriesFile = new ReadingQueriesFile(parameterFile.getQueryFileName());
  		  queriesFile.readFile();
  		
  		  String [] dict= queriesFile.getDictonaryNumberQueryToQuery();
  		  System.out.println(dict[3]);
         
  		  tester.search(dict[3]);
         StopWords stopWords=new StopWords();
         stopWords.computeTopTermQuery(idx);
         List<String> topTerms=stopWords.getTopTerms();
         Map<String,Integer> frequencyMap= stopWords.getFrequencyMap();
      } catch (Exception e) {
         e.printStackTrace();
      } 
   }
    
   private void createIndex(String [] docs) throws IOException{
      indexer = new Indexer(idx);
      indexer.createIndex(docs);
      indexer.close();      
   }
   
   private void search(String searchQuery) throws IOException, ParseException{
	      Searcher searcher = new Searcher(idx);
	     searcher.search(searchQuery);
	      searcher.close();
	   }
}