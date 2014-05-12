package com.github.bbva.logsReader.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.bbva.logsReader.db.DBConnection;
import com.github.bbva.logsReader.db.RepositoryCollections;
import com.github.bbva.logsReader.dto.InfoServicesDTO;
import com.github.bbva.logsReader.dto.ResultDTO;
import com.github.bbva.logsReader.dto.TuplaDto;
import com.github.bbva.logsReader.utils.FrecuencyGaussianDto;

/**
 * 
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
@Controller
public class LogReaderControler {

	@Autowired
	private DBConnection connection;
	@Autowired
	private RepositoryCollections repository;

	@RequestMapping("/Datoo")
	public String redirect() {
		System.out.println("\n\n\nDATO\n\n\n");
		return "index.html";
	}

	@RequestMapping(value = { "/StandardDeviation" }, method = { RequestMethod.GET }, produces = "text/plain")
	public @ResponseBody
	String getStandardDeviation() {
		Map<String, String> map = repository.getStandardDeviation(null, null);

		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> item : map.entrySet()) {
			sb.append(item.getKey()).append("	").append(item.getValue())
					.append("\n");
		}
		return sb.toString();
	}

	@RequestMapping(value = { "/Gaussian" }, method = { RequestMethod.GET }, produces = "application/json")
	public @ResponseBody
	List<FrecuencyGaussianDto> getGaussian() {

		List<FrecuencyGaussianDto> lst = new ArrayList<FrecuencyGaussianDto>();
		lst.add(new FrecuencyGaussianDto(5, 2, 0));
		lst.add(new FrecuencyGaussianDto(1, 6, 1));

		return lst;
	}

	@RequestMapping(value = { "/ListServices" }, 
			params = {"frecuencia"}, 
			method = { RequestMethod.GET }, produces = "application/json")
	public @ResponseBody
	List<InfoServicesDTO> getListServices(@RequestParam(value = "frecuencia" , defaultValue="diario") String frecuencia) {
		List<InfoServicesDTO> result = repository.getListService(25,frecuencia);
		return result;
	}

	@RequestMapping(value = { "/ListaLlamadasAgrupadasPorTiempo" }, 
			params = {"ancho", "serviceName"}, 
			method = { RequestMethod.GET }, 
			produces = "application/json")
//			produces = "Text/plain")
	public @ResponseBody
	ResultDTO getListaByGroupTimeElapsed(
			@RequestParam(value = "ancho" , defaultValue="1") String ancho,
			@RequestParam(value = "serviceName") String serviceName,
			@RequestParam(value = "timeOut") String timeOut,
			@RequestParam(value = "frecuenciaTiempo") String frecuenciaTiempo
			) {
		List<List> result = repository.getListaByGroupTimeElapsed(ancho,
				serviceName,timeOut);
		
	

		String[] dataHead= {"Agrupado por "+ancho,"Nº de ejecuciones"};
		result.add(0 ,  Arrays.asList(dataHead));
		
		ResultDTO dtoREsult= new ResultDTO("Intervalos de milisegundos", "Nº de ejecucionies",result);
		
		return dtoREsult;
	}

}
