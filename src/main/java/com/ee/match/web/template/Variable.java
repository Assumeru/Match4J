package com.ee.match.web.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Variable<T> {
	public static final Variable<String> TITLE = new Variable<>("title", null);
	public static final Variable<String> BODY = new Variable<>("body", null);
	public static final Variable<List<String>> JAVASCRIPT = new Variable<>("javascript", () -> new ArrayList<>(1));
	public static final Variable<Map<String, Object>> JAVASCRIPT_SETTINGS = new Variable<>("javascriptSettings", () -> new HashMap<>(1));
	private final String name;
	private final Supplier<T> supplier;

	private Variable(String name, Supplier<T> supplier) {
		this.name = name;
		this.supplier = supplier;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public T get(Object value) {
		if(value == null && supplier != null) {
			return supplier.get();
		}
		return (T) value;
	}
}
