package lucene_wrapping;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import file_io.DataFileReader;

import java.io.IOException;
import java.util.Collections;

/**
 * Created by GuyK on 30/08/2015.
 */
public class DataFileLuceneIndexer {

    private IndexWriter writer;


    public DataFileLuceneIndexer(Directory indexDirectory) throws IOException {
        //this directory will contain the indexes
        //create the indexer
        writer = new IndexWriter(indexDirectory,
                new StandardAnalyzer(Version.LUCENE_36, Collections.emptySet()),true,
                IndexWriter.MaxFieldLength.UNLIMITED);
    }

    public void close() throws CorruptIndexException, IOException{
        writer.close();
    }

    public void indexFile(String filename) throws IOException{
        DataFileReader reader = new DataFileReader(filename);
        System.out.println("Indexing " + filename);
        reader.open();

        Document document = reader.nextDocument();
        while(document!=null)
        {
            writer.addDocument(document);
            document = reader.nextDocument();
        }
        close();
    }
}
