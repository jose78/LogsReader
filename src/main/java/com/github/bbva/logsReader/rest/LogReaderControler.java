package com.github.bbva.logsReader.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.github.bbva.logsReader.dto.DonutDTO;
import com.github.bbva.logsReader.dto.ErrorNowDTO;
import com.github.bbva.logsReader.dto.InfoServicesDTO;
import com.github.bbva.logsReader.dto.ResultDTO;
import com.github.bbva.logsReader.utils.Daemon;
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
	
	@Autowired
	private Daemon daemon;
	

	@RequestMapping(value = { "/Daemon" }, params = { "value"}, method = { RequestMethod.GET })
	public void  setStartUpAndStopDaemon(@RequestParam(value = "value", defaultValue = "true") String flagStart) {
		daemon.setStartUpDaemon(new Boolean(flagStart));
	}
	
	@RequestMapping(value = { "/LoadFiles" }, params = { "directory" , "files"}, method = { RequestMethod.GET })
	public void  loadFiles(@RequestParam(value = "directory", defaultValue = "true") String flagStart,
			@RequestParam(value = "files") String files) {
		List<File> lst= new ArrayList<File>();
		
		
		
		daemon.setStartUpDaemon(new Boolean(flagStart));
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

	@RequestMapping(value = { "/ListServices" }, params = { "frecuencia","fecha","timeout" }, method = { RequestMethod.GET }, produces = "application/json")
	public @ResponseBody
	List<InfoServicesDTO> getListServices(
			@RequestParam(value = "frecuencia", defaultValue = "diario") String frecuencia,
			@RequestParam(value = "fecha") String fecha , 
			@RequestParam(value = "timeout") String timeOut) {
		List<InfoServicesDTO> result = repository.getListService(Integer.parseInt(timeOut),
				frecuencia, fecha);
		return result;
	}
	
	
//	@RequestMapping(value = { "/ListServicesDonut" }, params = { "service","application" }, method = { RequestMethod.GET }, produces = "application/json")
//	public @ResponseBody
//	List<DonutDTO> getListServicesDonut(
//			@RequestParam(value = "service") String service,
//			@RequestParam(value = "application") String application ) {
//		List<DonutDTO> result = repository.getDonutServiceApplication(application, service);
//		return result;
//	}
	
	@RequestMapping(value = { "/ErrorNowGr" }, params = {
			"ancho", "application" },method = { RequestMethod.GET }, produces = "application/json")
	public @ResponseBody
	ResultDTO<Object[][]> getErrorNowGr(
			@RequestParam(value = "ancho", defaultValue = "5") String ancho,
		    @RequestParam(value = "application") String application ) {
		Object[][] resultDB = repository.getErrorNowGraphic(ancho,application);
		
		ResultDTO<Object[][]>  result= new ResultDTO<Object[][]>("Tiempos chequeados.","Nº de errores.",resultDB);
		
		return result;
	}
	
	@RequestMapping(value = { "/ErrorNowTbl" }, params = {
			"ancho", "minutos" },method = { RequestMethod.GET }, produces = "application/json")
	public @ResponseBody
	List<ErrorNowDTO> getErrorNowTbl(
			@RequestParam(value = "ancho", defaultValue = "5") String ancho,
		    @RequestParam(value = "minutos") String minutos ) {
		List<ErrorNowDTO> resultDB = repository.getErrorNowTable(Integer.parseInt(ancho), Integer.parseInt(minutos));

		
		return resultDB;
	}
	
	
	

	@RequestMapping(value = { "/ListaLlamadasAgrupadasPorTiempo" }, params = {
			"ancho", "serviceName","application", "timeOut" }, method = { RequestMethod.GET }, produces = "application/json")
	// produces = "Text/plain")
	public @ResponseBody
	ResultDTO getListaByGroupTimeElapsed(
			@RequestParam(value = "ancho", defaultValue = "1") String ancho,
			@RequestParam(value = "serviceName") String serviceName,
			@RequestParam(value = "timeOut") String timeOut,
			@RequestParam(value = "application") String application) {
		List<List> result = repository.getListaByGroupTimeElapsed(ancho,
				serviceName,timeOut, application );

		String[] dataHead = { "Agrupado por " + ancho, "Nº de ejecuciones" };
		result.add(0, Arrays.asList(dataHead));

		ResultDTO<List<List>> dtoREsult = new ResultDTO<List<List>>("Intervalos de milisegundos",
				"Nº de ejecucionies", result);

		dtoREsult.setListDataDonut(repository.getDonutServiceApplication(application, serviceName));
		return dtoREsult;
	}

	
	
	
	@RequestMapping(value = { "/getEstadistica" }, params={"frecuencia","fecha"},  method = { RequestMethod.GET }, produces = "application/json")
	// produces = "Text/plain")
	public @ResponseBody
	List<InfoServicesDTO> geEstadistica(@RequestParam(value = "frecuencia", defaultValue = "diario") String frecuencia,
			@RequestParam(value = "fecha" , required=false) String fecha ) { 
		
		if(fecha != null && fecha.length() == 0)
			fecha = null;
		List<InfoServicesDTO> result = repository.getListEstadisticas(fecha);
		
		for (InfoServicesDTO infoServicesDTO : result) {
			infoServicesDTO.setMedianColum(repository.getMediano(infoServicesDTO.getApplication(), infoServicesDTO.getService(),fecha)+"");
		}
		
		return result;
	}
}
