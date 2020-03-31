package com.frogfront.webcrawler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import crawlercommons.robots.SimpleRobotRules;

public class RobotsTxtTest {

	@Test
	public void testLoadRobots() throws IOException {
		URL robotsUrl = new URL("http://example.com");
		InputStream robotsStream = this.getClass().getResourceAsStream("/robots.txt");
		RobotsTxtParser robotsParser = new RobotsTxtParser(robotsUrl,robotsStream);
		SimpleRobotRules rules = robotsParser.getRules();
		robotsStream.close();

		InputStream robotsStreamActual = this.getClass().getResourceAsStream("/simplerules.txt");
		ByteSource source = new ByteSource() {

			@Override
			public InputStream openStream() throws IOException {
				return robotsStreamActual;
			}
		};
		String actual = source.asCharSource(Charsets.UTF_8).read();
		robotsStreamActual.close();
		assertThat(actual, equalTo(rules.toString()));

	}

	@Test(expected = FileNotFoundException.class)
	public void testFileNotFound() throws IOException, FileNotFoundException {
		URL robotsUrl = new URL("http://example.com/robots.txt");
		new RobotsTxtParser(robotsUrl);
	}

	@Test
	public void testCannotProcess() throws IOException {
		
		URL robotsUrl = new URL("http://example.com");
		InputStream robotsStream = this.getClass().getResourceAsStream("/robots.txt");
		RobotsTxtParser robotsParser = new RobotsTxtParser(robotsUrl,robotsStream);
		SimpleRobotRules rules = robotsParser.getRules();
		robotsStream.close();
		boolean canProcess = rules.isAllowed("http://example.com/cgi-bin/");
		Assert.assertTrue(!canProcess);
	}

	@Test
	public void testCanProcess() throws IOException {
		URL robotsUrl = new URL("http://example.com");
		InputStream robotsStream = this.getClass().getResourceAsStream("/robots.txt");
		RobotsTxtParser robotsParser = new RobotsTxtParser(robotsUrl,robotsStream);
		SimpleRobotRules rules = robotsParser.getRules();
		robotsStream.close();
		boolean canProcess = rules.isAllowed("http://example.com/index.html");
		Assert.assertTrue(canProcess);
	}

}
