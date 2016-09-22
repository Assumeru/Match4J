package com.ee.match.quiz;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class Quiz {
	private int id;
	private final String title;
	private final String first;
	private final String second;
	private String password;
	private final List<Word> firstWords;
	private final List<Word> secondWords;

	public Quiz(int id, String title, String first, String second) {
		this(id, title, first, second, null, null, null);
	}

	public Quiz(int id, String title, String first, String second, String password, List<Word> firstWords, List<Word> secondWords) {
		this.id = id;
		this.title = title;
		this.first = first;
		this.second = second;
		this.password = password;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean hasPassword() {
		return password != null;
	}

	public boolean passwordMatches(String password) {
		return BCrypt.checkpw(password, this.password);
	}
}
