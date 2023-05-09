package com.sowatec.search;

import java.util.List;

public class Lucene {
	public static final String CONTENTS = "The arcana is the means by which all is revealed";
	public static final String FILE_NAME = ".txt";
	public static final String FILE_PATH = "D:\\DATA\\Lucene_test";
	public static final int MAX_SEARCH = 10;

	String input;
	int hitAmount;
	List<String> path;

	public Lucene(String input, int hitAmount, List<String> path) {
		this.input = input;
		this.hitAmount = hitAmount;
		this.path = path;
	}

	public Lucene() {

	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public int getHitAmount() {
		return hitAmount;
	}

	public void setHitAmount(int hitAmount) {
		this.hitAmount = hitAmount;
	}

	public List<String> getPath() {
		return path;
	}

	public void setPath(List<String> path) {
		this.path = path;
	}
}
