
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
	
   public static void main(String[] args)  {
      LuceneTester tester;
      String parameterFilePath=args[0];
      String  dataDir= "Data";
      String  indexDir= "Index";
      Indexer indexer;
      RAMDirectory idx;
      try {
    	  
    	  idx = new RAMDirectory();
    	  CreateInputFiles createInputFiles=new CreateInputFiles(parameterFilePath);
  		  createInputFiles.Create(dataDir);
  		  String [] docs=createInputFiles.getDocs();
          tester = new LuceneTester();
          indexer = tester.createIndex(docs, idx);
         
          ReadingParameterFile parameterFile = new ReadingParameterFile(parameterFilePath);
  		  parameterFile.readFile();
          ReadingQueriesFile queriesFile = new ReadingQueriesFile(parameterFile.getQueryFileName());
  		  queriesFile.readFile();
  		
  		  String [] dict= queriesFile.getDictonaryNumberQueryToQuery();
  		  System.out.println("The fourth query is:"+dict[3]);
         
  		  tester.search(dict[3], idx);
  		  
  		  
  		  
  		  tester.evaluateResults(parameterFile.getOutputFileName());
         //StopWords stopWords=new StopWords();
     //    stopWords.computeTopTermQuery(idx);
      //   List<String> topTerms=stopWords.getTopTerms();
       //  Map<String,Integer> frequencyMap= stopWords.getFrequencyMap();
      } catch (Exception e) {
         e.printStackTrace();
      } 
   }
    
   private void evaluateResults(String outputFilePath){
	   
   }
   private Indexer createIndex(String [] docs, RAMDirectory idx) throws IOException{
      Indexer indexer = new Indexer(idx);
      indexer.createIndex(docs);
      indexer.close();
      return indexer;
   }
   
   private void search(String searchQuery, RAMDirectory idx) throws IOException, ParseException{
	      Searcher searcher = new Searcher(idx);
	      searcher.search(searchQuery);
	      searcher.close();
	   }
}