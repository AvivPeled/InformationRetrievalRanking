package readingInputFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * The structure is
 * .I 1
 * .W
 * correlation between maternal and fetal plasma levels of glucose and free
 * fatty acids .
 *                                                        
 */
public class ReadingDocsFile {
	public String docsFileName;
	public String [] dictNumberDocToContent; 
	private int numberDocs;
	
	ReadingDocsFile(String docFileName)
	{
		this.docsFileName=docFileName;
		
	}
	
	public String[] getDictonaryNumberQueryToQuery()
	{
		return dictNumberDocToContent;
	}
	
	public void readFile() throws IOException
	{
		String content;int index=1;
		String [] numberQueryWithConent;
		String [] splitedContentByIndex;
		byte[] bytesContentFile;
		Path pathParameterFile = Paths.get(docsFileName);
		bytesContentFile = Files.readAllBytes(pathParameterFile);
		content = new String(bytesContentFile);
		
		splitedContentByIndex = content.split(".I");
		numberDocs=splitedContentByIndex.length - 1;
		dictNumberDocToContent = new String [numberDocs];
		while(index <= numberDocs)
		{
			numberQueryWithConent = splitedContentByIndex[index].split(".W"+InputConstants.LINE_FEED);
			if(isNumeric(numberQueryWithConent[0]))
				{
					dictNumberDocToContent[index-1]=numberQueryWithConent[1].replace(InputConstants.LINE_FEED, " ");
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
