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
package com.janilla.websitetemplate.frontend;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.net.ssl.SSLContext;

import com.janilla.blanktemplate.frontend.BlankFrontend;
import com.janilla.http.HttpServer;
import com.janilla.ioc.DiFactory;
import com.janilla.java.Java;
import com.janilla.net.SecureServer;

public class WebsiteFrontend extends BlankFrontend {

	public static void main(String[] args) {
		try {
			WebsiteFrontend a;
			{
				var f = new DiFactory(Stream.of("com.janilla.web", WebsiteFrontend.class.getPackageName())
						.flatMap(x -> Java.getPackageClasses(x).stream()).toList());
				a = f.create(WebsiteFrontend.class,
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
				var p = Integer.parseInt(a.configuration.getProperty(a.configurationKey() + ".frontend.server.port"));
				s = a.diFactory.create(HttpServer.class,
						Map.of("sslContext", c, "endpoint", new InetSocketAddress(p), "handler", a.handler));
			}
			s.serve();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public WebsiteFrontend(DiFactory diFactory, Path configurationFile) {
		this(diFactory, configurationFile, "website-template");
	}

	public WebsiteFrontend(DiFactory diFactory, Path configurationFile, String configurationKey) {
		super(diFactory, configurationFile, configurationKey);
	}

	@Override
	protected List<Path> resourcePaths() {
		return Stream.concat(super.resourcePaths().stream(),
				Java.getPackagePaths(WebsiteFrontend.class.getPackageName()).stream().filter(Files::isRegularFile))
				.toList();
	}
}
