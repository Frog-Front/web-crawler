package com.frogfront.webcrawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class FooTests {
	
	@Test
	public void matcherTest() {
		final Pattern pattern = Pattern.compile("^.*#.*");
		Matcher matcher = pattern.matcher("http://www.cjwilson.org/#page-top");
		if(matcher.matches()) {
			System.out.println();
		}
		if(!matcher.matches()) {
			System.out.println(false);
		}
	}

}
