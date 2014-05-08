package com.github.bbva.logsReader.utils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.bbva.logsReader.dto.TuplaDto;

/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 *
 */
public class ResultDto {

	Map<String, List<TuplaDto>> agrupador;

	public ResultDto() {
		agrupador = new TreeMap<String, List<TuplaDto>>();
	}
	
	public Map<String, List<TuplaDto>> getAgrupador() {
		return agrupador;
	}

	
}
