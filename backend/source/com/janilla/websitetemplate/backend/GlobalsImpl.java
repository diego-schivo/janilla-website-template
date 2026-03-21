package com.janilla.websitetemplate.backend;

import com.janilla.websitetemplate.Footer;
import com.janilla.websitetemplate.Header;

public record GlobalsImpl(Header header, Footer footer) implements Globals {
}
