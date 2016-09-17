package com.ee.match;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.ee.config.ConfigurationException;
import org.ee.web.WebApplication;
import org.ee.web.request.RequestHandler;
import org.ee.web.response.DefaultResponseWriter;
import org.ee.web.response.ResponseWriter;

import com.ee.match.config.MatchConfig;
import com.ee.match.state.State;
import com.ee.match.web.MatchRequestHandler;
import com.ee.match.web.template.TemplateProvider;

@WebServlet(urlPatterns = "/*")
public class Match extends WebApplication {
	private static final long serialVersionUID = -3257197669315982969L;
	private RequestHandler requestHandler;
	private ResponseWriter responseWriter;
	private MatchContext context;
	private MatchConfig config;
	private TemplateProvider templateProvider;
	private State state;

	@Override
	public void init() throws ServletException {
		context = new MatchContext(this);
		try {
			config = new MatchConfig(context);
		} catch (IOException e) {
			throw new ConfigurationException("Failed to init config", e);
		}
		responseWriter = new DefaultResponseWriter();
		requestHandler = new MatchRequestHandler(context);
		templateProvider = new TemplateProvider(context);
		state = new State(context);
	}

	MatchConfig getConfig() {
		return config;
	}

	State getState() {
		return state;
	}

	@Override
	protected RequestHandler getRequestHandler() {
		return requestHandler;
	}

	@Override
	protected ResponseWriter getResponseWriter() {
		return responseWriter;
	}

	TemplateProvider getTemplateProvider() {
		return templateProvider;
	}
}
