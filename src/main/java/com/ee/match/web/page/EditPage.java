package com.ee.match.web.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ee.collection.ListMap;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.Status;
import org.ee.web.exception.NotFoundException;
import org.ee.web.exception.WebException;
import org.ee.web.request.Request;
import org.ee.web.request.Request.Method;
import org.ee.web.response.Response;

import com.ee.match.MatchContext;
import com.ee.match.exception.StateException;
import com.ee.match.quiz.Quiz;
import com.ee.match.quiz.Word;
import com.ee.match.quiz.Word.Type;
import com.ee.match.web.PathParam;
import com.ee.match.web.template.Template;
import com.ee.match.web.template.Variable;

public class EditPage extends AbstractVariablePage {
	private static final Logger LOG = LogManager.createLogger();

	public EditPage(MatchContext context) {
		super(context, "edit", Status.OK, "Edit", "edit/{id : [-]{0,1}[0-9]+}");
	}

	public void setVariables(Template template, Request request, Response response, @PathParam("id") int id) {
		Quiz quiz = context.getState().getList(id);
		if(quiz == null) {
			throw new NotFoundException();
		}
		template.getVariable(Variable.JAVASCRIPT).add("edit.js");
		template.setVariable("list", quiz);
		if(request.getMethod() == Method.POST) {
			ListMap<String, String> params = request.getPostParameters();
			String title = params.getFirst("title");
			String first = params.getFirst("first");
			String second = params.getFirst("second");
			String password = params.getFirst("password");
			boolean removePassword = "on".equals(params.getFirst("removepassword"));
			String newPassword = params.getFirst("newpassword");
			List<String> firstWords = getWords(params.get("first_word[]"));
			List<String> secondWords = getWords(params.get("second_word[]"));
			if(title != null && first != null && second != null) {
				Quiz edited = buildQuiz(firstWords, secondWords, title, first, second, quiz.getPassword());
				edited.setId(quiz.getId());
				if(!quiz.hasPassword() || (password != null && !password.isEmpty() && quiz.passwordMatches(password))) {
					if(removePassword) {
						edited.setPassword(null);
					} else if(newPassword != null && !newPassword.isEmpty()) {
						edited.setPassword(hash(newPassword));
					}
					saveChanges(quiz, edited);
				}
				template.setVariable("saveerror", true);
				template.setVariable("list", edited);
			}
		}
	}

	private List<String> getWords(List<String> list) {
		return list == null ? Collections.emptyList() : list;
	}

	private void saveChanges(Quiz quiz, Quiz edited) {
		if(!edited.getTitle().isEmpty() && !edited.getFirst().isEmpty() && !edited.getSecond().isEmpty()) {
			if(edited.getFirstWords().isEmpty()) {
				LOG.d("deleting " + quiz.getId());
				try {
					context.getState().removeList(quiz);
					redirect("/");
				} catch(StateException e) {
					throw new WebException(e);
				}
			} else {
				try {
					context.getState().modifyList(quiz, edited);
					redirect("/list/" + quiz.getId());
				} catch(StateException e) {
					LOG.e("Failed to save changes to " + quiz.getId(), e);
				}
			}
		}
	}

	static Quiz buildQuiz(List<String> firstWords, List<String> secondWords, String title, String firstName, String secondName, String password) {
		Map<String, Word> firstCache = new LinkedHashMap<>();
		Map<String, Word> secondCache = new LinkedHashMap<>();
		for(int i = 0; i < firstWords.size() && i < secondWords.size(); i++) {
			Word first = getOrCreateWord(firstWords.get(i), Word.Type.FIRST, firstCache);
			Word second = getOrCreateWord(secondWords.get(i), Word.Type.SECOND, secondCache);
			if(first != null && second != null) {
				first.getMatches().add(second);
				second.getMatches().add(first);
			}
		}
		return new Quiz(0, title.trim(), firstName.trim(), secondName.trim(), password, new ArrayList<>(firstCache.values()), new ArrayList<>(secondCache.values()));
	}

	static Word getOrCreateWord(String word, Type type, Map<String, Word> cache) {
		word = word == null ? "" : word.trim();
		if(word.isEmpty()) {
			return null;
		}
		Word out = cache.get(word);
		if(out == null) {
			out = new Word(0, type, word, new ArrayList<>(1));
			cache.put(word, out);
		}
		return out;
	}
}
