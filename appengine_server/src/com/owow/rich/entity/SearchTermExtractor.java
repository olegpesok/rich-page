package com.owow.rich.entity;

import java.util.List;
import java.util.Set;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.google.appengine.labs.repackaged.com.google.common.collect.Sets;
import com.owow.rich.items.Highlight;
import com.owow.rich.items.SearchTerm;

public class SearchTermExtractor {

	private final EntityExtractor	entityExtractor;
	// private final NameExtractor nameExtractor = new NameExtractor();

	public SearchTermExtractor( ) {
		this(new EntityExtractor());
	}

	public SearchTermExtractor(final EntityExtractor entityExtractor) {
		this.entityExtractor = entityExtractor;
	}

	/**
	 * Extract {@link SearchTerm} to search for from the highlight text and the
	 * rest of the text.
	 */
	public List<SearchTerm> extractTerms(final Highlight highlight) {
		fixPartOfWordHighlight(highlight);
		List<SearchTerm> searchTerms = Lists.newArrayList();

		// We first take the highlight as a term:
		searchTerms.add(SearchTerm.create(highlight.highlight));
		// We then extract entities from the highlight:
		searchTerms.addAll(entityExtractor.extract(highlight.highlight));
		// We then extract names from the highlight:
		// searchTerms.addAll(nameExtractor.extract(highlight.highlight));
		// We then extract entities from the text:
		// searchTerms.addAll(entityExtractor.extract(highlight.text));
		// We then extract entities from the text:
		// searchTerms.addAll(nameExtractor.extract(highlight.text));

		trimTerms(searchTerms);
		searchTerms = deDupTerms(searchTerms);

		return searchTerms;
	}

	private void fixPartOfWordHighlight(final Highlight highlight) {
		//
		while (highlight.startIndex > 0 && highlight.text.charAt(highlight.startIndex - 1) != ' ')
			highlight.startIndex--;

		final List<Character> chars = Lists.newArrayList(' ', ',', '.');

		while (highlight.endIndex < highlight.text.length()
		      && !chars.contains(highlight.text.charAt(highlight.endIndex)))
			highlight.endIndex++;

		highlight.highlight = highlight.text.substring(highlight.startIndex, highlight.endIndex);
	}

	private void trimTerms(final List<SearchTerm> finalResults) {
		for (final SearchTerm searchTerm : finalResults) {
			searchTerm.term = searchTerm.term.trim();
			// remove the 's
			if (searchTerm.term.endsWith("�s")) searchTerm.term = searchTerm.term.substring(0, searchTerm.term.length() - 2);
		}
	}

	private List<SearchTerm> deDupTerms(final List<SearchTerm> list) {
		final List<SearchTerm> newList = Lists.newArrayList();
		final Set<String> terms = Sets.newHashSet();
		for (final SearchTerm searchTerm : list)
			if (!terms.contains(searchTerm.term)) {
				newList.add(searchTerm);
				terms.add(searchTerm.term);
			}
		return newList;
	}

	public static List<SearchTerm> extractAllTerms(Highlight highlight) {
		return new SearchTermExtractor(new EntityExtractor()).extractTerms(highlight);
   }
}
