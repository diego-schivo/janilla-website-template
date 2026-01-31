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
import java.util.Properties;
import java.util.stream.Stream;

import com.janilla.blanktemplate.frontend.BlankDataFetching;
import com.janilla.blanktemplate.frontend.BlankIndexFactory;
import com.janilla.blanktemplate.frontend.Index.Template;
import com.janilla.http.HttpExchange;
import com.janilla.web.ResourceMap;

public class WebsiteIndexFactory extends BlankIndexFactory {

	public WebsiteIndexFactory(Properties configuration, String configurationKey, BlankDataFetching dataFetching,
			ResourceMap resourceMap) {
		super(configuration, configurationKey, dataFetching, resourceMap);
	}

	@Override
	public Template blankTemplate(String name) {
		return template("blank/" + name);
	}

	public Template frontendTemplate(String name) {
		return template(name);
	}

	public Template websiteTemplate(String name) {
		return template(name);
	}

	@Override
	protected Map<String, Object> state(HttpExchange exchange) {
		var x = super.state(exchange);
		x.put("header", ((WebsiteDataFetching) dataFetching).header());
		x.put("footer", ((WebsiteDataFetching) dataFetching).footer());
		return x;
	}

	@Override
	protected void putImports(Map<String, String> map) {
		super.putImports(map);
		Stream.of("admin", "admin-bar", "admin-create-first-user", "admin-dashboard", "app", "archive", "banner",
				"call-to-action", "card", "content", "footer", "form-block", "header", "hero", "link", "media-block",
				"not-found", "page", "post", "posts", "rich-text", "search", "theme-selector")
				.map(this::websiteImportKey).forEach(x -> map.put(x, "/" + x + ".js"));
	}

	@Override
	protected String cmsImportKey(String name) {
		return "cms/" + name;
	}

	@Override
	protected String blankImportKey(String name) {
		return "blank/" + name;
	}

	protected String websiteImportKey(String name) {
		return name;
	}

	@Override
	protected void addTemplates(List<Template> list) {
		super.addTemplates(list);
		Stream.of("janilla-logo").map(this::frontendTemplate).forEach(list::add);
		Stream.of("app", "footer", "header", "link", "not-found", "page", "theme-selector").map(this::websiteTemplate)
				.forEach(list::add);
	}
}
