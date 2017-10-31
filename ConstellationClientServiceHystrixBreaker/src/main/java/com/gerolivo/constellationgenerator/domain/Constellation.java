package com.gerolivo.constellationgenerator.domain;

import java.util.LinkedHashMap;

public class Constellation {

	private String name;
	private LinkedHashMap<String, String> stars = new LinkedHashMap<>();

	public Constellation(String name, LinkedHashMap<String, String> stars) {
		this.name = name;
		this.stars = stars;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedHashMap<String, String> getStars() {
		return stars;
	}

	public void setStars(LinkedHashMap<String, String> stars) {
		this.stars = stars;
	}


}
