package com.ee.match.web.page;

import org.ee.web.Status;
import org.ee.web.request.Request;
import org.ee.web.response.Response;

import com.ee.match.MatchContext;
import com.ee.match.web.template.Template;

public class Http404 extends WebPage {
	public Http404(MatchContext context) {
		super(context, "error", Status.NOT_FOUND, "404 Not found", null);
	}

	@Override
	protected void setVariables(Template template, Request request, Response response) {
		template.setVariable("name", "404 Not found");
		template.setVariable("description", "The requested page could not be found.");
	}
}
