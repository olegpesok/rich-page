package com.owow.rich.services;

import com.owow.rich.items.NGram;

public class FrequencyRetriver {

	public final static Frequency FREQUNCY_THRESHOLD = new Frequency(0);
	
	public static class Frequency {
		
		private double value;
		Frequency(double value) {
			this.value = value;
		}
		public boolean isBiggerThan(Frequency other) {
			return this.value > other.value;
		}
	}
	
	public Frequency getFrequency(NGram ngram) {
		// new 
		return new Frequency(1);
	}
	
}
