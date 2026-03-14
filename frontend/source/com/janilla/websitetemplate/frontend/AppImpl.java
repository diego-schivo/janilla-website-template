package com.janilla.websitetemplate.frontend;

import java.util.Map;

import com.janilla.frontend.App;

record AppImpl(String key, String apiUrl, Map<String, Object> state) implements App {
}
