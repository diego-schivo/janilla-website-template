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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.janilla.blanktemplate.frontend.BlankDataFetching;
import com.janilla.blanktemplate.frontend.BlankFrontendHttpExchange;
import com.janilla.blanktemplate.frontend.BlankIndexFactory;
import com.janilla.blanktemplate.frontend.BlankWebHandling;
import com.janilla.http.HttpExchange;
import com.janilla.web.Bind;
import com.janilla.web.Handle;
import com.janilla.web.NotFoundException;

public class WebsiteWebHandling extends BlankWebHandling {

	public WebsiteWebHandling(BlankDataFetching dataFetching, BlankIndexFactory indexFactory) {
		super(dataFetching, indexFactory);
	}

//	@Handle(method = "GET", path = "/admin(/[\\w\\d/-]*)?")
//	public Object admin(String path, HttpExchange exchange) {
	//// IO.println("WebHandling.admin, path=" + path);
//		if (path == null || path.isEmpty())
//			path = "/";
//		switch (path) {
//		case "/":
//			if (((BlankFrontendHttpExchange) exchange).sessionUser() == null)
//				return URI.create("/admin/login");
//			break;
//		case "/login":
//			if (((List<?>) dataFetching.users(0l, 1l)).isEmpty())
//				return URI.create("/admin/create-first-user");
//			break;
//		}
//		return indexFactory.index(exchange);
//	}

	@Override
	public Object page(HttpExchange exchange) {
		return page("home", exchange);
	}

	@Handle(method = "GET", path = "/([\\w\\d-]+)")
	public Object page(String slug, HttpExchange exchange) {
//		IO.println("WebHandling.page, slug=" + slug);
		var pp = ((WebsiteDataFetching) dataFetching).pages(slug, ((BlankFrontendHttpExchange) exchange).tokenCookie());
		if (pp.isEmpty()) {
			if (slug.equals("home"))
				pp = List.of(Map.of("slug", "home"));
			else
				throw new NotFoundException("slug=" + slug);
		}
		var i = indexFactory.index(exchange);
		Object p = pp.getFirst();
		i.state().put("page", p);

		class A {
			@SuppressWarnings("unchecked")
			private static boolean pageLayoutContains(Object page, String component) {
				var l = (List<Object>) ((Map<String, Object>) page).get("layout");
				return l != null && l.stream().anyMatch(x -> ((Map<String, Object>) x).get("$type").equals(component));
			}
		}
		if (A.pageLayoutContains(p, "Archive"))
			i.state().put("posts", ((WebsiteDataFetching) dataFetching).posts(null,
					((BlankFrontendHttpExchange) exchange).tokenCookie()));

		Stream.of("archive", "call-to-action", "content", "form-block", "hero", "media-block", "page")
				.map(((WebsiteIndexFactory) indexFactory)::websiteTemplate).forEach(i.templates()::add);
		return i;
	}

	@Handle(method = "GET", path = "/posts/([\\w\\d-]+)")
	public Object post(String slug, HttpExchange exchange) {
//		IO.println("WebHandling.post, slug=" + slug);
		var pp = ((WebsiteDataFetching) dataFetching).posts(slug, ((BlankFrontendHttpExchange) exchange).tokenCookie());
		if (pp.isEmpty())
			throw new NotFoundException("slug=" + slug);
		var i = indexFactory.index(exchange);
		i.state().put("post", pp.getFirst());
		Stream.of("banner", "card", "media-block", "post", "rich-text")
				.map(((WebsiteIndexFactory) indexFactory)::websiteTemplate).forEach(i.templates()::add);
		return i;
	}

	@Handle(method = "GET", path = "/posts")
	public Object posts(HttpExchange exchange) {
//		IO.println("WebHandling.posts");
		var i = indexFactory.index(exchange);
		i.state().put("posts",
				((WebsiteDataFetching) dataFetching).posts(null, ((BlankFrontendHttpExchange) exchange).tokenCookie()));
		Stream.of("card", "posts").map(((WebsiteIndexFactory) indexFactory)::websiteTemplate)
				.forEach(i.templates()::add);
		return i;
	}

	@Handle(method = "GET", path = "/search")
	public Object search(@Bind("q") String query, HttpExchange exchange) {
//		IO.println("WebHandling.search, query=" + query);
		var i = indexFactory.index(exchange);
		i.state().put("results", ((WebsiteDataFetching) dataFetching).searchResults(query));
		Stream.of("card", "search").map(((WebsiteIndexFactory) indexFactory)::websiteTemplate)
				.forEach(i.templates()::add);
		return i;
	}
}
