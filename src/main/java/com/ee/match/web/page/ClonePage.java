package com.ee.match.web.page;

import org.ee.web.Status;
import org.ee.web.exception.NotFoundException;
import org.ee.web.request.Request;
import org.ee.web.response.Response;

import com.ee.match.MatchContext;
import com.ee.match.quiz.Quiz;
import com.ee.match.web.PathParam;
import com.ee.match.web.template.Template;
import com.ee.match.web.template.Variable;

public class ClonePage extends AbstractVariablePage {
	public ClonePage(MatchContext context) {
		super(context, "edit", Status.OK, "New list", "clone/{id : [-]{0,1}[0-9]+}");
	}

	public void setVariables(Template template, Request request, Response response, @PathParam("id") int id) {
		Quiz quiz = context.getState().getList(id);
		if(quiz == null) {
			throw new NotFoundException();
		}
		template.getVariable(Variable.JAVASCRIPT).add("edit.js");
		template.setVariable("list", quiz);
		template.setVariable("clone", true);
	}
}
