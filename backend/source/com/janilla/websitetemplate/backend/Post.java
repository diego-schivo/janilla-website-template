package com.janilla.websitetemplate.backend;

import java.util.List;

import com.janilla.backend.cms.Document;

public interface Post extends Document<Long> {

	List<Long> relatedPosts();

	Post withRelatedPosts(List<Long> relatedPosts);
}
