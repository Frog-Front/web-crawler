package com.frogfront.webcrawler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frogfront.webcrawler.api.LocationProvider;
import com.frogfront.webcrawler.api.LocationSource;
import com.frogfront.webcrawler.api.LocationSource.LocationType;
import com.frogfront.webcrawler.api.LocationSource.ParameterNames;

public class ReportingLocationProvider implements LocationProvider {

	private String locationUrl;
	private Map<ParameterNames, String> parameters;
	private Map<String, LocationType> embededURLs;

	private OutputStream outputStream;

	public ReportingLocationProvider(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	private LocationSource locationSource = new LocationSource() {

		@Override
		public LocationSource useLocaton(String locUrl) {
			locationUrl = locUrl;
			return this;
		}

		@Override
		public LocationSource useParameters(Map<ParameterNames, String> params) {
			parameters = params;
			return this;
		}

		@Override
		public LocationSource useEmbededUrls(Map<String, LocationType> emURLs) {
			embededURLs = emURLs;
			return this;
		}

	};

	@Override
	public LocationSource newLocationSource() {
		this.locationUrl = null;
		this.parameters = new HashMap<ParameterNames, String>();
		this.embededURLs = new HashMap<String, LocationType>();
		return locationSource;
	}

	@Override
	public void execute() {

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("\nBASE URL " + this.locationUrl + "\n");
		strBuilder.append("Parameters\n");
		this.parameters.forEach((k, v) -> {
			if (k != (LocationSource.ParameterNames.RAW)) {
				strBuilder.append(k + "::" + v + "\n");
			}

		});
		strBuilder.append("\n");
		strBuilder.append(this.buildEmbedded(LocationType.DOMAIN)).append(this.buildEmbedded(LocationType.DOMAIN_IMAGE))
				.append(this.buildEmbedded(LocationType.EXTERNAL))
				.append(this.buildEmbedded(LocationType.EXTERNAL_IMAGE));

		try {
			this.outputStream.write(strBuilder.toString().getBytes());
		} catch (IOException e) {
			log.error("IOException thrown writing output for " + this.locationUrl, e);
		}

	}

	private String buildEmbedded(LocationType type) {
		StringBuilder strBuilder = new StringBuilder();
		List<String> urlList = this.embededURLs.entrySet().stream().filter(e -> e.getValue() == type)
				.map(Map.Entry::getKey).collect(Collectors.toList());
		if (urlList.isEmpty()) {
			return "";
		}
		strBuilder.append("\t" + type.name() + "\n");
		urlList.forEach(v -> strBuilder.append("\t\t" + v + "\n"));
		return strBuilder.toString();
	}

	private static Logger log = LoggerFactory.getLogger(ReportingLocationProvider.class);

}
