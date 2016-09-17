package com.ee.match.quiz;

import java.util.Collection;

public class Word {
	public enum Type {
		FIRST, SECOND
	}

	private final int id;
	private final Type type;
	private final String word;
	private final Collection<Word> matches;

	public Word(int id, Type type, String word, Collection<Word> matches) {
		this.id = id;
		this.type = type;
		this.word = word;
		this.matches = matches;
	}

	public int getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	public String getWord() {
		return word;
	}

	public Collection<Word> getMatches() {
		return matches;
	}
}
