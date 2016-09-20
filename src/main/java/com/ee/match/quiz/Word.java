package com.ee.match.quiz;

import java.util.Collection;

import com.google.common.base.Objects;

public class Word {
	public enum Type {
		FIRST, SECOND
	}

	private int id;
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

	public void setId(int id) {
		this.id = id;
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

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		} else if(obj instanceof Word) {
			return Objects.equal(word, ((Word) obj).word);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 11 + 31 * Objects.hashCode(word);
	}

	@Override
	public String toString() {
		return word;
	}
}
