package readingInputFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReadingQueriesFile {
	public String queriesFileName;
	public String [] dictNumberQueryToQuery; 
	int numberQueries;
	
	public ReadingQueriesFile(String queryFileName)
	{
		this.queriesFileName = queryFileName;
	}
	
	public String[] getDictonaryNumberQueryToQuery()
	{
		return dictNumberQueryToQuery;
	}
	
	public void readFile() throws IOException
	{
		String content;
		int index = 1;
		String [] numberQueryWithConent;
		String [] splitedContentByIndex;
		byte[] bytesContentFile;
		Path pathParameterFile = Paths.get(queriesFileName);
		bytesContentFile = Files.readAllBytes(pathParameterFile);
		content = new String(bytesContentFile);
		
		splitedContentByIndex = content.split(".I");
		numberQueries = splitedContentByIndex.length - 1;
		dictNumberQueryToQuery = new String [numberQueries];
		while(index <= numberQueries)
		{
			numberQueryWithConent = splitedContentByIndex[index].split(".W"+InputConstants.LINE_FEED);
			if(isNumeric(numberQueryWithConent[0]))
				{
				
					dictNumberQueryToQuery[index-1]=numberQueryWithConent[1].replace(InputConstants.LINE_FEED, " ");
				}
			index++;
		}
		
		
	}
	
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
}
