package com.frogfront.webcrawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.frogfront.webcrawler.api.RobotsTxt;
import com.google.common.io.ByteStreams;

import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;

public class RobotsTxtParser implements RobotsTxt {
	
	private URL robotsUrl;
	private SimpleRobotRules robotsRule;
	
	public RobotsTxtParser(URL rebotsUrl) throws IOException{
		this.robotsUrl = rebotsUrl;
		final InputStream robotsIs = robotsUrl.openStream();
		fetchRules(robotsIs);
		robotsIs.close();
	}
	
	public RobotsTxtParser(URL rebotsUrl, InputStream robotsStream) throws IOException {
		this.robotsUrl = rebotsUrl;
		this.fetchRules(robotsStream);
	}
	
	/* (non-Javadoc)
	 * @see com.frogfront.webcrawler.IRobotsTxt#canProcess(java.lang.String)
	 */
	@Override
	public boolean canProcess(String url) {
		return this.robotsRule.isAllowed(url);
	}
	
	protected SimpleRobotRules getRules() {
		return this.robotsRule;
	}
		
	private void fetchRules(InputStream robotsStream) throws IOException {
			final byte[] bytes = ByteStreams.toByteArray(robotsStream);
			final SimpleRobotRulesParser robotsParser = new SimpleRobotRulesParser();
			this.robotsRule = robotsParser.parseContent(this.robotsUrl.toString(), bytes, null, "");
	}

}
