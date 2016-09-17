package com.ee.match.web;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ee.collection.MapBuilder;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.Status;
import org.ee.web.request.RequestHandler;
import org.ee.web.request.filter.RequestFilter;
import org.ee.web.request.filter.RequestFilterHandler;
import org.ee.web.request.resource.ResourceHandler;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import com.ee.match.MatchContext;
import com.ee.match.web.page.Http404;
import com.ee.match.web.page.Http500;

public class MatchRequestHandler extends RequestFilterHandler {
	private static final Logger LOG = LogManager.createLogger();
	private final Map<Status, RequestHandler> statusPages;

	public MatchRequestHandler(MatchContext context) {
		super(initFilters(context));
		statusPages = new MapBuilder<Status, RequestHandler>()
				.put(Status.NOT_FOUND, new Http404(context))
				.put(Status.INTERNAL_SERVER_ERROR, new Http500(context))
				.build();
	}

	private static Set<RequestFilter> initFilters(MatchContext context) {
		Set<RequestFilter> requestHandlers = new HashSet<>();
		for(Class<? extends RequestFilter> handler : getHandlers()) {
			if(!Modifier.isAbstract(handler.getModifiers())) {
				try {
					RequestFilter instance = getInstance(handler, context);
					requestHandlers.add(instance);
					LOG.d("Loaded handler " + instance.getClass());
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					LOG.e("Implementations of RequestHandler should provide a default constructor", e);
				}
			}
		}
		LOG.d("Loaded " + requestHandlers.size() + " handlers");
		return requestHandlers;
	}

	private static Set<Class<? extends RequestFilter>> getHandlers() {
		return new Reflections(new ConfigurationBuilder()
				.forPackages(MatchRequestHandler.class.getPackage().getName(), ResourceHandler.class.getPackage().getName()))
				.getSubTypesOf(RequestFilter.class);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getInstance(Class<T> type, MatchContext context) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		for(Constructor<?> constructor : type.getConstructors()) {
			if(constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].isAssignableFrom(MatchContext.class)) {
				return (T) constructor.newInstance(context);
			}
		}
		return type.newInstance();
	}

	@Override
	protected RequestHandler getStatusPage(Status status) {
		return statusPages.get(status);
	}
}
