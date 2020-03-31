package com.frogfront.webcrawler.api;

public interface LocationProvider {

	public LocationSource newLocationSource();

	public void execute();

}
