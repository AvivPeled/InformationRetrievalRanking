import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;


public class StopWords {


	Map<String, Integer> frequencyCountMap;
	ArrayList<String> stopWords;

	public StopWords() {
		frequencyCountMap = new HashMap<String, Integer>();
		// retrieve the top terms based on topTermCutoff
		stopWords = new ArrayList<String>();
	}

	public Map<String, Integer> getFrequencyMap() {
		return frequencyCountMap;
	}

	public List<String> getTopTerms() {
		return stopWords;
	}
	
	@SuppressWarnings("resource")
	public  String removeStopWords(String textFile) throws Exception {
		 TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_36, new StringReader(textFile));
		 Set stopWordsSet = StopFilter.makeStopSet(Version.LUCENE_36, stopWords, true);
		 tokenStream = new StopFilter(Version.LUCENE_36, tokenStream, stopWordsSet);
	      
	    StringBuilder sb = new StringBuilder();
	    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
	    tokenStream.reset();
	    while (tokenStream.incrementToken()) {
	        String term = charTermAttribute.toString();
	        sb.append(term + " ");
	    }
	    return sb.toString();
	}
	
	 public void calculateFrequency(String [] docs,RAMDirectory idx) throws IOException{
		   IndexReader ir = IndexReader.open(idx);
		    
	    
		    for(int docNum=0; docNum < ir.numDocs(); docNum++) {
		        TermFreqVector termFreqVector = ir.getTermFreqVector(docNum, LuceneConstants.CONTENTS);
		       
		                
		        String terms[] = termFreqVector.getTerms();
		        int freqs[] = termFreqVector.getTermFrequencies();
		        int totalEntries = terms.length;

		        //build a map from them
		        Map<String, Integer> termFrequencyMap = new HashMap<String, Integer>();
		 
		        for(int i=0; i<totalEntries;i++)
		        {
		            String word = terms[i];
		                Integer frequency = freqs[i];
		                termFrequencyMap.put(word, frequency);
		            
		        }
		        
		        for(Map.Entry<String, Integer> entry : termFrequencyMap.entrySet()){
		            Integer count = frequencyCountMap.get(entry.getKey());
		            count = (count==null) ? entry.getValue() : count + entry.getValue();
		            frequencyCountMap.put(entry.getKey(),count);
		        }
		    }
		    stopWords = buildStopwordsList(20);
		    printStopWords(stopWords);
		    
		   
		}

	 private ArrayList<String> buildStopwordsList(int topCount) {
	        if(topCount==0)
	            return new ArrayList<String>();

	        ArrayList<String> result = new ArrayList<String>();
	        Map<String, Integer> sortByValue = SortingComparator.sortByValueDescending(frequencyCountMap);
	        int count = 0;

	        for(Map.Entry<String,Integer> entry : sortByValue.entrySet())
	        {
	            result.add(entry.getKey());
	            count++;

	            if(count==topCount)
	                break;
	        }

	        return result;
	    }

	    private void printStopWords(ArrayList<String> mStopWords ) {
	        System.out.print("StopWords: [");
	        for(String word : mStopWords)
	        {
	            System.out.print(word + ", ");
	        }
	        System.out.println("]");
	    }

	
}
