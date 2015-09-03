import datatypes.*;
import file_io.ResultsFileReader;
import file_io.ResultsWriter;
import lucene_wrapping.LuceneServices;
import org.apache.lucene.queryParser.ParseException;
import services.Stopwords;

import java.io.IOException;

public class Main {

    public static boolean CHECK_TRUTH_FILE = false;
    public static GoldResults mGoldSet;

    public static void main(String[] args) throws IOException, ParseException {

        //checking arguments
        if(args.length!=1)
        {
            System.out.println("should be supplied exactly with one argument!");
            System.out.println("Usage: jar <params_file>");

            return;
        }

        //TermVector.DEBUG_MODE = true;

        //parse the params
        RunningParameters runningParameters = RunningParameters.CreateFromFileName(args[0]);
        runningParameters.print();

        //decide how many stop-words should be
        Stopwords.Instance.STOPWORDS_TOP_WORDS_COUNT =
                runningParameters.RetrievalAlgorithmType==RetrievalAlgorithm.Basic? 20 : 25 ;


        //open a results writer
        ResultsWriter writer = new ResultsWriter(runningParameters.OutputFileName);

        //index the datafile
        LuceneServices.Instance.indexDataFile(runningParameters.DocumentsFileName);

        if(CHECK_TRUTH_FILE) {
            //read gold results
            mGoldSet = ResultsFileReader.createFromFile(runningParameters.getGoldFilename());
        }

        //read the query set
        QueriesSet queriesSet = QueriesSet.CreateFromFile(runningParameters.QueryFileName);
        for(Query query : queriesSet.getQuries())
        {
            //if the algorithm is basic
            if(runningParameters.RetrievalAlgorithmType== RetrievalAlgorithm.Basic) {

                //compute cosine similarity for each document against the query
                TfidfQueryResult tfidfQueryResult = LuceneServices.Instance.queryCosineDistance(query.QueryString);

                //write it to the results file
                tfidfQueryResult.writeResultLines(query, writer);

                if (CHECK_TRUTH_FILE){
                    //insertResult against goldSet
                    mGoldSet.insertResult(query, tfidfQueryResult);
                }
            }

            //if the algorithm is improved
            if(runningParameters.RetrievalAlgorithmType == RetrievalAlgorithm.Improved)
            {
                //append the original query the same query with 's' for each word that doesn't end with s ('query expansion)

                //compute cosine similarity for each document against the query
                TfidfQueryResult tfidfQueryResult = LuceneServices.Instance.queryCosineDistance(query.getImprovedQueryString());

                //write it to the results file
                tfidfQueryResult.writeResultLines(query, writer);

                if (CHECK_TRUTH_FILE){
                    //insertResult against goldSet
                    mGoldSet.insertResult(query, tfidfQueryResult);
                }
            }
        }

        if (CHECK_TRUTH_FILE) {
            mGoldSet.printReport();
        }

        //close the writer
        writer.close();
    }
}
