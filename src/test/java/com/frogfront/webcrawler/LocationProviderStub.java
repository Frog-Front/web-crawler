package com.frogfront.webcrawler;

import java.util.Map;

import com.frogfront.webcrawler.api.LocationProvider;
import com.frogfront.webcrawler.api.LocationSource;
import com.frogfront.webcrawler.api.LocationSource.LocationType;
import com.frogfront.webcrawler.api.LocationSource.ParameterNames;

public class LocationProviderStub implements LocationProvider {

	String location;
	String httpStatus;
	Map<ParameterNames, String> parameters;
	Map<String, LocationType> embededURls;

	@Override
	public LocationSource newLocationSource() {
		return new LocationSource(){

			@Override
			public LocationSource useLocaton(String locUrl, String status) {
				location = locUrl;
				httpStatus = status;
				return this;
			}

			@Override
			public LocationSource useParameters(Map<ParameterNames, String> params) {
				parameters = params;
				return this;
			}

			@Override
			public LocationSource useEmbededUrls(Map<String, LocationType> EMURLs) {
				embededURls = EMURLs;
				return this;
			}
		};
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

}
