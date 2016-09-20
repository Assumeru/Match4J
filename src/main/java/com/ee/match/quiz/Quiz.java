package com.ee.match.quiz;

import java.util.List;

public class Quiz {
	private int id;
	private final String title;
	private final String first;
	private final String second;
	private final List<Word> firstWords;
	private final List<Word> secondWords;

	public Quiz(int id, String title, String first, String second) {
		this(id, title, first, second, null, null);
	}

	public Quiz(int id, String title, String first, String second, List<Word> firstWords, List<Word> secondWords) {
		this.id = id;
		this.title = title;
		this.first = first;
		this.second = second;
		this.firstWords = firstWords;
		this.secondWords = secondWords;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public String getFirst() {
		return first;
	}

	public String getSecond() {
		return second;
	}

	public List<Word> getFirstWords() {
		return firstWords;
	}

	public List<Word> getSecondWords() {
		return secondWords;
	}
}
