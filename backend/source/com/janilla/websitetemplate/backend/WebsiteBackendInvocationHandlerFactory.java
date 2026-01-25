package com.janilla.websitetemplate.backend;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import com.janilla.blanktemplate.backend.BlankBackendInvocationHandlerFactory;
import com.janilla.http.HttpHandlerFactory;
import com.janilla.ioc.DiFactory;
import com.janilla.web.Invocable;
import com.janilla.web.Invocation;
import com.janilla.web.RenderableFactory;

public class WebsiteBackendInvocationHandlerFactory extends BlankBackendInvocationHandlerFactory {

	public WebsiteBackendInvocationHandlerFactory(List<Invocable> invocables,
			Function<Class<?>, Object> instanceResolver, Comparator<Invocation> invocationComparator,
			RenderableFactory renderableFactory, HttpHandlerFactory rootFactory, Properties configuration,
			String configurationKey, DiFactory diFactory) {
		super(invocables, instanceResolver, invocationComparator, renderableFactory, rootFactory, configuration,
				configurationKey, diFactory);
		guestPost.add("/api/form-submissions");
	}
}
