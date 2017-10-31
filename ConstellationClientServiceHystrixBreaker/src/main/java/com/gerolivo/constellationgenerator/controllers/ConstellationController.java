package com.gerolivo.constellationgenerator.controllers;

import java.util.LinkedHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.gerolivo.constellationgenerator.domain.Constellation;
import com.gerolivo.constellationgenerator.services.StarsServiceImpl;

@RestController
public class ConstellationController {
	
	private static final Logger LOG = Logger.getLogger(ConstellationController.class);
	
	@Autowired
	StarsServiceImpl starsService;
	
	@Bean
	@LoadBalanced
	public RestTemplate rest() {
		return new RestTemplateBuilder().build();
	}
	
	@GetMapping("/")
	public Constellation getConstellation() {
		return getConstellation(false, false);

	}
	
	@GetMapping("/forceHystrixOpen")
	public Constellation forceHystrixOpen() {
		return getConstellation(false, true);
	}
	
	@GetMapping("/forceHystrixClose")
	public Constellation forceHystrixClose() {
		return getConstellation(true, false);
	}
	
	private Constellation getConstellation(boolean forceClose, boolean forceOpen) {
		LOG.log(Level.INFO, "Called getConstellation");
		String alphaStar = starsService.getStar("alpha-stars-service", forceClose, forceOpen);
//		String betaStar = starsService.getStar("beta-stars-service", false, false);
//		String gammaStar = starsService.getStar("gamma-stars-service");
		String deltaStar = starsService.getDeltaStarFeign();
		LinkedHashMap<String, String> stars = new LinkedHashMap<>();
		stars.put("Alpha star: ", alphaStar);
//		stars.put("Beta star: ", betaStar);
//		stars.put("Gamma star: ", gammaStar);
		stars.put("Delta star: ", deltaStar);
		Constellation constellation = new Constellation("Constellation", stars);
		return constellation;
	}

}
