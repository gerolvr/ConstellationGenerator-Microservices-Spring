package com.gerolivo.constellationgenerator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class StarsServiceImpl implements StarsService {
	
	@Bean
	@LoadBalanced
	public RestTemplate rest() {
		return new RestTemplateBuilder().build();
	}
	
	@Autowired
	RestTemplate rest;
	
	@Autowired
	DeltaStarsFeignService deltaStarsFeignService;

	/**
	 * Use the restTemplate with the Ribbon client-side load balancing.
	 * @param serviceName the service to call to get a star name
	 */
	@Override
	@HystrixCommand(fallbackMethod = "starsServiceNotFound")
	public String getStar(String serviceName) {
		return rest.getForObject("http://" + serviceName, String.class);
	}
	
	/**
	 * Use declarative feign rest client, which is also
	 * Ribbon load-balanced
	 * If not using feign own circuit breaker (feign.hystrix.enabled=true,
	 * based on Hystrix anyway), use this annotation:
	 * @HystrixCommand(fallbackMethod = "startServiceNotFound")
	 */
	@HystrixCommand(fallbackMethod = "starsServiceNotFoundFeign")
	public String getDeltaStarFeign() {
		return deltaStarsFeignService.getStar();
	}

	/**
	 * Fallback methods for Hystrix
	 * @param service
	 */
	public String starsServiceNotFound(String service) {
		return "Blackhole from " + service;
	}
	
	public String starsServiceNotFoundFeign() {
		return "Feign fallback Blackhole";
	}

}
