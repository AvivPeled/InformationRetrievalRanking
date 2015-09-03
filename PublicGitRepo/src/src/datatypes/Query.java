package datatypes;

public class Query
{
    public String QueryString;
    public int QueryID;


    public Query(String queryString, int queryID){
        QueryString = queryString;
        QueryID = queryID;

    }

    public void print() {
        System.out.println("Query #" + QueryID + ": [" + QueryString.replace("\n"," ") + "]");
    }

    public String getImprovedQueryString() {
        StringBuilder sb = new StringBuilder();
        for(String s : QueryString.split(" ")){
            boolean changed=false;

            if(s.length() >= 4 && s.endsWith("s")){
                sb.append(s.substring(0,s.length()-1));
                changed = true;
            }
            if(s.length() >= 5 && s.endsWith("ed")){
                sb.append(s.substring(0,s.length()-2));
                changed = true;
            }


            if(changed==false)
                sb.append(s);

            sb.append(" ");
        }

        return QueryString + " " + sb.toString();
    }
}
