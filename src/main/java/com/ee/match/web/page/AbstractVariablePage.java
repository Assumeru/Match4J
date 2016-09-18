package com.ee.match.web.page;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.reflection.ReflectionUtils;
import org.ee.text.PrimitiveUtils;
import org.ee.text.UriTemplate;
import org.ee.web.Status;
import org.ee.web.request.Request;
import org.ee.web.response.Response;

import com.ee.match.MatchContext;
import com.ee.match.exception.ParamPageException;
import com.ee.match.web.PathParam;
import com.ee.match.web.template.Template;

public abstract class AbstractVariablePage extends WebPage {
	private static final Logger LOG = LogManager.createLogger();
	private UriTemplate template;
	private Method method;
	private String[] parameterOrder;

	protected AbstractVariablePage(MatchContext context, String template, Status status, String title, String pattern) {
		super(context, template, status, title, null);
		setTemplate(pattern);
	}

	private void setTemplate(String pattern) {
		template = new UriTemplate(pattern + "{___slash : [/]*}");
		List<Method> methods = ReflectionUtils.getMethodsUntil(getClass(), AbstractVariablePage.class);
		for(Method method : methods) {
			Class<?>[] paramTypes = method.getParameterTypes();
			if("setVariables".equals(method.getName()) && method.getParameterCount() > 2
					&& paramTypes[0] == Template.class
					&& paramTypes[1] == Request.class
					&& paramTypes[2] == Response.class) {
				setMethod(method);
				break;
			}
		}
	}

	private void setMethod(Method method) {
		List<String> parameterOrder = new ArrayList<>();
		Parameter[] params = method.getParameters();
		for(int i = 3; i < params.length; i++) {
			Parameter param = params[i];
			PathParam annotation = param.getAnnotation(PathParam.class);
			if(annotation == null) {
				LOG.w("Parameter " + i + " in setVariables in " + getClass() + " is not annotated " + PathParam.class);
				return;
			} else if(!template.getParams().contains(annotation.value())) {
				LOG.w("UriTemplate for " + getClass() + " does not contain a parameter " + annotation.value());
				return;
			} else if(!PrimitiveUtils.isPrimitive(param.getType()) && !String.class.isAssignableFrom(param.getType())) {
				LOG.w(param.getType() + " in " + getClass() + " is not a valid argument type for setVariables");
				return;
			}
			parameterOrder.add(annotation.value());
		}
		this.method = method;
		this.parameterOrder = parameterOrder.toArray(new String[parameterOrder.size()]);
	}

	@Override
	protected final void setVariables(Template t, Request r1, Response r2) {
		if(method != null) {
			Object[] args = new Object[method.getParameterCount()];
			args[0] = t;
			args[1] = r1;
			args[2] = r2;
			Map<String, String> parameters = template.match(r1.getPath());
			for(int i = 3; i < args.length; i++) {
				String param = parameters.get(parameterOrder[i - 3]);
				Class<?> type = method.getParameters()[i].getType();
				if(PrimitiveUtils.isPrimitive(type)) {
					args[i] = PrimitiveUtils.parse(type, param);
				} else {
					args[i] = param;
				}
			}
			try {
				method.invoke(this, args);
			} catch (InvocationTargetException e) {
				if(e.getCause() instanceof RuntimeException) {
					throw (RuntimeException) e.getCause();
				}
				throw new ParamPageException("Failed to call " + method, e);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new ParamPageException("Failed to call " + method, e);
			}
		}
	}

	@Override
	public boolean matches(Request request) {
		return template.matches(request.getPath());
	}
}
