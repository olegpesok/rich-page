package com.owow.rich.services;

import com.owow.rich.items.NGram;

/**
 * Get the frequency of toekns and NGrams.
 */
public class FrequencyRetriver {

	/**
	 * 
	 */
	public final static Frequency FREQUNCY_THRESHOLD = new Frequency(0);
	
	
	/**
	 * Frequency of an NGram according to external corpus.
	 */
	public static class Frequency {
		
		private double value;
		Frequency(double value) {
			this.value = value;
		}
		public boolean isBiggerThan(Frequency other) {
			return this.value > other.value;
		}
	}
	
	/**
	 * Get the frequency for a given NGram, according to external corpus.
	 */
	public Frequency getFrequency(NGram ngram) {
		// new 
		return new Frequency(1);
	}
	
}
