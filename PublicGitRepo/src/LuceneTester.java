
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import readingInputFiles.CreateInputFiles;
import readingInputFiles.ReadingParameterFile;
import readingInputFiles.ReadingQueriesFile;

public class LuceneTester {
	
   static String parameterFilePath="parameterFile.txt";
   static String  dataDir= "C:\\Lucene\\Data";
   String  indexDir= "Index";
   Indexer indexer;
   
   public static void main(String[] args) throws ParseException {
      LuceneTester tester;
      try {
    	  
    	  
    	  CreateInputFiles createInputFiles=new CreateInputFiles(parameterFilePath);
  		  createInputFiles.Create(dataDir);
          tester = new LuceneTester();
          tester.createIndex();
         
          ReadingParameterFile parameterFile = new ReadingParameterFile(parameterFilePath);
  		  parameterFile.readFile();
          ReadingQueriesFile queriesFile = new ReadingQueriesFile(parameterFile.getQueryFileName());
  		  queriesFile.readFile();
  		
  		  String [] dict= queriesFile.getDictonaryNumberQueryToQuery();
  		  System.out.println(dict[3]);
          tester.search(dict[3]);
         
         
      } catch (IOException e) {
         e.printStackTrace();
      } 
   }
    
   private void createIndex() throws IOException{
      indexer = new Indexer(indexDir);
      int numIndexed;
      long startTime = System.currentTimeMillis();	
      numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
      long endTime = System.currentTimeMillis();
      indexer.close();
      System.out.println(numIndexed+" File indexed, time taken: "
         +(endTime-startTime)+" ms");		
      
   }
   
   private void search(String searchQuery) throws IOException, ParseException{
	      Searcher searcher = new Searcher(indexDir);
	      long startTime = System.currentTimeMillis();
	      TopDocs hits = searcher.search(searchQuery);
	      long endTime = System.currentTimeMillis();
	      ReadingParameterFile parametersFileReader = new ReadingParameterFile(parameterFilePath);
	      parametersFileReader.readFile();
	      System.out.println(System.getProperty("user.dir"));
	      System.out.println(hits.totalHits +
	         " documents found. Time :" + (endTime - startTime));
	      for(ScoreDoc scoreDoc : hits.scoreDocs) {
	         Document doc = searcher.getDocument(scoreDoc);
	         //Write result document in the output file
	         File file = new File(parametersFileReader.getOutputFileName());

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("File: "
			            + doc.get(LuceneConstants.FILE_PATH));
				bw.close();
	         
	      }
	      searcher.close();
	   }
}