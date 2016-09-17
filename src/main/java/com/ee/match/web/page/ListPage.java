package com.ee.match.web.page;

import java.util.HashSet;
import java.util.Set;

import org.ee.web.Status;
import org.ee.web.exception.NotFoundException;
import org.ee.web.request.Request;
import org.ee.web.response.Response;

import com.ee.match.MatchContext;
import com.ee.match.quiz.Quiz;
import com.ee.match.quiz.Word;
import com.ee.match.web.PathParam;
import com.ee.match.web.template.Template;
import com.ee.match.web.template.Variable;

public class ListPage extends AbstractVariablePage {
	public ListPage(MatchContext context) {
		super(context, "list", Status.OK, null, "list/{id : [-]{0,1}[0-9]+}");
	}

	public void setVariables(Template template, Request request, Response response, @PathParam("id") int id) {
		Quiz quiz = context.getState().getList(id);
		if(quiz == null) {
			throw new NotFoundException();
		}
		Set<Word> second = new HashSet<>(quiz.getSecondWords());
		for(Word first : quiz.getFirstWords()) {
			second.removeAll(first.getMatches());
		}
		template.setVariable(Variable.TITLE, quiz.getTitle());
		template.setVariable("list", quiz);
		template.setVariable("second", second);
	}
}
