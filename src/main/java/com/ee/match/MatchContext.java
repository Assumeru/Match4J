package com.ee.match;

import org.ee.config.Config;
import org.ee.config.ConfigurationException;
import org.ee.web.AbstractApplicationContext;

import com.ee.match.state.State;
import com.ee.match.web.template.TemplateProvider;

public class MatchContext extends AbstractApplicationContext {
	private final Match context;

	public MatchContext(Match context) {
		super(context.getServletContext());
		this.context = context;
	}

	public Config getConfig() {
		return context.getConfig();
	}

	public TemplateProvider getTemplateProvider() {
		return context.getTemplateProvider();
	}

	public String getStringSetting(Class<?> type, String key) {
		String value = getConfig().getString(type, key);
		if(value == null || value.isEmpty()) {
			throw new ConfigurationException("Missing config value for " + Config.getKey(type, key));
		}
		return value;
	}

	public State getState() {
		return context.getState();
	}
}
