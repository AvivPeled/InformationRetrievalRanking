package datatypes;

import file_io.QueriesFileReader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GuyK on 30/08/2015.
 */
public class QueriesSet {
    private ArrayList<Query> mQuries;

    public static QueriesSet CreateFromFile(String filename) throws IOException {

        QueriesFileReader reader = new QueriesFileReader(filename);
        reader.open();

        ArrayList<Query> queries = new ArrayList<Query>();
        String queryString = reader.nextQuery();
        int queryID = 1;

        while(queryString!=null)
        {
            queries.add(new Query(queryString,queryID));

            queryString = reader.nextQuery();
            queryID++;
        }


        QueriesSet set = new QueriesSet();
        set.mQuries = queries;
        return set;
    }

    public ArrayList<Query>  getQuries()
    {
        return mQuries;
    }

    public void print() {
        for(Query q : mQuries)
        {
            q.print();
        }
    }
}

