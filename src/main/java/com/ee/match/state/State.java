package com.ee.match.state;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.ee.collection.MapSet;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.sql.CloseableDataSource;
import org.ee.sql.PreparedStatementBuilder;

import com.ee.match.MatchContext;
import com.ee.match.exception.StateException;
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

	public void removeList(Quiz quiz) throws StateException {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("DELETE FROM `lists` WHERE `id` = ?");
			statement.setInt(1, quiz.getId());
			statement.execute();
			conn.commit();
		} catch(SQLException e) {
			throw new StateException("Failed to delete list " + quiz.getId(), e);
		}
	}

	public void modifyList(Quiz quiz, Quiz edited) throws StateException {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("UPDATE `lists` SET `title` = ?, `first` = ?, `second` = ? WHERE `id` = ?");
			statement.setString(1, edited.getTitle());
			statement.setString(2, edited.getFirst());
			statement.setString(3, edited.getSecond());
			statement.setInt(4, quiz.getId());
			statement.execute();
			updateWords(conn, quiz, edited);
			conn.commit();
		} catch(SQLException e) {
			throw new StateException("Failed to save changes to " + quiz.getId(), e);
		}
	}

	private void updateWords(Connection conn, Quiz quiz, Quiz edited) throws SQLException {
		MapSet<Word> newFirst = new MapSet<>(edited.getFirstWords());
		MapSet<Word> newSecond = new MapSet<>(edited.getSecondWords());
		List<Word> toDelete = new ArrayList<>(0);
		for(Word word : quiz.getFirstWords()) {
			if(newFirst.contains(word)) {
				newFirst.get(word).setId(word.getId());
				newFirst.remove(word);
			} else {
				toDelete.add(word);
			}
		}
		for(Word word : quiz.getSecondWords()) {
			if(newSecond.contains(word)) {
				newSecond.get(word).setId(word.getId());
				newSecond.remove(word);
			} else {
				toDelete.add(word);
			}
		}
		PreparedStatement statement = conn.prepareStatement("DELETE FROM `matches` WHERE `id` = ?");
		statement.setInt(1, quiz.getId());
		statement.execute();
		if(!toDelete.isEmpty()) {
			deleteWords(conn, toDelete);
		}
		if(newFirst.size() + newSecond.size() > 0) {
			saveWords(conn, quiz.getId(), newFirst, newSecond);
		}
		saveMatches(conn, edited);
	}

	private void deleteWords(Connection conn, List<Word> toDelete) throws SQLException {
		PreparedStatement statement = new PreparedStatementBuilder("DELETE FROM `words` WHERE `id` = IN(").appendParameters(toDelete.size()).append(')').build(conn);
		for(int i = 0; i < toDelete.size(); i++) {
			statement.setInt(1 + i, toDelete.get(i).getId());
		}
		statement.execute();
	}

	public void saveList(Quiz quiz) throws StateException {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("INSERT INTO `lists` (`title`, `first`, `second`, `password`) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, quiz.getTitle());
			statement.setString(2, quiz.getFirst());
			statement.setString(3, quiz.getSecond());
			statement.setString(4, null);
			statement.execute();
			ResultSet keys = statement.getGeneratedKeys();
			keys.next();
			quiz.setId(keys.getInt(1));
			saveWords(conn, quiz);
			saveMatches(conn, quiz);
			conn.commit();
		} catch(SQLException e) {
			throw new StateException("Failed to save list", e);
		}
	}

	private void saveWords(Connection conn, Quiz quiz) throws SQLException {
		saveWords(conn, quiz.getId(), quiz.getFirstWords(), quiz.getSecondWords());
	}

	private void saveWords(Connection conn, int id, Collection<Word> firstWords, Collection<Word> secondWords) throws SQLException {
		StringBuilder query = new StringBuilder("INSERT INTO `words` (`list`, `type`, `word`) VALUES(?, ?, ?)");
		int words = firstWords.size() + secondWords.size();
		for(int i = 1; i < words; i++) {
			query.append(", (?, ?, ?)");
		}
		PreparedStatement statement = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
		int i = 1;
		for(Word word : firstWords) {
			statement.setInt(i++, id);
			statement.setInt(i++, word.getType().ordinal());
			statement.setString(i++, word.getWord());
		}
		for(Word word : secondWords) {
			statement.setInt(i++, id);
			statement.setInt(i++, word.getType().ordinal());
			statement.setString(i++, word.getWord());
		}
		statement.execute();
		ResultSet keys = statement.getGeneratedKeys();
		for(Word word : firstWords) {
			keys.next();
			word.setId(keys.getInt(1));
		}
		for(Word word : secondWords) {
			keys.next();
			word.setId(keys.getInt(1));
		}
	}

	private void saveMatches(Connection conn, Quiz quiz) throws SQLException {
		StringBuilder query = new StringBuilder("INSERT INTO `matches` (`id`, `first`, `second`) VALUES");
		boolean comma = false;
		for(Word first : quiz.getFirstWords()) {
			for(int i = 0; i < first.getMatches().size(); i++) {
				if(comma) {
					query.append(',');
				} else {
					comma = true;
				}
				query.append("(?, ?, ?)");
			}
		}
		int i = 1;
		PreparedStatement statement = conn.prepareStatement(query.toString());
		for(Word first : quiz.getFirstWords()) {
			for(Word second : first.getMatches()) {
				statement.setInt(i++, quiz.getId());
				statement.setInt(i++, first.getId());
				statement.setInt(i++, second.getId());
			}
		}
		statement.execute();
	}

	public int addMatch(int list, int word, String match) throws StateException {
		try(Connection conn = dataSource.getConnection()) {
			PreparedStatement statement = conn.prepareStatement("SELECT `type` FROM `words` WHERE `id` = ? AND `list` = ?");
			statement.setInt(1, word);
			statement.setInt(2, list);
			ResultSet result = statement.executeQuery();
			if(result.next()) {
				Word.Type type = Word.Type.values()[result.getInt(1)];
				int matchId = getOrAddWord(conn, list, match, type == Word.Type.FIRST ? Word.Type.SECOND : Word.Type.FIRST);
				statement = conn.prepareStatement("INSERT INTO `matches` (`id`, `first`, `second`) VALUES(?, ?, ?)");
				statement.setInt(1, list);
				statement.setInt(2, type == Word.Type.FIRST ? word : matchId);
				statement.setInt(3, type == Word.Type.SECOND ? word : matchId);
				statement.execute();
				conn.commit();
				return matchId;
			} else {
				throw new NoSuchElementException("Word " + word + " does not exist in " + list);
			}
		} catch(SQLException e) {
			throw new StateException("Failed to add match", e);
		}
	}

	private int getOrAddWord(Connection conn, int list, String word, Word.Type type) throws SQLException {
		PreparedStatement statement = conn.prepareStatement("SELECT `id` FROM `words` WHERE `list` = ? AND `type` = ? AND `word` = ?");
		statement.setInt(1, list);
		statement.setInt(2, type.ordinal());
		statement.setString(3, word);
		ResultSet result = statement.executeQuery();
		if(result.next()) {
			LOG.d("Word already exists");
			return result.getInt(1);
		}
		LOG.d("Adding new word");
		statement = conn.prepareStatement("INSERT INTO `words` (`list`, `type`, `word`) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		statement.setInt(1, list);
		statement.setInt(2, type.ordinal());
		statement.setString(3, word);
		statement.execute();
		result = statement.getGeneratedKeys();
		result.next();
		return result.getInt(1);
	}
}
