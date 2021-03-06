package com.ee.match.web.page;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.ee.web.Status;
import org.ee.web.exception.WebException;
import org.ee.web.request.Request;
import org.ee.web.request.filter.RequestFilter;
import org.ee.web.response.Response;
import org.ee.web.response.SimpleResponse;
import org.mindrot.jbcrypt.BCrypt;

import com.ee.match.MatchContext;
import com.ee.match.web.template.Template;
import com.ee.match.web.template.Variable;

public abstract class WebPage implements RequestFilter {
	protected final MatchContext context;
	private final String body;
	private final Status status;
	private final String title;
	private final String path;

	protected WebPage(MatchContext context, String template, Status status, String title, String path) {
		this.context = context;
		this.body = template + "::fragment";
		this.status = status;
		this.title = title;
		this.path = path;
	}

	@Override
	public Response handle(Request request) {
		Response response = new SimpleResponse(status);
		response.setOutput(getResponseOutput(request, response));
		response.setContentType("text/html");
		return response;
	}

	private ByteArrayOutputStream getResponseOutput(Request request, Response response) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Template template = context.getTemplateProvider().createTemplate("page", request);
		template.setVariable(Variable.TITLE, title);
		template.setVariable(Variable.BODY, body);
		template.getVariable(Variable.JAVASCRIPT_SETTINGS).put("ajaxurl", context.getContextPath() + "/ajax");
		setVariables(template, request, response);
		try {
			template.process(output);
		} catch (IOException e) {
			throw new WebException(e);
		}
		return output;
	}

	protected abstract void setVariables(Template template, Request request, Response response);

	@Override
	public boolean matches(Request request) {
		return request.getPath().equals(this.path) || request.getPath().equals(this.path + "/");
	}

	protected final void redirect(String path) {
		Response response = new SimpleResponse(Status.SEE_OTHER);
		response.setHeader("Location", context.getContext().getContextPath() + path);
		throw new WebException(response);
	}

	protected String hash(String password) {
		if(password != null && !password.isEmpty()) {
			return BCrypt.hashpw(password, BCrypt.gensalt(10, context.getSecureRandom()));
		}
		return null;
	}
}
