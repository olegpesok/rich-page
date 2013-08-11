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

	public static int getTitleSimilarityScore(String title, String ngram) {
		if (title.equals(ngram)){
      	return 10;
      }
		
		if ( title.toLowerCase().equals(ngram.toLowerCase()) ){
      	return 9;
      }
		
		
		if(ngram.toLowerCase().contains(title.toLowerCase())){
      	return 8;
      }
		
		if(title.toLowerCase().contains(ngram.toLowerCase())){
      	return 7;
      }
		
		int titlesCloseness = getCloseness(title, ngram);
		if(titlesCloseness  <= 1){
      	return 6;
      }
		if(titlesCloseness  <= 2){
      	return 5;
      }
		if(titlesCloseness  <= 3){
      	return 4;
      }
		
	   for (String word : title.split(" ")) {
	      if(getCloseness(word, ngram) <= 3){
	      	return 2;
	      }
      }
	   
	   for (String word : ngram.split(" ")) {
	      if(getCloseness(word, title) <= 3){
	      	return 1;
	      }
      }
	   
	   return 0;
   }

	private static int getCloseness(String word, String title) {
		return StringCompareUtils.computeLevenshteinDistance(word.toLowerCase(), title.toLowerCase());
	}

}
