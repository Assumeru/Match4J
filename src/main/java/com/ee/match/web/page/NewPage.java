package com.ee.match.web.page;

import java.util.Collections;
import java.util.List;

import org.ee.collection.ListMap;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.Status;
import org.ee.web.request.Request;
import org.ee.web.request.Request.Method;
import org.ee.web.response.Response;

import com.ee.match.MatchContext;
import com.ee.match.exception.StateException;
import com.ee.match.quiz.Quiz;
import com.ee.match.web.template.Template;
import com.ee.match.web.template.Variable;

public class NewPage extends WebPage {
	private static final Logger LOG = LogManager.createLogger();

	public NewPage(MatchContext context) {
		super(context, "new", Status.OK, "New list", "new");
	}

	@Override
	protected void setVariables(Template template, Request request, Response response) {
		template.getVariable(Variable.JAVASCRIPT).add("edit.js");
		if(request.getMethod() == Method.POST) {
			ListMap<String, String> params = request.getPostParameters();
			String title = params.getFirst("title");
			String first = params.getFirst("first");
			String second = params.getFirst("second");
			List<String> firstWords = getWords(params.get("first_word[]"));
			List<String> secondWords = getWords(params.get("second_word[]"));
			if(title != null && first != null && second != null) {
				Quiz quiz = EditPage.buildQuiz(firstWords, secondWords, title, first, second);
				save(quiz);
				template.setVariable("list", quiz);
				template.setVariable(Variable.BODY, "edit");
			}
		}
	}

	private List<String> getWords(List<String> list) {
		return list == null ? Collections.emptyList() : list;
	}

	private void save(Quiz quiz) {
		if(!quiz.getTitle().isEmpty() && !quiz.getFirst().isEmpty() && !quiz.getSecond().isEmpty() && !quiz.getFirstWords().isEmpty()) {
			try {
				context.getState().saveList(quiz);
				redirect("/list/" + quiz.getId());
			} catch (StateException e) {
				LOG.w(e);
			}
		}
	}
}
