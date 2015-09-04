import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.LockObtainFailedException;

public class Indexer {

	private IndexWriter writer;

	public Indexer(RAMDirectory idx, String[] docs) throws IOException {
		writer = new IndexWriter(idx, new StandardAnalyzer(Version.LUCENE_36,
				Collections.emptySet()), true,
				IndexWriter.MaxFieldLength.UNLIMITED);
	}

	/**
	 * Make a Document object with an un-indexed title field and an indexed
	 * content field.
	 */
	private static Document createDocument(String title, String content) {
		Document doc = new Document();

		// Add the title as an unindexed field...

		doc.add(new Field(LuceneConstants.TITLE, title, Field.Store.YES, Field.Index.NO));

		// ...and the content as an indexed field. Note that indexed
		// Text fields are constructed using a Reader. Lucene can read
		// and index very large chunks of text, without storing the
		// entire content verbatim in the index. In this example we
		// can just wrap the content string in a StringReader.
		doc.add(new Field(LuceneConstants.CONTENTS, content, Field.Store.YES,
				Field.Index.ANALYZED, Field.TermVector.YES));

		return doc;
	}

	private void addDocs(String[] docs) throws Exception {
		for (int i = 0; i < docs.length; i++) {

			writer.addDocument(createDocument(Integer.toString(i), docs[i]));

		}

	}

	public void createIndex(String[] docs) throws Exception {
		addDocs(docs);

	}

	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

}
