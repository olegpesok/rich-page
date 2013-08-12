package com.owow.rich.items;

import java.util.Set;

import com.googlecode.objectify.annotation.Embed;
import com.owow.rich.utils.NlpUtils.Tag;

@Embed
public class ScoredResult implements Comparable<ScoredResult> {

	public Set<Tag>	matchingTagSet;
	public double		score;

	public ScoredResult(){}
	public ScoredResult(Set<Tag> mathcingTagSet, double score) {
		matchingTagSet = mathcingTagSet;
		this.score = score;
	}

	@Override
	public String toString() {
		return "score:" + score + " matching tags:" + matchingTagSet;
	}

	@Override
	public int compareTo(ScoredResult o) {
		return (int) ((score - o.score) * 10000);
	}
}
