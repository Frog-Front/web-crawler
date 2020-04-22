package com.frogfront.webcrawler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.frogfront.webcrawler.api.WebCrawler;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Jsoup.class })

public class WebCrawlerTest {

	@Test
	public void testWebCrwaler() throws IOException {

		Document doc1 = Jsoup.parse(this.getClass().getResourceAsStream("/index.html"), null,
				"http://example.com/index.html");
		Document doc2 = Jsoup.parse(this.getClass().getResourceAsStream("/foo.html"), null,
				"http://example.com/foo.html");
		Document doc3 = Jsoup.parse(this.getClass().getResourceAsStream("/bar.html"), null,
				"http://example.com/bar.html");

		Connection connection1 = Mockito.mock(Connection.class);
		Connection connection2 = Mockito.mock(Connection.class);
		Connection connection3 = Mockito.mock(Connection.class);
		Connection.Response response1 = Mockito.mock(Connection.Response.class);
		Connection.Response response2 = Mockito.mock(Connection.Response.class);
		Connection.Response response3 = Mockito.mock(Connection.Response.class);

		PowerMockito.mockStatic(Jsoup.class);

		Mockito.when(Jsoup.connect("http://example.com")).thenReturn(connection1);
		Mockito.when(connection1.method(Connection.Method.GET)).thenReturn(connection1);
		Mockito.when(connection1.execute()).thenReturn(response1);
		Mockito.when(response1.parse()).thenReturn(doc1);

		Mockito.when(Jsoup.connect("http://example.com/foo.html")).thenReturn(connection2);
		Mockito.when(connection2.method(Connection.Method.GET)).thenReturn(connection2);
		Mockito.when(connection2.execute()).thenReturn(response2);
		Mockito.when(response2.parse()).thenReturn(doc2);

		Mockito.when(Jsoup.connect("http://example.com/bar.html")).thenReturn(connection3);
		Mockito.when(connection3.method(Connection.Method.GET)).thenReturn(connection3);
		Mockito.when(connection3.execute()).thenReturn(response3);
		Mockito.when(response3.parse()).thenReturn(doc3);

		ByteArrayOutputStream actualOs = new ByteArrayOutputStream();
		ReportingLocationProvider locationProvider = new ReportingLocationProvider(actualOs);

		InputStream robotsStream = this.getClass().getResourceAsStream("/robots.txt");
		RobotsTxtParser robotsParser = new RobotsTxtParser(new URL("http://example.com"), robotsStream);

		WebCrawler webCrawler = new DomainWebCrawler().useRobotstxt(robotsParser).useLocationProvider(locationProvider);
		webCrawler.crawlUrl(new URL("http://example.com"));

		String expected = IOUtils.toString(this.getClass().getResourceAsStream("/crawler-out.txt"));
		String actual = new String(actualOs.toByteArray());
		actualOs.close();
		assertThat(actual, equalTo(expected));
		actualOs.close();
	}

	@Test
	public void testNormilizingUrl() throws IOException {
		Document doc1 = Jsoup.parse("<html></html>");

		Connection connection1 = Mockito.mock(Connection.class);
		Connection.Response response1 = Mockito.mock(Connection.Response.class);

		PowerMockito.mockStatic(Jsoup.class);

		Mockito.when(Jsoup.connect("http://example.com")).thenReturn(connection1);
		Mockito.when(connection1.method(Connection.Method.GET)).thenReturn(connection1);
		Mockito.when(connection1.execute()).thenReturn(response1);
		Mockito.when(response1.parse()).thenReturn(doc1);

		LocationProviderStub locationProvider = new LocationProviderStub();
		WebCrawler webCrawler = new DomainWebCrawler().useLocationProvider(locationProvider);
		webCrawler.crawlUrl(new URL("http://example.com/"));
		String expected = "http://example.com";
		String actual = locationProvider.location;

		assertThat(actual, equalTo(expected));

	}

	@Test
	public void testRobotsDisallow() throws IOException {
		Document doc1 = Jsoup.parse(this.getClass().getResourceAsStream("/index.html"), null,
				"http://example.com/index.html");
		Document doc2 = Jsoup.parse(this.getClass().getResourceAsStream("/foo.html"), null,
				"http://example.com/foo.html");
		Document doc3 = Jsoup.parse(this.getClass().getResourceAsStream("/bar.html"), null,
				"http://example.com/bar.html");

		Connection connection1 = Mockito.mock(Connection.class);
		Connection connection2 = Mockito.mock(Connection.class);
		Connection connection3 = Mockito.mock(Connection.class);
		Connection.Response response1 = Mockito.mock(Connection.Response.class);
		Connection.Response response2 = Mockito.mock(Connection.Response.class);
		Connection.Response response3 = Mockito.mock(Connection.Response.class);

		PowerMockito.mockStatic(Jsoup.class);

		Mockito.when(Jsoup.connect("http://example.com")).thenReturn(connection1);
		Mockito.when(connection1.method(Connection.Method.GET)).thenReturn(connection1);
		Mockito.when(connection1.execute()).thenReturn(response1);
		Mockito.when(response1.parse()).thenReturn(doc1);

		Mockito.when(Jsoup.connect("http://example.com/foo.html")).thenReturn(connection2);
		Mockito.when(connection2.method(Connection.Method.GET)).thenReturn(connection2);
		Mockito.when(connection2.execute()).thenReturn(response2);
		Mockito.when(response2.parse()).thenReturn(doc2);

		Mockito.when(Jsoup.connect("http://example.com/bar.html")).thenReturn(connection3);
		Mockito.when(connection3.method(Connection.Method.GET)).thenReturn(connection3);
		Mockito.when(connection3.execute()).thenReturn(response3);
		Mockito.when(response3.parse()).thenReturn(doc3);

		ByteArrayOutputStream actualOs = new ByteArrayOutputStream();
		ReportingLocationProvider locationProvider = new ReportingLocationProvider(actualOs);
		InputStream robotsStream = this.getClass().getResourceAsStream("/robots-disallow.txt");
		RobotsTxtParser robotsParser = new RobotsTxtParser(new URL("http://example.com"), robotsStream);

		WebCrawler webCrawler = new DomainWebCrawler().useRobotstxt(robotsParser).useLocationProvider(locationProvider);
		webCrawler.crawlUrl(new URL("http://example.com"));

		String expected = "";
		String actual = new String(actualOs.toByteArray());

		assertThat(actual, equalTo(expected));
		actualOs.close();
	}

	@Test
	public void unsupportedMimeTypeExceptionTest() throws IOException {
		Document doc1 = Jsoup.parse(this.getClass().getResourceAsStream("/index.html"), null,
				"http://example.com/index.html");
		Document doc2 = Jsoup.parse(this.getClass().getResourceAsStream("/foo.html"), null,
				"http://example.com/foo.html");
		Document doc3 = Jsoup.parse(this.getClass().getResourceAsStream("/UnsupportedMimeType.html"), null,
				"http://example.com/bar.html");

		Connection connection1 = Mockito.mock(Connection.class);
		Connection connection2 = Mockito.mock(Connection.class);
		Connection connection3 = Mockito.mock(Connection.class);
		Connection connectionUnsupported = Mockito.mock(Connection.class);
		Connection.Response response1 = Mockito.mock(Connection.Response.class);
		Connection.Response response2 = Mockito.mock(Connection.Response.class);
		Connection.Response response3 = Mockito.mock(Connection.Response.class);

		PowerMockito.mockStatic(Jsoup.class);

		Mockito.when(Jsoup.connect("http://example.com")).thenReturn(connection1);
		Mockito.when(connection1.method(Connection.Method.GET)).thenReturn(connection1);
		Mockito.when(connection1.execute()).thenReturn(response1);
		Mockito.when(response1.parse()).thenReturn(doc1);

		Mockito.when(Jsoup.connect("http://example.com/foo.html")).thenReturn(connection2);
		Mockito.when(connection2.method(Connection.Method.GET)).thenReturn(connection2);
		Mockito.when(connection2.execute()).thenReturn(response2);
		Mockito.when(response2.parse()).thenReturn(doc2);

		Mockito.when(Jsoup.connect("http://example.com/bar.html")).thenReturn(connection3);
		Mockito.when(connection3.method(Connection.Method.GET)).thenReturn(connection3);
		Mockito.when(connection3.execute()).thenReturn(response3);
		Mockito.when(response3.parse()).thenReturn(doc3);

		Mockito.when(Jsoup.connect("http://example.com/image.png")).thenReturn(connectionUnsupported);
		Mockito.when(connectionUnsupported.method(Connection.Method.GET)).thenReturn(connectionUnsupported);
		Mockito.when(connectionUnsupported.execute()).thenThrow(new UnsupportedMimeTypeException("", "", ""));

		ByteArrayOutputStream actualOs = new ByteArrayOutputStream();
		ReportingLocationProvider locationProvider = new ReportingLocationProvider(actualOs);

		InputStream robotsStream = this.getClass().getResourceAsStream("/robots.txt");
		RobotsTxtParser robotsParser = new RobotsTxtParser(new URL("http://example.com"), robotsStream);

		WebCrawler webCrawler = new DomainWebCrawler().useRobotstxt(robotsParser).useLocationProvider(locationProvider);
		webCrawler.crawlUrl(new URL("http://example.com"));

		String expected = IOUtils.toString(this.getClass().getResourceAsStream("/unsupported-out.txt"));
		String actual = new String(actualOs.toByteArray());
		actualOs.close();
		assertThat(actual, equalTo(expected));
		actualOs.close();
	}

	@Test
	public void testHTTPStatus() throws IOException {

		PowerMockito.mockStatic(Jsoup.class);

		Connection connection1 = Mockito.mock(Connection.class);
		HttpStatusException statusException = Mockito.mock(HttpStatusException.class);

		Mockito.when(Jsoup.connect("http://example.com")).thenReturn(connection1);
		Mockito.when(connection1.method(Connection.Method.GET)).thenReturn(connection1);
		Mockito.when(connection1.execute()).thenThrow(statusException);
		Mockito.when(statusException.getStatusCode()).thenReturn(404);

		LocationProviderStub locationProvider = new LocationProviderStub();
		WebCrawler webCrawler = new DomainWebCrawler().useLocationProvider(locationProvider);
		webCrawler.crawlUrl(new URL("http://example.com"));

		assertEquals(Integer.parseInt(locationProvider.httpStatus), 404);

	}

}
