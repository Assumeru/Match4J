package com.ee.match.web.page;

import org.ee.web.Status;
import org.ee.web.request.Request;
import org.ee.web.response.Response;

import com.ee.match.MatchContext;
import com.ee.match.web.template.Template;

public class IndexPage extends WebPage {
	public IndexPage(MatchContext context) {
		super(context, "index", Status.OK, "Lists", "");
	}

	@Override
	protected void setVariables(Template template, Request request, Response response) {
		template.setVariable("lists", context.getState().getLists());
	}
}
