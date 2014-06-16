package com.github.bbva.logsReader.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.bbva.logsReader.db.RepositoryCollections;
import com.github.bbva.logsReader.dto.ErrorNowDTO;
import com.github.bbva.logsReader.dto.InfoServicesDTO;
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
	private RepositoryCollections repository;

	@RequestMapping(value = { "/" })
	public void setStartUp(HttpServletResponse response) throws IOException {
		response.sendRedirect("index.html");
	}

	@RequestMapping(value = { "/ListServices" }, params = { "frecuencia",
			"fecha", "timeout" }, method = { RequestMethod.GET }, produces = "application/json")
	public @ResponseBody ResultTableView getListServices(
			@RequestParam(value = "frecuencia", defaultValue = "diario") String frecuencia,
			@RequestParam(value = "fecha") String fecha,
			@RequestParam(value = "timeout") String timeOut) {
		List<InfoServicesDTO> result = repository.getListService(
				Integer.parseInt(timeOut), frecuencia, fecha);

		List<ColumnTableView> lstCol = new ArrayList<ColumnTableView>();

		lstCol.add(new ColumnTableView("Service", "string"));
		lstCol.add(new ColumnTableView("Application", "string"));
		lstCol.add(new ColumnTableView("Average time (seg)", "number"));
		lstCol.add(new ColumnTableView("% Under the TimeOut", "number"));
		lstCol.add(new ColumnTableView("% Over the TimeOut", "number"));

		List<RowTableView> lst = new ArrayList<RowTableView>();

		for (InfoServicesDTO infoServicesDTO : result) {
			RowTableView row = new RowTableView();
			row.addCol(infoServicesDTO.getService(),
					infoServicesDTO.getApplication(),
					resizeNumber(infoServicesDTO.getAggregate()),
					infoServicesDTO.getFailDown(), 
					infoServicesDTO.getFailUp());

			lst.add(row);
		}

		ResultTableView r = new ResultTableView(lstCol, lst);

		return r;
	}

	@RequestMapping(value = { "/ErrorNowGr" }, params = { "ancho",
			"application" }, method = { RequestMethod.GET }, produces = "application/json")
	public @ResponseBody ResultBarView<?> getErrorNowGr(
			@RequestParam(value = "ancho", defaultValue = "5") String ancho,
			@RequestParam(value = "application") String application) {
		Object[][] resultDB = repository.getErrorNowGraphic(ancho, application);

		ResultBarView<Object[][]> result = new ResultBarView<Object[][]>("Servicios.",
				"Nº de errores.", resultDB);

		return result;
	}

	@RequestMapping(value = { "/ErrorNowTbl" }, params = { "ancho", "minutos" }, method = { RequestMethod.GET }, produces = "application/json")
	public @ResponseBody ResultView getErrorNowTbl(
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
		}
		int max = Math.min(Integer.parseInt(ancho), Integer.parseInt(ancho) - resultDB.size() );
		for (int index = 0; index < max; index++) {
			RowTableView rowView = new RowTableView();
			rowView.addCol("-", "-", 0);
			lstRow.add(rowView);
		}
		

		List<ColumnTableView> lstColumn = new ArrayList<ColumnTableView>();
		lstColumn.add(new ColumnTableView("Application", "string"));
		lstColumn.add(new ColumnTableView("Service", "string"));
		lstColumn.add(new ColumnTableView("Num. of errors", "number"));

		ResultTableView tableView = new ResultTableView(lstColumn, lstRow);

		ResultView view = new ResultView();
		view.setTableView(tableView);
		return view;
	}

	@RequestMapping(value = { "/ListaLlamadasAgrupadasPorTiempo" }, params = {
			"ancho", "serviceName", "application", "timeOut" }, method = { RequestMethod.GET }, produces = "application/json")
	// produces = "Text/plain")
	public @ResponseBody ResultView getListaByGroupTimeElapsed(
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
	public @ResponseBody List<Object[]> getListNumberOfAccesByHour(
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
	public @ResponseBody ResultView geEstadistica(
			@RequestParam(value = "fecha", required = false) String fecha,
			@RequestParam(value = "percentil") String percentil) {

		if (fecha != null && fecha.length() == 0)
			fecha = null;
		List<InfoServicesDTO> result = repository.getListEstadisticas(fecha,
				Integer.parseInt(percentil));

		List<RowTableView> lstRow = new ArrayList<RowTableView>();

		for (InfoServicesDTO infoServicesDTO : result) {

			RowTableView rowView = new RowTableView();

			rowView.addCol(
					infoServicesDTO.getApplication(),
					infoServicesDTO.getService(),
					resizeNumber(infoServicesDTO.getAggregate()),
					infoServicesDTO.getStandaDesviation(),
					array(infoServicesDTO.getFailUp(), "%")	,
					resizeNumber(infoServicesDTO.getQ1()),
					resizeNumber(infoServicesDTO.getMedianColum()),
					resizeNumber(infoServicesDTO.getQ3()),
					resizeNumber(infoServicesDTO.getPercentile()),
					resizeNumber(infoServicesDTO.getMax()),
					resizeNumber(infoServicesDTO.getMin())
					);
			lstRow.add(rowView);

		}

		List<ColumnTableView> lstCol = new ArrayList<ColumnTableView>();
		lstCol.add(new ColumnTableView("Application", "string"));
		lstCol.add(new ColumnTableView("Service", "string"));
		lstCol.add(new ColumnTableView("Average time (seg)", "number"));
		lstCol.add(new ColumnTableView("Standard Desviation", "number"));
		lstCol.add(new ColumnTableView("% Over the range", "number"));
		lstCol.add(new ColumnTableView("Q1 (seg)", "number"));
		lstCol.add(new ColumnTableView("Q2 (seg)", "number"));
		lstCol.add(new ColumnTableView("Q3 (seg)", "number"));
		lstCol.add(new ColumnTableView("Percent "+percentil+" (seg)", "number"));
		lstCol.add(new ColumnTableView("Max (seg)", "number"));
		lstCol.add(new ColumnTableView("Min (seg)", "number"));

		ResultView view = new ResultView();
		view.setTableView(new ResultTableView(lstCol, lstRow));

		return view;
	}
	
	
	private Double resizeNumber(Double number){
		int decimalPlaces = 2;
		BigDecimal bd = new BigDecimal(number);

		// setScale is immutable
		bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	private Object[] array(Object... objects) {
		return objects;
	}
}
