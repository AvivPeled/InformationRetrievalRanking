package datatypes;

import java.util.ArrayList;

public class GoldQueryResult {
    public int QueryID;
    public ArrayList<Integer> RelatedDocuments;

    public GoldQueryResult(int queryID)
    {
        QueryID = queryID;
        RelatedDocuments = new ArrayList<Integer>();
    }

}
