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
package com.janilla.websitetemplate.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import com.janilla.backend.cms.AbstractCollectionApi;
import com.janilla.backend.persistence.Persistence;
import com.janilla.http.HttpExchange;
import com.janilla.web.Bind;
import com.janilla.web.Handle;

public abstract class PostApi0<P extends Post> extends AbstractCollectionApi<Long, P> {

	public PostApi0(Class<P> type, Predicate<HttpExchange> drafts, Persistence persistence) {
		super(type, drafts, persistence);
	}

	@Override
	public List<P> read(Long skip, Long limit) {
		throw new UnsupportedOperationException();
	}

	@Handle(method = "GET")
	public List<P> read(@Bind("slug") String slug, HttpExchange exchange) {
//		IO.println("PostApi.read, slug=" + slug);
		var d = drafts.test(exchange);
		var ll = new ArrayList<>(
				slug != null && !slug.isBlank() ? crud().filter(d ? "slugDraft" : "slug", new Object[] { slug })
						: crud().list());
		Collections.reverse(ll);
		return crud().read(ll, d);
	}
}
