import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.LockObtainFailedException;

public class Indexer {

	private IndexWriter writer;

	public Indexer(RAMDirectory idx) throws IOException{
		 
		// Construct a RAMDirectory to hold the in-memory representation
	      // of the index.
	      
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36); 
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
		writer = new IndexWriter(idx, config); 
	       
	}

	      /**
	       * Make a Document object with an un-indexed title field and an
	       * indexed content field.
	       */
	      private static Document createDocument(String title, String content) {
	         Document doc = new Document();
	    
	         // Add the title as an unindexed field...
	    
	         doc.add(new Field("title", title, Field.Store.YES, Field.Index.NO));
	    
	    
	         // ...and the content as an indexed field. Note that indexed
	         // Text fields are constructed using a Reader. Lucene can read
	         // and index very large chunks of text, without storing the
	         // entire content verbatim in the index. In this example we
	         // can just wrap the content string in a StringReader.
	         doc.add(new Field("content", content, Field.Store.YES, Field.Index.ANALYZED));
	    
	         return doc;
	      }
	      
	      
	private static void addDoc(IndexWriter w, String text, String id) throws IOException {
		  	Document doc = new Document();
		  	Document document = new Document();

			//index file contents
			Field contentField = new Field(LuceneConstants.CONTENTS,text,Field.Store.YES,Index.ANALYZED);
			//index file name
			Field fileNameField = new Field(LuceneConstants.FILE_NAME,id,Field.Store.YES,Field.Index.NOT_ANALYZED);
			//index file path
			
			document.add(contentField);
			document.add(fileNameField);
	
			w.addDocument(doc);
		}
	
	@SuppressWarnings("deprecation")
	private Document getDocument(File file) throws IOException
	{
		Document document = new Document();

		//index file contents
		Field contentField = new Field(LuceneConstants.CONTENTS,new FileReader(file));
		//index file name
		Field fileNameField = new Field(LuceneConstants.FILE_NAME,file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED);
		//index file path
		Field filePathField = new Field(LuceneConstants.FILE_PATH,file.getCanonicalPath(),Field.Store.YES,Field.Index.NOT_ANALYZED);

		document.add(contentField);
		document.add(fileNameField);
		document.add(filePathField);

		return document;
	}

   private void indexFile(File file) throws IOException{
	      System.out.println("Indexing "+file.getCanonicalPath());
	      Document document = getDocument(file);
	      writer.addDocument(document);
	   }

   
   private void addDocs(String [] docs) throws CorruptIndexException, IOException
   {
	   for(int i=0;i<docs.length;i++)
	   {
		   writer.addDocument(createDocument(Integer.toString(i),docs[i]));
		   
	   }

   }
	public void createIndex(String [] docs) 
      throws IOException{
		addDocs(docs);
     
	   }
     
	

	public void close() throws CorruptIndexException, IOException{
    writer.close();
	}
}