package com.janilla.websitetemplate.backend;

import java.util.List;

import com.janilla.backend.cms.Document;

public interface Post0 extends Document<Long> {

	List<Long> relatedPosts();

	Post0 withRelatedPosts(List<Long> relatedPosts);
}
