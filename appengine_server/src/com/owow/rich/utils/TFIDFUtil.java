package com.owow.rich.utils;

import java.util.List;
import java.util.Map;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.MatchScorer;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.SortOptions;
import com.google.appengine.labs.repackaged.com.google.common.collect.Iterables;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.owow.rich.items.Token;

public class TFIDFUtil {
	public class DocumentScore {
		public String documentId;
		public String documentText;
		public double score;
		public DocumentScore(String documentId, String documentText, double score) {
			this.documentId = documentId;
			this.documentText = documentText;
			this.score = score;
		}
	}

	private final static  String INDEX_NAME = "INDEX1";
   private Index index = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName(INDEX_NAME).build());
	private TokenizerUtil tokenizeUtil = new TokenizerUtil();
   
	public List<DocumentScore> rankDocumentsSimilarityToText(String text, Map<String, String> documentIdToText, String namespace) {
		// put all the document in index
		for (String documentId : documentIdToText.keySet()) {
			Document document = Document.newBuilder().setId(documentId)
					.addField(Field.newBuilder().setName("content").setText(documentIdToText.get(documentId)))
					.addField(Field.newBuilder().setName("namespcae").setText(namespace))
					.build();
			index.put(document);
      }
	
		// Convert Query string to App Engine Text Search format
		List<Token> tokens = tokenizeUtil.tokenize(text);
		String queryString = "";
		for (int i = 0; i < tokens.size(); i++) {
			queryString += "content:" + tokens.get(i);
		    if(i < tokens.size() -1) {
		   	 queryString += " OR ";
		    }
	      }
		
		// Creates the query, in the option force to show sort score.
		Query queryObject = Query.newBuilder()
				.setOptions(QueryOptions.newBuilder().setSortOptions(
						SortOptions.newBuilder().setMatchScorer
						(MatchScorer.newBuilder().build()))).build(queryString);
	   Results<ScoredDocument> searchResults = index.search(queryObject);
	   
	   List<DocumentScore> processedResults = Lists.newArrayList();
	   for (ScoredDocument document : searchResults) {
	   	String documentText = documentIdToText.get(document.getId());
	   	Double score = Iterables.getFirst(document.getSortScores(), 0.0);
	   	processedResults.add(new DocumentScore(document.getId(), documentText, score));  
	   }
	   return processedResults;
	}

	
	// TODO(guti): this is just the test: works good.
//	public static String text1 = "A field can contain only one value, which must match its type. Field names do not have to be unique. A document can have multiple fields with the same name and same type, which is a way to represent a field with multiple values. (However, date and number fields with the same name can't be repeated.) A document can also contain multiple fields with the same name and different field types.";
//	public static String text2 = "Fields, like documents, are created using a builder. The Field.newBuilder() method returns a field builder that allows you to specify a field's name and value. The field type is automatically specified by choosing a specific set method. For example, to indicate that a field holds plain text, call setText(). The following code builds a document with fields representing a guestbook greeting.";
//	public static void quickTest() {
//		Document document = Document.newBuilder().setId("doc_id").addField(Field.newBuilder().setName("c").setText("first").build()).build();
//		Document document2 = Document.newBuilder().setId("doc_id2").addField(Field.newBuilder().setName("c").setText("Alon").build()).build();
//		Document document3 = Document.newBuilder().setId("doc_id3").addField(Field.newBuilder().setName("c").setText("first").build()).build();
//		Document document4 = Document.newBuilder().setId("doc_id4").addField(Field.newBuilder().setName("c").setText("the").build()).build();
//		Document document5 = Document.newBuilder().setId("doc_id5").addField(Field.newBuilder().setName("c").setText("first").build()).build();
//		Document document6 = Document.newBuilder().setId("doc_id6").addField(Field.newBuilder().setName("c").setText(text1).build()).build();
//		Document document7 = Document.newBuilder().setId("doc_id7").addField(Field.newBuilder().setName("c").setText(text2).build()).build();
//		Document document8 = Document.newBuilder().setId("doc_id8").addField(Field.newBuilder().setName("c").setText("aaaaaaaaaaaaaaaaa bbbbbbbbbbbbbbb ccccccccccc").build()).build();
//		Document document9 = Document.newBuilder().setId("doc_id9").addField(Field.newBuilder().setName("c").setText("aa bb cc dd ee ff gg hh ii jj kk ll mm").build()).build();
//		Document document10 = Document.newBuilder().setId("doc_id10").addField(Field.newBuilder().setName("c").setText("dddddddd eeeeeee fffffff ggggggg hhhhhh iiiiii jjjjjjjjjjjjj kkkkkkkkkkkkk lllllllll mmmmmmmmmm nnnnnnn oooooooo ppppppppp qqqqqqqqqq rrrrrrrrr ssssssssssssss tttttt aaaaaaaaaaaaa bbbbbbbbbbbbbbbb ccccccccccccccc dddddddddddddd aff afsfasdf asfasf asfasf asfasf afasfsfd ererer kggkgkg slslslf fkfsdkd fkdlfskf skfsdf sdfjljlsd fsdfjlkj fsdjfljl sdfljlkjs sdfljlkjsdf ljlkjsdf ljlsdfkjl sdlfjlkjsdf sdflkjlj sdfljlkjsdf sdfljlkjsdf lkjlsdfjlk sdflkjlj sdlfkjleiwr vlkjf").build()).build();
//		Document document11 = Document.newBuilder().setId("doc_id11").addField(Field.newBuilder().setName("c").setText("aa bb cc dd ee ff gg hh ii jj kk ll mm nn oo pp qq rr ss tt").build()).build();
//		
//		
//		String text3 = "aa bb cc dd ee ff gg hh ii jj kk ll mm nn oo pp qq rr ss tt aaaaaaaaaaaaaaaaa bbbbbbbbbbbbbbb ccccccccccc dddddddd eeeeeee fffffff ggggggg hhhhhh iiiiii jjjjjjjjjjjjj kkkkkkkkkkkkk lllllllll mmmmmmmmmm nnnnnnn oooooooo ppppppppp qqqqqqqqqq rrrrrrrrr ssssssssssssss tttttt aaaaaaaaaaaaa bbbbbbbbbbbbbbbb ccccccccccccccc dddddddddddddd aff afsfasdf asfasf asfasf asfasf afasfsfd ererer kggkgkg slslslf fkfsdkd fkdlfskf skfsdf sdfjljlsd fsdfjlkj fsdjfljl sdfljlkjs sdfljlkjsdf ljlkjsdf ljlsdfkjl sdlfjlkjsdf sdflkjlj sdfljlkjsdf sdfljlkjsdf lkjlsdfjlk sdflkjlj sdlfkjleiwr vlkjf";
//		
//		IndexSpec indexSpec = IndexSpec.newBuilder().setName("myindex").build();
//	   Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
//	   index.put(document);
//	   index.put(document2);
//	   index.put(document3);
//	   index.put(document4);
//	   index.put(document5);
//	   index.put(document6);
//	   index.put(document7);
//	   index.put(document8);
//	   index.put(document9);
//	   index.put(document10);
//	   index.put(document11);
//	   
//	   String[] t = text3.split(" ");
//	   String qr = ""; 
//	   for (int i = 0; i < t.length; i++) {
//	    qr += "c:"+t[i];
//	    if(i < t.length -1) {
//	   	 qr += " OR ";
//	    }
//      }
//	   int ln = qr.length();
//	   
//	   //"c:Alon OR c:first OR c:the OR"
//	   Query q = Query.newBuilder().setOptions(QueryOptions.newBuilder().setSortOptions(SortOptions.newBuilder().setMatchScorer(MatchScorer.newBuilder().build()))).build(qr);
//	   Results<ScoredDocument> res = index.search(q);
//	   List<ScoredDocument> list = Lists.newArrayList();
//	   List<List<Double>> score = Lists.newArrayList();
//	   for (ScoredDocument d : res) {
//	   	list.add(d);
//	   	score.add(d.getSortScores());
//	   }
//	   list = list;
//	}

	
}
