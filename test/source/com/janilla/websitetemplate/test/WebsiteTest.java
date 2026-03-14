package com.janilla.websitetemplate.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.janilla.blanktemplate.test.BlankTest;
import com.janilla.ioc.DefaultDiFactory;
import com.janilla.ioc.DiFactory;
import com.janilla.java.Java;
import com.janilla.websitetemplate.fullstack.WebsiteFullstack;

public class WebsiteTest extends BlankTest {

	public static final String[] DI_PACKAGES = Stream
			.concat(Arrays.stream(BlankTest.DI_PACKAGES), Stream.of("com.janilla.websitetemplate.test"))
			.toArray(String[]::new);

	public static void main(String[] args) {
		IO.println(ProcessHandle.current().pid());
		var f = new DefaultDiFactory(
				Arrays.stream(DI_PACKAGES).flatMap(x -> Java.getPackageClasses(x, false).stream()).toList());
		serve(f, WebsiteTest.class, args.length > 0 ? args[0] : null);
	}

	public WebsiteTest(DiFactory diFactory, Path configurationFile) {
		this(diFactory, configurationFile, "website-template");
	}

	public WebsiteTest(DiFactory diFactory, Path configurationFile, String configurationKey) {
		super(diFactory, configurationFile, configurationKey);
	}

	@Override
	protected String[] diFullstackPackages() {
		return WebsiteFullstack.DI_PACKAGES;
	}

	@Override
	protected Map<String, List<Path>> resourcePaths() {
		return Map.of("",
				Stream.of("com.janilla.frontend", "com.janilla.blanktemplate.test", "com.janilla.websitetemplate.test")
						.flatMap(x -> Java.getPackagePaths(x, false).filter(Files::isRegularFile)).toList());
	}
}
