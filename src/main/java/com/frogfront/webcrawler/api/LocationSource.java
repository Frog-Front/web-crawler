package com.frogfront.webcrawler.api;

import java.util.Map;

public interface LocationSource {

	public static enum LocationType {
		DOMAIN, EXTERNAL, DOMAIN_IMAGE, EXTERNAL_IMAGE
	};

	public static enum ParameterNames {
		LAST_MODIFIED, RAW
	}

	public LocationSource useLocaton(String locationUrl);

	public LocationSource useParameters(Map<ParameterNames, String> parameters);

	public LocationSource useEmbededUrls(Map<String, LocationType> embededURls);

}
