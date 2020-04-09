package com.frogfront.webcrawler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.frogfront.webcrawler.api.LocationSource;
import com.frogfront.webcrawler.api.LocationSource.LocationType;
import com.frogfront.webcrawler.api.LocationSource.ParameterNames;

public class ReportingLocationProviderTest {

	@Test
	public void testLocationProviderOutput() throws IOException {
		ByteArrayOutputStream actualBos = new ByteArrayOutputStream();
		ReportingLocationProvider rlp = new ReportingLocationProvider(actualBos);

		HashMap<ParameterNames, String> params = new HashMap<>();
		params.put(LocationSource.ParameterNames.LAST_MODIFIED, "foo");
		params.put(LocationSource.ParameterNames.RAW, "bar");
		HashMap<String, LocationType> locations = new HashMap<>();
		locations.put("http://example.com/foo", LocationSource.LocationType.DOMAIN);
		locations.put("http://example.com/foo.png", LocationSource.LocationType.DOMAIN_IMAGE);
		locations.put("http://google.com/foo", LocationSource.LocationType.EXTERNAL);
		locations.put("http://google.com/foo.png", LocationSource.LocationType.EXTERNAL_IMAGE);

		rlp.newLocationSource().useParameters(params).useLocaton("http://example.com","200").useEmbededUrls(locations);
		rlp.execute();

		String expected = IOUtils.toString(this.getClass().getResourceAsStream("/rlp-out.txt"));
		String actual =  new String(actualBos.toByteArray());
		assertThat(actual, equalTo(expected));
		
	}
	
}
