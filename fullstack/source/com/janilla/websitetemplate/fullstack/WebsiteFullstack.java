/*
 * MIT License
 *
 * Copyright (c) 2018-2025 Payload CMS, Inc. <info@payloadcms.com>
 * Copyright (c) 2024-2026 Diego Schivo <diego.schivo@janilla.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.janilla.websitetemplate.fullstack;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import javax.net.ssl.SSLContext;

import com.janilla.http.HttpExchange;
import com.janilla.http.HttpHandler;
import com.janilla.http.HttpServer;
import com.janilla.ioc.DiFactory;
import com.janilla.java.Java;
import com.janilla.net.SecureServer;
import com.janilla.websitetemplate.backend.BackendExchange;
import com.janilla.websitetemplate.backend.WebsiteBackend;
import com.janilla.websitetemplate.frontend.WebsiteFrontend;

public class WebsiteFullstack {

//	public static final AtomicReference<WebsiteFullstack> INSTANCE = new AtomicReference<>();

	public static final ScopedValue<WebsiteFullstack> INSTANCE = ScopedValue.newInstance();

	public static void main(String[] args) {
		try {
			WebsiteFullstack a;
			{
				var f = new DiFactory(Java.getPackageClasses(WebsiteFullstack.class.getPackageName()), // INSTANCE::get,
						"fullstack");
				a = f.create(WebsiteFullstack.class,
						Java.hashMap("diFactory", f, "configurationFile",
								args.length > 0 ? Path.of(
										args[0].startsWith("~") ? System.getProperty("user.home") + args[0].substring(1)
												: args[0])
										: null));
			}

			HttpServer s;
			{
				SSLContext c;
				try (var x = SecureServer.class.getResourceAsStream("localhost")) {
					c = Java.sslContext(x, "passphrase".toCharArray());
				}
				var p = Integer.parseInt(a.configuration.getProperty("website-template.fullstack.server.port"));
				s = a.diFactory.create(HttpServer.class,
						Map.of("sslContext", c, "endpoint", new InetSocketAddress(p), "handler", a.handler));
			}
			s.serve();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	protected final WebsiteBackend backend;

	protected final Properties configuration;

	protected final Path configurationFile;

	protected final String configurationKey;

	protected final DiFactory diFactory;

	protected final WebsiteFrontend frontend;

	protected final HttpHandler handler;

	public WebsiteFullstack(DiFactory diFactory, Path configurationFile) {
		this(diFactory, configurationFile, "website-template");
	}

	public WebsiteFullstack(DiFactory diFactory, Path configurationFile, String configurationKey) {
		this.diFactory = diFactory;
		this.configurationFile = configurationFile;
		this.configurationKey = configurationKey;
//		if (!INSTANCE.compareAndSet(null, this))
//			throw new IllegalStateException();
		diFactory.context(this);
		configuration = diFactory.create(Properties.class, Collections.singletonMap("file", configurationFile));

		var cf = Optional.ofNullable(configurationFile).orElseGet(() -> {
			try {
				return Path.of(getClass().getResource("configuration.properties").toURI());
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		});
		backend = ScopedValue.where(INSTANCE, this).call(() -> {
			var f = new DiFactory(backendTypes(), // WebsiteBackend.INSTANCE::get,
					"backend");
			return f.create(WebsiteBackend.class,
					Java.hashMap("diFactory", f, "configurationFile", cf, "configurationKey", configurationKey));
		});
		frontend = ScopedValue.where(INSTANCE, this).call(() -> {
			var f = new DiFactory(frontendTypes(), // WebsiteFrontend.INSTANCE::get,
					"frontend");
			return f.create(WebsiteFrontend.class,
					Java.hashMap("diFactory", f, "configurationFile", cf, "configurationKey", configurationKey));
		});

		handler = this::handle;
	}

	public WebsiteBackend backend() {
		return backend;
	}

	public Properties configuration() {
		return configuration;
	}

	public String configurationKey() {
		return configurationKey;
	}

	public DiFactory diFactory() {
		return diFactory;
	}

	public WebsiteFrontend frontend() {
		return frontend;
	}

	public HttpHandler handler() {
		return handler;
	}

	protected List<Class<?>> backendTypes() {
		return Stream
				.of("com.janilla.web", WebsiteBackend.class.getPackageName(), WebsiteFullstack.class.getPackageName())
				.flatMap(x -> Java.getPackageClasses(x).stream()).toList();
	}

	protected List<Class<?>> frontendTypes() {
		return Stream
				.of("com.janilla.web", WebsiteFrontend.class.getPackageName(), WebsiteFullstack.class.getPackageName())
				.flatMap(x -> Java.getPackageClasses(x).stream()).toList();
	}

	protected boolean handle(HttpExchange exchange) {
		var h = exchange instanceof BackendExchange ? backend.handler() : frontend.handler();
		return h.handle(exchange);
	}
}
