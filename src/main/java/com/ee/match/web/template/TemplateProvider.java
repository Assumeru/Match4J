package com.ee.match.web.template;

import org.ee.web.request.Request;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.ee.match.MatchContext;

public class TemplateProvider {
	private final TemplateEngine engine;

	public TemplateProvider(MatchContext context) {
		engine = new TemplateEngine();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context.getContext());
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setCacheTTLMs(3600000L);
		engine.setTemplateResolver(templateResolver);
	}

	public Template createTemplate(String template, Request request) {
		return new Template(template, engine,
				new WebContext(request.getContext().getServletRequest(), request.getContext().getServletResponse(), request.getContext().getContext()));
	}
}
