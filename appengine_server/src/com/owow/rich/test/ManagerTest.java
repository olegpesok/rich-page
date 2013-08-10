package com.owow.rich.test;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.labs.repackaged.com.google.common.collect.Maps;
import com.owow.rich.Manager;
import com.owow.rich.apiHandler.ApiResponse;
import com.owow.rich.apiHandler.ApiType;
import com.owow.rich.items.NGram;
import com.owow.rich.items.Token;
import com.owow.rich.items.WebPage;
import com.owow.rich.retriever.EntityRetriever;
import com.owow.rich.storage.Storage;
import com.owow.rich.utils.TokenizerUtil;

public class ManagerTest {
	private TokenizerUtil	      tokenizer;
	private EntityRetriever	entityRetriever;
	private Storage	      storage;
	private String	         s;
	private WebPage	      wp;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	@Before
	public void setUp() throws Exception {
		wp = new WebPage(null, null, "http://www.cnn.com/2013/07/30/tech/social-media/twitter-hate-speech/index.html?hpt=te_t1");
		set(new TokenizerUtil(), new EntityRetriever(), new Storage());
		s = "I'm rather like a mosquito in a nudist beach, I know what I want, I just don't know where to start!\n\nAnd when I grow up I want to be a teacher. \nHere I made a list of my favorite books, and a list of my favorite Ted Talks, If you are interested.\nPlease fell free to contact me, Especially, I'm looking to mentor students online, So lets talk and cooperate on that.";
	}
	public void set(TokenizerUtil tokenizer, EntityRetriever entityRetriever, Storage storage) {
		this.tokenizer = tokenizer;
		this.entityRetriever = entityRetriever;
		this.storage = storage;
	}
	@Test
	public void test() throws Exception {
//		List<Token> tokens = tokenizer.tokenize("shalti USA Google");
//		List<NGram> nGrams = tokenizer.combineToNGrams(tokens, 2);
//
//		Map<NGram, ApiResponse> entitesMap = Maps.newHashMap();
//		Manager m = new Manager();
//		for (NGram ngram : nGrams) {
//			if (entitesMap.containsKey(ngram)) continue;
//			ApiResponse entity = entityRetriever.getTopEntity(ngram, ApiType.freebase);
//
//			entitesMap.put(ngram, entity);
//		}
//		Exception e = new Exception(String.valueOf(entitesMap.size()));
//		e.printStackTrace();
//		Assert.assertTrue(entitesMap.entrySet().size() > 4);
	}
}
