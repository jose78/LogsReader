package com.github.bbva.logsReader.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.github.bbva.logsReader.utils.LoadData;
import com.github.bbva.logsReader.utils.ResultDto;
import com.github.bbva.logsReader.utils.TuplaDto;
import com.github.bbva.logsReader.utils.enums.AgrupadorEnum;
import com.github.bbva.logsReader.utils.enums.MediaEnum;

/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 * 
 */
@Repository
public class RepositoryCollections {

	@Autowired
	DBConnection connection;

	/**
	 * 
	 * @param agrupador
	 * @param medida
	 * @param inicioFechaIntervalo Fecha inicio del intervalo con el formato 'DD/MM/YYYY'
	 * param finFechaIntervalo Fecha fin del intervalo con el formato 'DD/MM/YYYY'
	 * @return
	 */
	public List<ResultDto> getStandardDeviation(AgrupadorEnum agrupador, MediaEnum medida,
			String inicioFechaIntervalo, String finFechaIntervalo) {

		String sql = "SELECT  "
				+ "           (extract(? FROM timestamp)) AS AGRUPADOR  ,  "
				+ "           (extract(? FROM timestamp)) AS MEDIDA , "
				+ "           (stddev_pop(elapsed)) AS TIME_RESPONSE  "
				+ " FROM CONTAINER_LOGS_DATA.BASIC_LOGS "
				+ " WHERE timestamp BETWEEN TO_DATE(? , 'DD/MM/YYYY') AND TO_DATE(? , 'DD/MM/YYYY')"
				+ " GROUP BY AGRUPADOR , MEDIDA";

		List<ResultDto> result = connection.read(ResultDto.class, sql,
				new LoadData<ResultDto>() {

					@Override
					public ResultDto load(ResultSet rs) throws SQLException {

						ResultDto resultDto = new ResultDto();

						do {
							String agrupador = rs.getString("AGRUPADOR");
							String x = rs.getString("MEDIDA");
							String y = rs.getString("TIME_RESPONSE");

							List<TuplaDto> lstTupla = null;
							if ((lstTupla = resultDto.getAgrupador().get(agrupador)) != null) {
								lstTupla.add(new TuplaDto(x, y));
							} else {
								lstTupla = new ArrayList<TuplaDto>();
								resultDto.getAgrupador().put(agrupador,	lstTupla);
							}
						} while (rs.next());

						return resultDto;
					}
				}, agrupador.name(), medida.name(), inicioFechaIntervalo, finFechaIntervalo);

		return result;
	}

}
