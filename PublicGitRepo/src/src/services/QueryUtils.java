package services;

import java.util.*;

/**
 * Created by GuyK on 30/08/2015.
 */
public class QueryUtils {

    private static ArrayList<Character> mDelimiters = new ArrayList<Character>();

    public static QueryUtils Instance = new QueryUtils();
    private QueryUtils()
    {
        char[] delimiters = new char[]{'?','!',',','.','\t','|',':','(',')','[',']','\n','\r'};
        for(char d : delimiters)
        {
            mDelimiters.add(d);
        }
    }

    public String normalize(String w) {
        StringBuilder sb = new StringBuilder();
        for(char c : w.toCharArray())
        {
            if(mDelimiters.contains(c)==false)
                sb.append(c);
        }
        return sb.toString().toLowerCase();
    }

    public String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

}

