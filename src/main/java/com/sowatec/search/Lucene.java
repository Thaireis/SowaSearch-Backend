package com.sowatec.search;

import java.util.List;

public class Lucene {

	public Lucene() {

	}

	String input;
	int hitAmount;
	List<String> path;

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


	/*------Filter Variables------*/


	String filterPath;
	String filterFileName;
	String filterDataType;

	List<String> ignoreList;

	public String getFilterPath() {
		return filterPath;
	}

	public void setFilterPath(String filterPath) {
		this.filterPath = filterPath;
	}

	public String getFilterFileName() {
		return filterFileName;
	}

	public void setFilterFileName(String filterFileName) {
		this.filterFileName = filterFileName;
	}

	public String getFilterDataType() {
		return filterDataType;
	}

	public void setFilterDataType(String filterDataType) {
		this.filterDataType = filterDataType;
	}


	public List<String> getIgnoreList() {
		return ignoreList;
	}

	public void setIgnoreList(List<String> ignoreList) {
		this.ignoreList = ignoreList;
	}
}
