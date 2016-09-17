package com.ee.match.web.template;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.AbstractContext;

public class Template {
	private final String template;
	private final TemplateEngine engine;
	private final AbstractContext context;

	public Template(String template, TemplateEngine engine, AbstractContext context) {
		this.template = template;
		this.engine = engine;
		this.context = context;
	}

	public Template setVariable(String key, Object value) {
		context.setVariable(key, value);
		return this;
	}

	public <T> Template setVariable(Variable<T> key, T value) {
		context.setVariable(key.getName(), value);
		return this;
	}

	public <T> T getVariable(Variable<T> variable) {
		Object current = context.getVariable(variable.getName());
		T value = variable.get(current);
		if(current != value) {
			setVariable(variable, value);
		}
		return value;
	}

	public void process(OutputStream output) throws IOException {
		final Writer writer = new OutputStreamWriter(output);
		engine.process(template, context, writer);
		writer.flush();
	}
}
