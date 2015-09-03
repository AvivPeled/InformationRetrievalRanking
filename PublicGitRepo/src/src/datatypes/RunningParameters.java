package datatypes;

import file_io.ParametersFileReader;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by GuyK on 30/08/2015.
 */
public class RunningParameters {

    public static RunningParameters CreateFromFileName(String filename) throws IOException {
        ParametersFileReader reader = new ParametersFileReader(filename);
        reader.open();
        RunningParameters parameters = reader.readParameters();
        return parameters;
    }

    public String QueryFileName;
    public String DocumentsFileName;
    public String OutputFileName;
    public RetrievalAlgorithm RetrievalAlgorithmType;

    public RunningParameters(String queryFileName, String documentsFileName, String outputFileName, String retrievalAlgorithmType)
    {
        QueryFileName = queryFileName;
        DocumentsFileName = documentsFileName;
        OutputFileName = outputFileName;
        if(retrievalAlgorithmType.toLowerCase().compareTo("basic")==0)
            RetrievalAlgorithmType = RetrievalAlgorithm.Basic;
        else
            RetrievalAlgorithmType = RetrievalAlgorithm.Improved;
    }

    public void print() {
        System.out.println("Parameters Print Report");
        System.out.println("--------------------------------------------");
        System.out.println("QueryFileName           = " + QueryFileName);
        System.out.println("DocumentsFileName       = " + DocumentsFileName);
        System.out.println("OutputFileName          = " + OutputFileName);
        System.out.println("RetrievalAlgorithmType  = " + RetrievalAlgorithmType.toString());
    }

    public String getGoldFilename() {
        String basePath = Paths.get(DocumentsFileName).getParent().toString();
        String fullPath = Paths.get(basePath,"truth.txt").toString();
        return fullPath;
    }
}
