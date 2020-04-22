package com.frogfront.webcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frogfront.webcrawler.api.LocationProvider;
import com.frogfront.webcrawler.api.LocationSource;
import com.frogfront.webcrawler.api.LocationSource.LocationType;
import com.frogfront.webcrawler.api.LocationSource.ParameterNames;
import com.frogfront.webcrawler.api.RobotsTxt;
import com.frogfront.webcrawler.api.WebCrawler;

public class DomainWebCrawler implements WebCrawler {

	private LocationProvider locationProvider;
	private RobotsTxt robotsTxt;

	@Override
	public WebCrawler useRobotstxt(RobotsTxt robotsTxt) {
		this.robotsTxt = robotsTxt;
		return this;
	}

	@Override
	public WebCrawler useLocationProvider(LocationProvider locationProvider) {
		this.locationProvider = locationProvider;
		return this;
	}

	@Override
	public void crawlUrl(URL url) {
		if (this.locationProvider == null) {
			throw new IllegalStateException("LocationProvider cannot be null");
		}

		final LinkedHashSet<String> urls = new LinkedHashSet<>();
		final HashSet<String> crawledUrls = new HashSet<>();
		try {
			final Pattern pattern = Pattern.compile("^.*#.*");
			urls.add(this.normilizeUrl(url.toString()));
			Iterator<String> urlIt = urls.iterator();
			while (urlIt.hasNext()) {
				final String nextUrl = urlIt.next();
				Map<String, LocationType> embededURLs = new HashMap<>();
				HashMap<ParameterNames, String> urlParameters = new HashMap<>();
				if (crawledUrls.contains(nextUrl) || (this.robotsTxt != null && !this.robotsTxt.canProcess(nextUrl))) {
					urls.remove(nextUrl);
					urlIt = urls.iterator();
					continue;
				}
				Connection.Response connectionResponse = null;
				String statusCode = "";
				try {
					connectionResponse = Jsoup.connect(nextUrl).method(Connection.Method.GET).execute();
					statusCode = String.valueOf(connectionResponse.statusCode());
				} catch (UnsupportedMimeTypeException | MalformedURLException | SocketTimeoutException
						| HttpStatusException e) {
					urls.remove(nextUrl);
					crawledUrls.add(nextUrl);
					urlIt = urls.iterator();
					if (e instanceof HttpStatusException) {
						this.executeProvider(nextUrl, String.valueOf(((HttpStatusException) e).getStatusCode()), embededURLs, urlParameters);
					}
					continue;
				}
				try {
					String lastModified = connectionResponse.headers("Last-Modified").get(0);
					urlParameters.put(LocationSource.ParameterNames.LAST_MODIFIED, lastModified);
				} catch (IndexOutOfBoundsException e) {
					// no header value
				}
				urlParameters.put(LocationSource.ParameterNames.RAW, connectionResponse.body());
				final Document doc = connectionResponse.parse();
				final Elements hrefs = doc.select("a[href]");
				hrefs.forEach(e -> {
					final String newUrl = e.absUrl("href");
					Matcher matcher = pattern.matcher(newUrl);
					if (newUrl.startsWith(url.toString()) && !matcher.matches()) {
						urls.add(this.normilizeUrl(newUrl));
						embededURLs.put(newUrl, LocationType.DOMAIN);
					} else if (matcher.matches()) {
						embededURLs.put(newUrl, LocationType.DOMAIN);
					} else {
						embededURLs.put(newUrl, LocationType.EXTERNAL);
					}
				});
				Elements images = doc.select("img[src]");
				images.forEach(e -> {
					final String newUrl = e.absUrl("src");
					embededURLs.put(newUrl,
							newUrl.contains(url.getHost()) ? LocationType.DOMAIN_IMAGE : LocationType.EXTERNAL_IMAGE);
				});
				crawledUrls.add(nextUrl);
				urls.remove(nextUrl);
				urlIt = urls.iterator();
				this.executeProvider(nextUrl, statusCode, embededURLs, urlParameters);
			}

		} catch (IOException e) {
			log.error("Error processing domain " + url.toString(), e);

		}

	}

	protected void executeProvider(String location, String status, Map<String, LocationType> embededURLs,
			Map<ParameterNames, String> urlParameters) {
		this.locationProvider.newLocationSource().useLocaton(location, status).useEmbededUrls(embededURLs)
				.useParameters(urlParameters);
		this.locationProvider.execute();
	}

	private String normilizeUrl(String url) {
		if (url.endsWith("/")) {
			return url.substring(0, url.length() - 1);
		}
		return url;
	}

	private static Logger log = LoggerFactory.getLogger(DomainWebCrawler.class);

}
