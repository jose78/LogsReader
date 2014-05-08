package com.github.bbva.logsReader.rest;

import java.util.ArrayList;
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

	@RequestMapping(value = { "/ListServices" }, method = { RequestMethod.GET }, produces = "application/json")
	public @ResponseBody
	List<InfoServicesDTO> getListServices() {
		List<InfoServicesDTO> result = repository.getListService();
		return result;
	}

	@RequestMapping(value = { "/ListaLlamadasAgrupadasPorTiempo" }, 
			params = {"ancho", "serviceName"}, 
			method = { RequestMethod.GET }, 
			produces = "application/json")
	public @ResponseBody
	List<TuplaDto> getListaByGroupTimeElapsed(
			@RequestParam(value = "ancho" , defaultValue="1") String ancho,
			@RequestParam(value = "serviceName") String serviceName) {
		List<TuplaDto> result = repository.getListaByGroupTimeElapsed(ancho,
				serviceName);
		
		
		return result;
	}

}
