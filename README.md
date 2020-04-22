## Web Crawler

![alt travis](https://travis-ci.org/Frog-Front/web-crawler.svg?branch=master) [![Maven Central](https://img.shields.io/maven-central/v/com.frogfront/web-crawler.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.frogfront%22%20AND%20a:%22web-crawler%22) [ ![Download](https://api.bintray.com/packages/frog-front/com.frogfront/web-crawler/images/download.svg) ](https://bintray.com/frog-front/com.frogfront/web-crawler/_latestVersion)

Web Crawler is a recursive link extractor which crawls a domains website in its entirety respecting rules implemented in `robots.txt`. The implementation contains a simple reporting provider writing to an output stream of your choice in the following format for each page within the domain.

```
BASE URL http://example.com
Parameters
LAST_MODIFIED::

HTTP STATUS 200

	DOMAIN
		http://example.com/foo
	DOMAIN_IMAGE
		http://example.com/foo.png
	EXTERNAL
		http://google.com/foo
	EXTERNAL_IMAGE
		http://google.com/foo.png
```
This format may be changed by implementing a custom `LocationProvider` so that output may be written in any format and sent to other services or a `sitemap.xml` could be created.

### Building
The project is built using [gradle](https://gradle.org/). Once [installed](https://gradle.org/install/) building the project is done with the following command.

```bash
$> gradle
...
$>
```

### Usage
The package is available from [Maven Central](https://search.maven.org/artifact/com.frogfront/web-crawler) and [JCenter](https://bintray.com/cuzz22000/com.frogfront/web-crawler/). You will find the appropriate dependency resolution for your specific build tool. 

Example implementation:

```java
String domainToCrawl = "http://example.com";
String domainRobotsTxt = "http://example.com/robots.txt";
File outFile = new File("your output file);
FileOutputStream fileOutputStream = new FileOutputStream(outFile);
LocationProvider locationProvider = new ReportingLocationProvider(fileOutputStream);
RobotsTxt robotsTxt = new RobotsTxtParser(domainRobotsTxt);
WebCrawler webCrawler = new DomainWebCrawler()
	.useRobotstxt(robotsTxt);		
	.useLocationProvider(locationProvider);
webCrawler.crawlUrl(new URL(domainToCrawl));

```

### Continuous Integration
[TravcisCI](https://travis-ci.org/github/Frog-Front/web-crawler/) 

### Continuous Deployment
Project is deployed to [Maven Central](https://search.maven.org/artifact/com.frogfront/web-crawler) and [JCenter](https://bintray.com/cuzz22000/com.frogfront/web-crawler/) via [TravcisCI](https://travis-ci.org/github/Frog-Front/web-crawler/) on tagged releases.