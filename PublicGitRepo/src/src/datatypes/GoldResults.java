package datatypes;

import services.QueryUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by GuyK on 31/08/2015.
 */
public class GoldResults {
    private HashMap<Integer, GoldQueryResult> mGoldenResults;
    private HashMap<Integer, ActualQueryResult> mActualResults;

    public GoldResults()
    {
        mActualResults = new HashMap<Integer, ActualQueryResult>();
    }

    public static GoldResults CreateFromLines(ArrayList<String> lines)
    {
        HashMap<Integer, GoldQueryResult> map = new HashMap<Integer, GoldQueryResult>();
        for(String line : lines){
            String[] split = line.split(" ");
            int queryID = Integer.parseInt(split[0]);
            int documentID = Integer.parseInt(split[2]);

            if(map.containsKey(queryID)==false)
                map.put(queryID, new GoldQueryResult(queryID));

            map.get(queryID).RelatedDocuments.add(documentID);
        }

        GoldResults results = new GoldResults();
        results.mGoldenResults = map;

        return results;
    }

    public double insertResult(Query query, TfidfQueryResult tfidfQueryResult) {
        mActualResults.put(query.QueryID, new ActualQueryResult(query.QueryID, mGoldenResults.get(query.QueryID),tfidfQueryResult));
        return mActualResults.get(query.QueryID).getPrecision();
    }

    public void printReport() {
        double prec_sum=0;
        double recall_sum=0;

        double totalQueryCount=0;
        double totalHits=0;
        double totalGoldSetHits=0;
        double totalActualResultsSetSize=0;
        double sum_Top5_Percent=0;
        double sum_Top10_Percent=0;

        for(ActualQueryResult result : mActualResults.values()) {
            //result.print();
            result.printTop5And10();

            prec_sum += result.getPrecision();
            recall_sum += result.getRecall();

            totalQueryCount ++;
            totalHits += result.getMatchingCountBetweenResultSetAndGoldSet();
            totalGoldSetHits += result.getGoldSetSize();
            totalActualResultsSetSize += result.getActualResultsSetSize();

            sum_Top5_Percent += result.getTop5Percent();
            sum_Top10_Percent += result.getTop10Percent();
        }

        System.out.println("---------------------------------------------------------------------");
        System.out.println("TOTAL AVG PRECISION: [" + prec_sum + "/" + totalQueryCount + "] = " + prec_sum/totalQueryCount + "%");
        System.out.println("TOTAL OVERALL PRECISION, HITS/TOTAL: [" + totalHits + "/" + totalGoldSetHits + "] = " + (totalHits/totalGoldSetHits)*100 + "%");
        System.out.println();
        System.out.println("TOTAL AVG RECALL: [" + recall_sum + "/" + totalQueryCount + "] = " + recall_sum/totalQueryCount + "%");
        System.out.println("TOTAL OVERALL RECALL, HITS/TOTAL: [" + totalHits + "/" + totalActualResultsSetSize + "] = " + (totalHits/totalActualResultsSetSize)*100 + "%");
        System.out.println();
        System.out.println("TOTAL TOP5-PRECISION: [" + sum_Top5_Percent + "/" + totalQueryCount + "] = " + sum_Top5_Percent/totalQueryCount + "%");
        System.out.println("TOTAL TOP10-PRECISION: [" + sum_Top10_Percent + "/" + totalQueryCount + "] = " + sum_Top10_Percent/totalQueryCount + "%");
    }
}

class ActualQueryResult {
    public int QueryID;
    public GoldQueryResult GoldQueryResult;
    public ArrayList<Integer> ActualRelatedDocuments;
    public ArrayList<Integer> NonRelatedDocuments;
    public int Top5_Hits;
    public int Top10_Hits;


    public double getTop5Percent(){
        return ((double)Top5_Hits / 5)*100;
    }

    public double getTop10Percent(){
        return ((double)Top10_Hits / 10)*100;
    }

    public int getMatchingCountBetweenResultSetAndGoldSet(){
        return ActualRelatedDocuments.size();
    }

    public int getGoldSetSize(){
        return GoldQueryResult.RelatedDocuments.size();
    }

    public int getNonMatchingDocument(){
        return NonRelatedDocuments.size();
    }

    public int getActualResultsSetSize(){
        return NonRelatedDocuments.size() + ActualRelatedDocuments.size();
    }

    public double getPrecision(){
        double matching = getMatchingCountBetweenResultSetAndGoldSet();
        double total = getGoldSetSize();

        if(total==0)
            return 0;

        double res = (matching /total)*100;
        return res;
    }

    public double getRecall(){
        double matching = getMatchingCountBetweenResultSetAndGoldSet();
        double total = getActualResultsSetSize();

        if(total==0)
            return 0;

        double res = (matching /total)*100;
        return res;
    }

    public ActualQueryResult(int queryID, GoldQueryResult goldQueryResult, TfidfQueryResult tfidfQueryResult)
    {
        QueryID = queryID;
        GoldQueryResult = goldQueryResult;
        ActualRelatedDocuments = new ArrayList<Integer>();
        NonRelatedDocuments = new ArrayList<Integer>();

        int count=1;
        //the vector is sorted
        for(TermVectorWithCosineSimilarity vec : tfidfQueryResult.getVectorWithCosineSimilarities()){

            int docID = vec.TermVector.getDocumentID();
            if(GoldQueryResult.RelatedDocuments.contains(docID)){
                ActualRelatedDocuments.add(docID);

                if(count<=5){
                    Top5_Hits++;
                }
                if(count >5 && count<=10){
                    Top10_Hits++;
                }
            }
            else{
                NonRelatedDocuments.add(docID);
            }

            count++;
        }
    }

    public void print()
    {
        System.out.print("Query #" + QueryID + ": PRECISION: [" + getMatchingCountBetweenResultSetAndGoldSet() + "/" + getGoldSetSize() + "] ==>  " + getPrecision() + "%");
        System.out.print(", RECALL: [" + getMatchingCountBetweenResultSetAndGoldSet() + "/" + getActualResultsSetSize() + "] ==>  " + getRecall() + "%");
        System.out.println();
    }

    public void printTop5And10() {

        System.out.print("Query #" + QueryUtils.Instance.padRight(""+QueryID,2) + ": TOP5_PRECISION: [" + Top5_Hits + "/" + 5 + "]=" + ((double)Top5_Hits / 5)*100 + "%");
        System.out.println(" | TOP10_PRECISION: [" + Top10_Hits + "/" + 10 + "]=" + ((double) Top10_Hits / 10) * 100 + "%");

        //System.out.println();
    }
}

