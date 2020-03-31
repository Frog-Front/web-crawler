## Web Crawler

![alt travis](https://travis-ci.org/Frog-Front/web-crawler.svg?branch=master)

Web Crawler is a recursive link extractor which crawls a domains website in its entirety respecting rules implemented in `robots.txt`. The implementing contains a simple reporting generator writing to an output stream of your choice in the following format for each page within the domain.

```
BASE URL http://example.com
Parameters
LAST_MODIFIED::

	DOMAIN
		http://example.com/foo
	DOMAIN_IMAGE
		http://example.com/foo.png
	EXTERNAL
		http://google.com/foo
	EXTERNAL_IMAGE
		http://google.com/foo.png
```
This format may be changed by implementing a custom `LocationProvider` so that output may be written in json and sent to other services or a `sitemap.xml` could be created.

### Building
The project is built using [gradle](https://gradle.org/). Once [installed](https://gradle.org/install/) building the project is done with the following command.

```bash
$> gradle
...
BUILD SUCCESSFUL in 17s
6 actionable tasks: 6 executed
```

### Usage
The package can be included in your project via `jcenter` with the corresponding bintray [repository](https://bintray.com/cuzz22000/com.frogfront/web-crawler/). There you will find the appropriate dependency resolution for your build tool. 

Example implementation:

```java
String domainToCrawl = "http://example.com";
String domainRobotsTxt = "http://example.com/robots.txt";
WebCrawler webCrawler = new DomainWebCrawler();
File outFile = new File("your output file);
FileOutputStream fileOutputStream = new FileOutputStream(outFile);
LocationProvider locationProvider = new ReportingLocationProvider(fileOutputStream);
RobotsTxt robotsTxt = new RobotsTxtParser(domainRobotsTxt);
webCrawler.useRobotstxt(robotsTxt);		
webCrawler.useLocationProvider(locationProvider);
webCrawler.crawlUrl(new URL(domainToCrawl));

```

### Continuous Integration
Currently using [TravcisCI](https://travis-ci.org/github/Frog-Front/web-crawler/)

### Future Plans

 - Include functionality to extract links based on `sitemaps`.
 - The HTML parsing is using [JSoup](https://jsoup.org/). Investigating a pure SAX implementation would be worth the effort to gain performance.
 - Include more `LocationProvider`s for other specifications. eg Sitemaps.

