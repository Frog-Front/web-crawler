package com.frogfront.webcrawler.app;

import java.net.MalformedURLException;
import java.net.URL;

import com.frogfront.webcrawler.DomainWebCrawler;
import com.frogfront.webcrawler.ReportingLocationProvider;
import com.frogfront.webcrawler.api.LocationProvider;
import com.frogfront.webcrawler.api.WebCrawler;

/**
 * Static main invoking API for testing to standard out
 * Argument containing url to crawl must be provided.
 *
 */
public class AppBootStrap {

	public static void main(String[] args) throws MalformedURLException {
		LocationProvider locationProvider = new ReportingLocationProvider(System.out);
		WebCrawler webCrawler = new DomainWebCrawler().useLocationProvider(locationProvider);
		webCrawler.crawlUrl(new URL(args[0]));
	}

}
