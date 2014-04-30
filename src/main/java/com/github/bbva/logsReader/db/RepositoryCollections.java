package com.github.bbva.logsReader.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.github.bbva.logsReader.utils.LoadData;
import com.github.bbva.logsReader.utils.LogsReaderException;
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
	public Map<String, String> getStandardDeviation(
			String inicioFechaIntervalo, String finFechaIntervalo) {

		
		String param = (inicioFechaIntervalo == null || finFechaIntervalo == null)?
				"":"WHERE timestamp BETWEEN TO_DATE(? , 'DD/MM/YYYY') AND TO_DATE(? , 'DD/MM/YYYY')";
		String sql = String.format("SELECT  "
				+ "           (to_char(timestamp ,'DD/MM/YYYY')) AS DATE_EXECUTION , "
				+ "           (stddev_pop(elapsed)) AS TIME_RESPONSE  "
				+ " FROM CONTAINER_LOGS_DATA.BASIC_LOGS "
				+ " %s "
				+ " GROUP BY DATE_EXECUTION",param);

		List<Map> result = connection.read(Map.class, sql,
				new LoadData<Map>() {

					@Override
					public Map load(ResultSet rs) throws SQLException {

						Map<String,String> tsv= new TreeMap<String, String>(new Comparator<String>() {

							@Override
							public int compare(String o1, String o2) {
								SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
								try {
									return sdf.parse(o1).compareTo(sdf.parse(o2));
								} catch (ParseException e) {
									e.printStackTrace();
									throw new LogsReaderException(e);
								}
							}
						});
						
						do {
							String x = rs.getString("DATE_EXECUTION");
							String y = rs.getString("TIME_RESPONSE");
							tsv.put(x, y);
						} while (rs.next());
						
						return tsv;
					}
				}
//		, inicioFechaIntervalo, finFechaIntervalo
		);

		return result.get(0);
	}

}
