package datatypes;

/**
 * Created by GuyK on 30/08/2015.
 */
public class TermVectorWithCosineSimilarity {
    //take a query string and build a normalized term vector from it.
    //take all the document term frequency and build normalized vector.
    //compute for each correlating term, the multiplication of them and sum it to return cosine distance.

    public TermVector TermVector;
    public Double CosineSimilarity;

    public TermVectorWithCosineSimilarity(TermVector termVector, Double cosineSimilarity) {
        TermVector = termVector;
        CosineSimilarity = cosineSimilarity;
    }
}
