package com.frogfront.webcrawler.api;

import java.net.URL;

public interface WebCrawler {
	
	public void useRobotstxt(RobotsTxt robotsTxt);
	
	public void useLocationProvider(LocationProvider locationProvider);
	
	public void crawlUrl(URL url);
}
