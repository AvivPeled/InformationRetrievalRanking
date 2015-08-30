package readingInputFiles;

import java.io.IOException;

public class Test {

	public static void main(String[]args) throws IOException
	{
		//ReadingDocsFile docsFile = new ReadingDocsFile();
		String parameterFilePath=InputConstants.PARAMETER_FILE;
		ReadingParameterFile parameterFile = new ReadingParameterFile(parameterFilePath);
		parameterFile.readFile();
		
		ReadingQueriesFile queriesFile = new ReadingQueriesFile(parameterFile.getQueryFileName());
		queriesFile.readFile();
		
		String [] dict= queriesFile.getDictonaryNumberQueryToQuery();
		
		ReadingDocsFile docsFileReader = new ReadingDocsFile(parameterFile.getDocsFileName());
		docsFileReader.readFile();
		String [] docs = docsFileReader.getDictonaryNumberQueryToQuery();
		
		CreateInputFiles createInputFiles=new CreateInputFiles(parameterFilePath);
		createInputFiles.Create("C:\\Lucene\\Index");

		int x=0;
	}
}
