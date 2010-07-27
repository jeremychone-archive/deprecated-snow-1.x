/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static String deCamelize(String camelString) {
        StringBuilder sb = new StringBuilder();

        String regExFindUpperCase = "([A-Z]|(\\d*\\d))";

        Pattern myPattern = Pattern.compile(regExFindUpperCase);
        Matcher m = myPattern.matcher(camelString);
        ArrayList<Integer> idxs = new ArrayList<Integer>();
        while (m.find()) {            
            idxs.add(m.start());
        }
        
        int lastIdx = 0;
        String lastString = null;
        for (int idx : idxs) {
            //do not add a space if: 
            // 1) there is no previous string (it is the first string) >> lastString != null
            // 2) the last string was empty or one char (i.e. do not add space between two upper case) >> lastString.length() > 1
            // 3) if the lastString ended with a space
            if (lastString != null && lastString.length() > 1 && lastString.charAt(lastString.length() -1) != ' ') {
                sb.append(' ');
            }
            lastString = camelString.substring(lastIdx, idx);
            sb.append(lastString);
            
            lastIdx = idx;
        }
        // add the last one
        if (lastIdx < camelString.length()) {
            if (lastString != null && lastString.length() > 1 && lastString.charAt(lastString.length() -1) != ' ') {
                sb.append(' ');
            }
            sb.append(camelString.substring(lastIdx));
        }

        return sb.toString();
    }
    
    public static String extractFirstMatch(String content,String regEx){
        return null;
    }

    /**
     * Returns true if the string to check ends with the specified suffix; this check is case-insensitive. If both
     * strings are null, then true is returned. Otherwise, <var>toCheck</var> must end with <var>suffix</var>,
     * disregarding case.
     * 
     * @param toCheck
     *            the string to check to see if it ends with the suffix
     * @param suffix
     *            what the string to check should end with
     * @return true the string to check ends with the suffix without regard to case, or if both strings are null
     */
    public static boolean endsWithIgnoreCase(String toCheck, String suffix) {
        boolean endsWith;
        if (toCheck == suffix) {
            endsWith = true;
        } else if (toCheck == null || suffix == null) {
            endsWith = false;
        } else {
            // toCheck and suffix are both non-null
            toCheck = toCheck.toLowerCase();
            suffix = suffix.toLowerCase();
            endsWith = toCheck.endsWith(suffix);
        }
        return endsWith;
    }

    /**
     * Return the a string containing only the Alpha char of the string str.
     * 
     * @param str
     *            The string. Cannot be null.
     * @return
     */
    public static String getAlphaOnly(String str) {
        assert str != null : "str cannot be null";

        final char[] chars = str.toCharArray();
        final char[] newChars = new char[chars.length];

        int length = 0;
        for (int x = 0; x < chars.length; x++) {
            final char c = chars[x];
            if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'))) {
                newChars[length] = c;
                length++;
            }

        }

        return new String(newChars, 0, length);
    }
    
    
    public static int countOccurences(String regex, String content){
        Matcher m = Pattern.compile(regex).matcher(content);
        int count = 0;
        while (m.find()){
            count++;
        }
        return count;
    }

}
