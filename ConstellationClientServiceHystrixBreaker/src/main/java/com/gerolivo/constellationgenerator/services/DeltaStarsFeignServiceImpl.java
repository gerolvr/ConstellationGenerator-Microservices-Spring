package com.gerolivo.constellationgenerator.services;

import org.springframework.stereotype.Component;

/**
 * For Feign fallback
 * @author gerolvr
 *
 */
@Component
public class DeltaStarsFeignServiceImpl implements DeltaStarsFeignService {
	
	@Override
    public String getStar(){
          return "Feign fallback Blackhole";
     }
}