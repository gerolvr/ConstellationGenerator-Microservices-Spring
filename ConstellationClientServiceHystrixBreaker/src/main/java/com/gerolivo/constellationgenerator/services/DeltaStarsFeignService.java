package com.gerolivo.constellationgenerator.services;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name="delta-stars-service"/*, fallback=DeltaStarsFeignServiceImpl.class*/)
public interface DeltaStarsFeignService {

		@RequestMapping("/")
		public String getStar();

}