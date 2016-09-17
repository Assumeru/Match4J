package com.ee.match.web.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ee.collection.ListMap;
import org.ee.collection.MapBuilder;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.Status;
import org.ee.web.request.Request;
import org.ee.web.request.Request.Method;
import org.ee.web.response.Response;

import com.ee.match.MatchContext;
import com.ee.match.quiz.Quiz;
import com.ee.match.quiz.Word;
import com.ee.match.quiz.Word.Type;
import com.ee.match.web.template.Template;
import com.ee.match.web.template.Variable;

public class CurrentPage extends WebPage {
	private static final Logger LOG = LogManager.createLogger();

	public CurrentPage(MatchContext context) {
		super(context, "test", Status.OK, "Test", "current");
	}

	@Override
	protected void setVariables(Template template, Request request, Response response) {
		List<String> js = template.getVariable(Variable.JAVASCRIPT);
		js.add("classes.js");
		if(request.getMethod() == Method.POST) {
			try {
				startNew(template, request, js);
			} catch(NumberFormatException e) {
				LOG.d(e);
			}
		}
		js.add("test.js");
	}

	private void startNew(Template template, Request request, List<String> js) {
		ListMap<String, String> params = request.getPostParameters();
		int id = Integer.parseInt(params.getFirst("id"));
		Quiz quiz = context.getState().getList(id);
		if(quiz != null) {
			Word.Type direction = Word.Type.FIRST;
			if("second".equals(params.getFirst("direction"))) {
				direction = Word.Type.SECOND;
			}
			boolean twice = "on".equals(params.getFirst("double"));
			boolean repeat = "on".equals(params.getFirst("repeat"));
			boolean caseSensitive = "on".equals(params.getFirst("casesensitive"));
			template.getVariable(Variable.JAVASCRIPT_SETTINGS).put("current", toMap(quiz, direction, twice, repeat, caseSensitive));
			js.add("start.js");
		}
	}

	private Map<?, ?> toMap(Quiz list, Type direction, boolean twice, boolean repeat, boolean caseSensitive) {
		return new MapBuilder<>()
				.put("title", list.getTitle())
				.put("id", list.getId())
				.put("repeat", repeat)
				.put("double", twice)
				.put("caseSensitive", caseSensitive)
				.put("words", getWords(list, direction))
				.build();
	}

	private List<Map<?, ?>> getWords(Quiz list, Type direction) {
		List<Word> listWords = direction == Word.Type.FIRST ? list.getFirstWords() : list.getSecondWords();
		List<Map<?, ?>> words = new ArrayList<>(listWords.size());
		for(Word word : listWords) {
			words.add(new MapBuilder<>()
					.put("id", word.getId())
					.put("word", word.getWord())
					.put("matches", getMatches(word.getMatches()))
					.build());
		}
		return words;
	}

	private List<String> getMatches(Collection<Word> matches) {
		List<String> words = new ArrayList<>(matches.size());
		for(Word word : matches) {
			words.add(word.getWord());
		}
		return words;
	}
}
