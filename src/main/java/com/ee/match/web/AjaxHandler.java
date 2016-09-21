package com.ee.match.web;

import java.util.Objects;

import org.ee.collection.ListMap;
import org.ee.logger.LogManager;
import org.ee.logger.Logger;
import org.ee.web.Status;
import org.ee.web.request.Request;
import org.ee.web.request.Request.Method;
import org.ee.web.request.filter.RequestFilter;
import org.ee.web.response.Response;
import org.ee.web.response.SimpleResponse;

import com.ee.match.MatchContext;
import com.ee.match.exception.NoSuchListException;
import com.ee.match.exception.NoSuchWordException;
import com.ee.match.exception.NotAllowedException;
import com.ee.match.exception.StateException;

public class AjaxHandler implements RequestFilter {
	private static final Logger LOG = LogManager.createLogger();
	private final MatchContext context;

	public AjaxHandler(MatchContext context) {
		this.context = context;
	}

	@Override
	public Response handle(Request request) {
		ListMap<String, String> params = request.getPostParameters();
		String type = params.getFirst("type");
		Response response = new SimpleResponse(Status.NOT_FOUND);
		response.setContentType("application/json");
		if("add-match".equals(type)) {
			try {
				int list = Integer.parseInt(params.getFirst("list"));
				int word = Integer.parseInt(params.getFirst("word"));
				String match = Objects.requireNonNull(params.getFirst("match"));
				String password = params.getFirst("password");
				addMatch(list, word, match, password, response);
			} catch(NumberFormatException | NullPointerException e) {
				response.setStatus(Status.BAD_REQUEST);
				response.setOutput("{\"error\": \"Invalid parameters\"}");
			}
		}
		return response;
	}

	private void addMatch(int list, int word, String match, String password, Response response) {
		try {
			int wordId = context.getState().addMatch(list, word, match, password);
			response.setStatus(Status.CREATED);
			response.setOutput("{\"id\": " + wordId + "}");
		} catch(NoSuchWordException e) {
			response.setStatus(Status.BAD_REQUEST);
			response.setOutput("{\"error\": \"The word you are trying to add a match for does not exist\"}");
		} catch(NoSuchListException e) {
			response.setStatus(Status.BAD_REQUEST);
			response.setOutput("{\"error\": \"This list does not exist\"}");
		} catch(NotAllowedException e) {
			response.setStatus(Status.FORBIDDEN);
			response.setOutput("{\"error\": \"Wrong password\"}");
		} catch(StateException e) {
			LOG.e("Failed to add word", e);
			response.setStatus(Status.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public boolean matches(Request request) {
		return request.getMethod() == Method.POST && "ajax".equals(request.getPath());
	}
}
