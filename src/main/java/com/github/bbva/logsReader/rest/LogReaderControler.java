package com.github.bbva.logsReader.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.bbva.logsReader.db.DBConnection;
import com.github.bbva.logsReader.db.RepositoryCollections;
import com.github.bbva.logsReader.dto.AppendFilesDto;
import com.github.bbva.logsReader.dto.ErrorNowDTO;
import com.github.bbva.logsReader.dto.InfoServicesDTO;
import com.github.bbva.logsReader.utils.Daemon;
import com.github.bbva.logsReader.utils.FilesUtils;
import com.github.bbva.logsReader.view.ColumnTableView;
import com.github.bbva.logsReader.view.ResultBarView;
import com.github.bbva.logsReader.view.ResultDonutView;
import com.github.bbva.logsReader.view.ResultTableView;
import com.github.bbva.logsReader.view.ResultView;
import com.github.bbva.logsReader.view.RowTableView;

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

	@Autowired
	private FilesUtils filesUtils;

	@RequestMapping("/Datoo")
	public String redirect() {
		System.out.println("\n\n\nDATO\n\n\n");
		return "index.html";
	}

	@Autowired
	private Daemon daemon;

	@RequestMapping(value = { "/Daemon" }, params = { "value" }, method = { RequestMethod.GET })
	public void setStartUpAndStopDaemon(
			@RequestParam(value = "value", defaultValue = "true") String flagStart) {
		daemon.setStartUpDaemon(new Boolean(flagStart));
	}

	@RequestMapping(value = { "/LoadFiles" }, params = { "directory", "files",
			"environment" }, method = { RequestMethod.GET })
	public void loadFiles(@RequestParam(value = "directory") String directory,
			@RequestParam(value = "files") String files,
			@RequestParam(value = "environment") String environment) {
		List<File> lst = new ArrayList<File>();

		String[] arrayNameFiles = files.split(";");
		List<File> lstFiles = new ArrayList<File>();
		for (String nameFile : arrayNameFiles) {
			lstFiles.add(new File(directory + "/" + nameFile));
		}

		File[] arrayFiles = lstFiles.toArray(new File[lstFiles.size()]);

		filesUtils.loadFiles(new AppendFilesDto(arrayFiles, environment));

	}

	// @RequestMapping(value = { "/StandardDeviation" }, method = {
	// RequestMethod.GET }, produces = "text/plain")
	// public @ResponseBody
	// String getStandardDeviation() {
	// Map<String, String> map = repository.getStandardDeviation(null, null);
	//
	// StringBuilder sb = new StringBuilder();
	// for (Entry<String, String> item : map.entrySet()) {
	// sb.append(item.getKey()).append("	").append(item.getValue())
	// .append("\n");
	// }
	// return sb.toString();
	// }

	// @RequestMapping(value = { "/Gaussian" }, method = { RequestMethod.GET },
	// produces = "application/json")
	// public @ResponseBody
	// List<FrecuencyGaussianDto> getGaussian() {
	//
	// List<FrecuencyGaussianDto> lst = new ArrayList<FrecuencyGaussianDto>();
	// lst.add(new FrecuencyGaussianDto(5, 2, 0));
	// lst.add(new FrecuencyGaussianDto(1, 6, 1));
	//
	// return lst;
	// }

	@RequestMapping(value = { "/ListServices" }, params = { "frecuencia",
			"fecha", "timeout" }, method = { RequestMethod.GET }, produces = "application/json")
	public @ResponseBody
	ResultTableView getListServices(
			@RequestParam(value = "frecuencia", defaultValue = "diario") String frecuencia,
			@RequestParam(value = "fecha") String fecha,
			@RequestParam(value = "timeout") String timeOut) {
		List<InfoServicesDTO> result = repository.getListService(
				Integer.parseInt(timeOut), frecuencia, fecha);

		List<ColumnTableView> lstCol = new ArrayList<ColumnTableView>();

		lstCol.add(new ColumnTableView("Servicio", "string"));
		lstCol.add(new ColumnTableView("Aplicación", "string"));
		lstCol.add(new ColumnTableView("Tiempo medio", "number"));
		lstCol.add(new ColumnTableView("% Por debajo del TimeOut", "number"));
		lstCol.add(new ColumnTableView("% Por encima del TimeOut", "number"));

		List<RowTableView> lst = new ArrayList<RowTableView>();

		for (InfoServicesDTO infoServicesDTO : result) {
			RowTableView row = new RowTableView();
			row.addCol(infoServicesDTO.getService(),
					infoServicesDTO.getApplication(),
					infoServicesDTO.getAggregate(),
					infoServicesDTO.getFailDown(), infoServicesDTO.getFailUp());

			lst.add(row);
		}

		ResultTableView r = new ResultTableView(lstCol, lst);

		return r;
	}

	// @RequestMapping(value = { "/ListServicesDonut" }, params = {
	// "service","application" }, method = { RequestMethod.GET }, produces =
	// "application/json")
	// public @ResponseBody
	// List<DonutDTO> getListServicesDonut(
	// @RequestParam(value = "service") String service,
	// @RequestParam(value = "application") String application ) {
	// List<DonutDTO> result =
	// repository.getDonutServiceApplication(application, service);
	// return result;
	// }

	@RequestMapping(value = { "/ErrorNowGr" }, params = { "ancho",
			"application" }, method = { RequestMethod.GET }, produces = "application/json")
	public @ResponseBody
	ResultBarView getErrorNowGr(
			@RequestParam(value = "ancho", defaultValue = "5") String ancho,
			@RequestParam(value = "application") String application) {
		Object[][] resultDB = repository.getErrorNowGraphic(ancho, application);

		ResultBarView result = new ResultBarView("Servicios.",
				"Nº de errores.", resultDB);

		return result;
	}

	@RequestMapping(value = { "/ErrorNowTbl" }, params = { "ancho", "minutos" }, method = { RequestMethod.GET }, produces = "application/json")
	public @ResponseBody
	ResultView getErrorNowTbl(
			@RequestParam(value = "ancho", defaultValue = "5") String ancho,
			@RequestParam(value = "minutos") String minutos) {
		List<ErrorNowDTO> resultDB = repository.getErrorNowTable(
				Integer.parseInt(ancho), Integer.parseInt(minutos));

		List<RowTableView> lstRow = new ArrayList<RowTableView>();

		if (resultDB.size() > 0) {
			for (ErrorNowDTO errorNowDTO : resultDB) {
				RowTableView rowView = new RowTableView();
				rowView.addCol(errorNowDTO.getApplication(),
						errorNowDTO.getLabel(), errorNowDTO.getNumberOfError());
				lstRow.add(rowView);
			}
		} else {
			for (int index = 0; index <= Integer.parseInt(ancho); index++) {
				RowTableView rowView = new RowTableView();
				rowView.addCol("-", "-", 0);
				lstRow.add(rowView);
			}
		}

		List<ColumnTableView> lstColumn = new ArrayList<ColumnTableView>();
		lstColumn.add(new ColumnTableView("Aplicación", "string"));
		lstColumn.add(new ColumnTableView("Servicio", "string"));
		lstColumn.add(new ColumnTableView("Nº de errores", "number"));

		ResultTableView tableView = new ResultTableView(lstColumn, lstRow);

		ResultView view = new ResultView();
		view.setTableView(tableView);
		return view;
	}

	@RequestMapping(value = { "/ListaLlamadasAgrupadasPorTiempo" }, params = {
			"ancho", "serviceName", "application", "timeOut" }, method = { RequestMethod.GET }, produces = "application/json")
	// produces = "Text/plain")
	public @ResponseBody
	ResultView getListaByGroupTimeElapsed(
			@RequestParam(value = "ancho", defaultValue = "1") String ancho,
			@RequestParam(value = "serviceName") String serviceName,
			@RequestParam(value = "timeOut") String timeOut,
			@RequestParam(value = "application") String application) {
		List<List> result = repository.getListaByGroupTimeElapsed(ancho,
				serviceName, timeOut, application);

		String[] dataHead = { "Agrupado por " + ancho, "Nº de ejecuciones" };
		result.add(0, Arrays.asList(dataHead));

		ResultBarView<List<List>> barView = new ResultBarView<List<List>>(
				"Intervalos de milisegundos", "Nº de ejecucionies", result);

		ResultDonutView donutView = new ResultDonutView(
				repository.getDonutServiceApplication(application, serviceName));

		ResultView resultView = new ResultView();
		resultView.setBarView(barView);
		resultView.setDonutView(donutView);

		return resultView;
	}

	@RequestMapping(value = { "/ListaLlamadasAgrupadasPorHora" }, params = {
			"serviceName", "application", "limInf", "limSup" }, method = { RequestMethod.GET }, produces = "application/json")
	// produces = "Text/plain")
	public @ResponseBody
	List<Object[]> getListNumberOfAccesByHour(
			@RequestParam(value = "limInf") String limInf,
			@RequestParam(value = "limSup") String limSup,
			@RequestParam(value = "serviceName") String serviceName,
			@RequestParam(value = "application") String application) {
		List<Object[]> result = repository.getListNumberOfAccesByHour(limInf,
				limSup, serviceName, application);

		String[] params = { "x_", "Nº de llamdas." };
		result.add(0, params);
		return result;
	}

	@RequestMapping(value = { "/getEstadistica" }, params = { "percentil",
			"fecha" }, method = { RequestMethod.GET }, produces = "application/json")
	// produces = "Text/plain")
	public @ResponseBody
	ResultView geEstadistica(
			@RequestParam(value = "fecha", required = false) String fecha,
			@RequestParam(value = "percentil") String percentil) {

		if (fecha != null && fecha.length() == 0)
			fecha = null;
		List<InfoServicesDTO> result = repository.getListEstadisticas(fecha,
				Integer.parseInt(percentil));

		// for (InfoServicesDTO infoServicesDTO : result) {
		// infoServicesDTO.setMedianColum(repository.getMediano(infoServicesDTO.getApplication(),
		// infoServicesDTO.getService(),fecha)+"");
		// }

		List<RowTableView> lstRow = new ArrayList<RowTableView>();

		Object[] data = null;
		for (InfoServicesDTO infoServicesDTO : result) {

			RowTableView rowView = new RowTableView();

			rowView.addCol(
					infoServicesDTO.getApplication(),
					infoServicesDTO.getService(),
					array(infoServicesDTO.getAggregate(), "ms"),
					infoServicesDTO.getStandaDesviation(),
					array(infoServicesDTO.getOk(), "%"),
					array(infoServicesDTO.getFailDown(), "%"),
					array(infoServicesDTO.getFailUp(), "%")

					,
					array(infoServicesDTO.getQ1(), "ms"),
					array(infoServicesDTO.getMedianColum(), "ms"),
					array(infoServicesDTO.getQ3(), "ms"),
					array(Integer.parseInt(infoServicesDTO.getPercentile()),
							"ms"), infoServicesDTO.getMin(), infoServicesDTO
							.getMax());
			lstRow.add(rowView);

		}

		List<ColumnTableView> lstCol = new ArrayList<ColumnTableView>();
		lstCol.add(new ColumnTableView("Application", "string"));
		lstCol.add(new ColumnTableView("Service", "string"));

		lstCol.add(new ColumnTableView("Agrgate", "number"));
		lstCol.add(new ColumnTableView("Standar Desviation", "number"));
		lstCol.add(new ColumnTableView("% OK", "number"));
		lstCol.add(new ColumnTableView("% Down OK", "number"));
		lstCol.add(new ColumnTableView("% Up OK", "number"));

		ResultView view = new ResultView();
		view.setTableView(new ResultTableView(lstCol, lstRow));

		return view;
	}

	private Object[] array(Object... objects) {
		return objects;
	}
}
