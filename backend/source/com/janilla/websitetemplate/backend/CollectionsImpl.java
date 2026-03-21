package com.janilla.websitetemplate.backend;

import java.util.List;

import com.janilla.blanktemplate.Media;
import com.janilla.cms.User;
import com.janilla.websitetemplate.Category;
import com.janilla.websitetemplate.Form;
import com.janilla.websitetemplate.FormSubmission;
import com.janilla.websitetemplate.Page;
import com.janilla.websitetemplate.Post;
import com.janilla.websitetemplate.SearchResult;

public record CollectionsImpl(List<Page> pages, List<Post> posts, List<Media> media, List<Category> categories,
		List<User<Long>> users, List<Redirect> redirects, List<Form> forms, List<FormSubmission> formSubmissions,
		List<SearchResult> searchResults) implements Collections {
}
