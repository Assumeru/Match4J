package com.ee.match.web.page;

import org.ee.web.Status;
import org.ee.web.request.Request;
import org.ee.web.response.Response;

import com.ee.match.MatchContext;
import com.ee.match.web.template.Template;

public class Http500 extends WebPage {
	public Http500(MatchContext context) {
		super(context, "error", Status.INTERNAL_SERVER_ERROR, "500 Internal server error", null);
	}

	@Override
	protected void setVariables(Template template, Request request, Response response) {
		template.setVariable("name", "500 Internal server error");
		template.setVariable("description", "An error occurred while loading the page.");
	}
}
