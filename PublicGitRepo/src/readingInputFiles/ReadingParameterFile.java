package readingInputFiles;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


/*
 * The structure of the file is:
 * 		queryFile=queries.txt
 * 		docsFile=docs.txt
 * 		outpotFile=output.txt
 * 		etrievalAlgorithm=basic
 */
public class ReadingParameterFile {
	
	String parameterFileName;
	String queryFileName;
	String docsFileName;
	String outputFileName;
	String retrievalAlgorithmType;
	
	public ReadingParameterFile(String parameterFilePath)
	{
		parameterFileName = parameterFilePath;
		queryFileName = "";
		docsFileName = "";
		outputFileName = "";
		retrievalAlgorithmType = "";
	}
	
	public String getQueryFileName()
	{
		return queryFileName;
	}
	
	public String getDocsFileName()
	{
		return docsFileName;
	}
	
	public String getOutputFileName()
	{
		return outputFileName;
	}
	
	public String getRetrievalAlgorithmType()
	{
		return retrievalAlgorithmType;
	}
	
	public void readFile() throws IOException 
	{
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream(this.parameterFileName);
		
		prop.load(fis);
		
		queryFileName = prop.getProperty("queryFile");
		docsFileName = prop.getProperty("docsFile");
		outputFileName = prop.getProperty("outpotFile");
		retrievalAlgorithmType = prop.getProperty("retrievalAlgorithm");

	}
	
}
