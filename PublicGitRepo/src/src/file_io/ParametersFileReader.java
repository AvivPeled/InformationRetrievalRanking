package file_io;

import datatypes.RunningParameters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by GuyK on 30/08/2015.
 */
public class ParametersFileReader {
    private String mFileName;
    private BufferedReader mReader;

    public ParametersFileReader(String filename)
    {
        mFileName = filename;
    }



    public void open() throws IOException {
        mReader = new BufferedReader(new FileReader(mFileName));
    }

    public RunningParameters readParameters() throws IOException {

        HashMap<String,String> map = new HashMap<String, String>();


        String line = mReader.readLine();
        while(line!=null) {
            String[] split = line.split("=");
            map.put(split[0], split[1]);

            line = mReader.readLine();
        }


        String basePath = Paths.get(mFileName).getParent().toString();

        RunningParameters parameters = new RunningParameters(
                Paths.get(basePath,map.get("queryFile")).toString(),
                Paths.get(basePath,map.get("docsFile")).toString(),
                Paths.get(basePath,map.get("outputFile")).toString(),
                map.get("retrievalAlgorithm")
        );

        return parameters;
    }


}
