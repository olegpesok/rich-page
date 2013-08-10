package com.owow.rich.utils;

import com.owow.rich.items.NGram;

public class StringCompareUtils {
	// string distance
   private static int minimum(int a, int b, int c) {
           return Math.min(Math.min(a, b), c);
   }
   
   public static int computeLevenshteinDistance(CharSequence str1,
                   CharSequence str2) {
           int[][] distance = new int[str1.length() + 1][str2.length() + 1];

           for (int i = 0; i <= str1.length(); i++)
                   distance[i][0] = i;
           for (int j = 1; j <= str2.length(); j++)
                   distance[0][j] = j;

           for (int i = 1; i <= str1.length(); i++)
                   for (int j = 1; j <= str2.length(); j++)
                           distance[i][j] = minimum(
                                           distance[i - 1][j] + 1,
                                           distance[i][j - 1] + 1,
                                           distance[i - 1][j - 1]
                                                           + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                                                                           : 1));

           return distance[str1.length()][str2.length()];
   }

	public static boolean isMatch(String title, String ngram) {
		if(title.toLowerCase().contains(ngram.toLowerCase())){
      	return true;
      }
		
		if(ngram.toLowerCase().contains(title.toLowerCase())){
      	return true;
      }
		
		if(close(title, ngram)){
      	return true;
      }
	   for (String word : title.split(" ")) {
	      if(close(word, ngram)){
	      	return true;
	      }
      }
	   
	   for (String word : ngram.split(" ")) {
	      if(close(word, title)){
	      	return true;
	      }
      }
	   return false;
   }

	private static boolean close(String word, String title) {
		int d = StringCompareUtils.computeLevenshteinDistance(word.toLowerCase(), title.toLowerCase());
		if(d <= 2) {
			return true;
		}
		return false;
   }

}
