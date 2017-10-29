package com.gerolivo.constellationgenerator.controllers;

import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StarsController {

	private static final Logger logger = Logger.getLogger(StarsController.class);
	
	@Value("${stars}")
	private String stars;
	
	@GetMapping("/")
	public String getStar() {
		String[] starsArray = stars.split(", ");
		int randomIndex = new Random().nextInt(starsArray.length);
		String star = (starsArray[randomIndex]);
		logger.info("Returning: " + star);
		return star;
	}
}
