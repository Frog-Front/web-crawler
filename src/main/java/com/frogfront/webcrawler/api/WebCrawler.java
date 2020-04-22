package com.frogfront.webcrawler.api;

import java.net.URL;

public interface WebCrawler {
	
	public WebCrawler useRobotstxt(RobotsTxt robotsTxt);
	
	public WebCrawler useLocationProvider(LocationProvider locationProvider);
	
	public void crawlUrl(URL url);
}
