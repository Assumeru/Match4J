package com.ee.match.state;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.sql.CloseableDataSource;

import com.ee.match.MatchContext;
import com.ee.match.quiz.Quiz;
import com.ee.match.quiz.Word;

public class State extends CloseableDataSource {
	private static final Logger LOG = LogManager.createLogger();

	public State(MatchContext context) {
		super(DBCPProvider.getDataSource(context));
	}

	public List<Quiz> getLists() {
		List<Quiz> lists = new ArrayList<>();
		try(Connection conn = dataSource.getConnection()) {
			ResultSet result = conn.createStatement().executeQuery("SELECT `id`, `title`, `first`, `second` FROM `lists` ORDER BY `id`");
			while(result.next()) {
				lists.add(new Quiz(result.getInt(1), result.getString(2), result.getString(3), result.getString(4)));
			}
		} catch (SQLException e) {
			LOG.e("Failed to get lists", e);
		}
		return lists;
	}

	public Quiz getList(int id) {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT `title`, `first`, `second` FROM `lists` WHERE `id` = ?");
			statement.setInt(1, id);
			ResultSet result = statement.executeQuery();
			if(result.next()) {
				Quiz quiz = new Quiz(id, result.getString(1), result.getString(2), result.getString(3), new ArrayList<>(), new ArrayList<>());
				getWords(conn, quiz);
				return quiz;
			}
		} catch (SQLException e) {
			LOG.e("Failed to get list", e);
		}
		return null;
	}

	private void getWords(Connection conn, Quiz quiz) throws SQLException {
		Map<Integer, Word> firstCache = new HashMap<>();
		Map<Integer, Word> secondCache = new HashMap<>();
		PreparedStatement statement = conn.prepareStatement("SELECT `id`, `type`, `word` FROM `words` WHERE `list` = ? ORDER BY `id`");
		statement.setInt(1, quiz.getId());
		ResultSet result = statement.executeQuery();
		while(result.next()) {
			Word word = new Word(result.getInt(1), Word.Type.values()[result.getInt(2)], result.getString(3), new ArrayList<>());
			if(word.getType() == Word.Type.FIRST) {
				firstCache.put(word.getId(), word);
				quiz.getFirstWords().add(word);
			} else {
				secondCache.put(word.getId(), word);
				quiz.getSecondWords().add(word);
			}
		}
		getMatches(conn, quiz, firstCache, secondCache);
	}

	private void getMatches(Connection conn, Quiz quiz, Map<Integer, Word> firstCache, Map<Integer, Word> secondCache) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("SELECT `first`, `second` FROM `matches` WHERE `id` = ?");
		statement.setInt(1, quiz.getId());
		ResultSet result = statement.executeQuery();
		while(result.next()) {
			Word first = firstCache.get(result.getInt(1));
			Word second = secondCache.get(result.getInt(2));
			first.getMatches().add(second);
			second.getMatches().add(first);
		}
	}
}
