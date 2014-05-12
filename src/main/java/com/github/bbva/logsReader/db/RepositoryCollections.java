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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.github.bbva.logsReader.dto.InfoServicesDTO;
import com.github.bbva.logsReader.utils.LoadData;
import com.github.bbva.logsReader.utils.LogsReaderException;

/**
 * 
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
@Repository
public class RepositoryCollections {

	private static Logger log = Logger.getLogger(RepositoryCollections.class);

	@Autowired
	DBConnection connection;

	/**
	 * 
	 * @param agrupador
	 * @param medida
	 * @param inicioFechaIntervalo
	 *            Fecha inicio del intervalo con el formato 'DD/MM/YYYY' param
	 *            finFechaIntervalo Fecha fin del intervalo con el formato
	 *            'DD/MM/YYYY'
	 * @return
	 */
	public Map<String, String> getStandardDeviation(
			String inicioFechaIntervalo, String finFechaIntervalo) {

		String param = (inicioFechaIntervalo == null || finFechaIntervalo == null) ? ""
				: "WHERE timestamp BETWEEN TO_DATE(? , 'DD/MM/YYYY') AND TO_DATE(? , 'DD/MM/YYYY')";
		String sql = String
				.format("SELECT  "
						+ "           (to_char(timestamp ,'DD/MM/YYYY')) AS DATE_EXECUTION , "
						+ "           (stddev_pop(elapsed)) AS TIME_RESPONSE  "
						+ " FROM CONTAINER_LOGS_DATA.BASIC_LOGS " + " %s "
						+ " GROUP BY DATE_EXECUTION", param);

		List<Map> result = connection.read(Map.class, sql, new LoadData<Map>() {

			@Override
			public Map load(ResultSet rs) throws SQLException {

				Map<String, String> tsv = new TreeMap<String, String>(
						new Comparator<String>() {

							@Override
							public int compare(String o1, String o2) {
								SimpleDateFormat sdf = new SimpleDateFormat(
										"dd/MM/yyyy");
								try {
									return sdf.parse(o1).compareTo(
											sdf.parse(o2));
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
		// , inicioFechaIntervalo, finFechaIntervalo
				);

		return result.get(0);
	}

	/**
	 * Retrieve the list of calleds gruped by
	 * 
	 * @param serviceName
	 * @return
	 */
	public List<List> getListaByGroupTimeElapsed(String ancho,
			String serviceName, String timeOut) {
		String sql = String
				.format(""
						+ "SELECT  MAX (elapsed)  as maxi, "
						+ "substring (''||elapsed from 1 for  %s)||repeat('0',char_length (''||elapsed)-1)|| '-'||  "
						+ "substring (''||elapsed from 1 for  %s)||repeat('9',char_length (''||elapsed)-1)  as X  , "
						+ "count(*) AS Y "
//						+ " , 25 as timeOut"
						+ " from CONTAINER_LOGS_DATA.BASIC_LOGS WHERE label = ?  group by X ORDER BY maxi ",
						ancho,ancho,timeOut);

	
		List<List> lst = connection.read(new RowMapper<List>() {

			@Override
			public List mapRow(ResultSet rs, int arg1) throws SQLException {
				List<Object> lst = new ArrayList<Object>();
				lst.add(rs.getObject("x"));
				lst.add(rs.getObject("y"));
//				lst.add(rs.getObject("timeOut"));
				return lst;
			}
		}, List.class, sql, serviceName);

		return lst;
	}

//	/**
//	 * Retrieve a list with the name of all services executed.
//	 * 
//	 * @return List with the name of services.
//	 */
//	public List<InfoServicesDTO> getListService(String... params) {
//
//		String paramWhere = "";
//		if (params.length > 0)
//			paramWhere = String.format("AND LABEL = '%s' ", params[0]);
//
//		StringBuilder sql = new StringBuilder();
//		sql.append("  SELECT  sub.label, ROUND(sub.F_AVG) AS F_AVG, ROUND(sub.standaDesviation)  AS standaDesviation,  sub.F_MAX,F_MIN,");
//		sql.append("  ROUND(");
//		sql.append("  100.0 * (");
//		sql.append("  SUM(CASE WHEN (logs.elapsed <= sub.F_MAX AND logs.elapsed >=F_MIN) THEN 1 ELSE 0 END)");
//		sql.append("  ) /COUNT(*), 1) AS percent_total,");
//		sql.append("  ROUND(");
//		sql.append("    100.0 * (");
//		sql.append("        SUM(CASE WHEN ( logs.elapsed <F_MIN) THEN 1 ELSE 0 END)");
//		sql.append("    ) /COUNT(*), 1) AS percent_down,");
//		sql.append("  ROUND(");
//		sql.append("    100.0 * (");
//		sql.append("        SUM(CASE WHEN ( logs.elapsed >F_MAX) THEN 1 ELSE 0 END)");
//		sql.append("    ) /COUNT(*), 1) AS percent_up");
//		sql.append("  FROM (");
//		sql.append("  SELECT * , (main.F_AVG - main.standaDesviation ) AS I_MIN   ,  (main.F_AVG + main.standaDesviation ) AS I_MAX");
//		sql.append("  FROM  (");
//		sql.append("  SELECT LABEL , COUNT (*) AS TOTAL,");
//		sql.append("  avg(elapsed) AS F_AVG , ");
//		sql.append("  stddev_pop(elapsed) AS standaDesviation , MAX (elapsed) AS F_MAX , MIN (elapsed) AS F_MIN ");
//		sql.append("  FROM CONTAINER_LOGS_DATA.BASIC_LOGS ");
//		sql.append("  WHERE responsecode = '200' ");
//		sql.append(paramWhere);
//		sql.append("  GROUP by LABEL ) main ");
//		sql.append("  ) sub, CONTAINER_LOGS_DATA.BASIC_LOGS  logs WHERE logs.responsecode = '200' GROUP BY sub.label , sub.F_AVG ,sub.standaDesviation ,  sub.F_MAX , F_MIN ORDER BY percent_up DESC");
//
//		log.info("SQL: " + sql.toString());
//		List<InfoServicesDTO> result = connection.read(InfoServicesDTO.class,
//				sql.toString());
//		return result;
//	}
//}
	
	
	/**
	 * Retrieve a list with the name of all services executed.
	 * 
	 * @return List with the name of services.
	 */
	public List<InfoServicesDTO> getListService(Integer timeOut, String frecuencia, String... params) {

		String paramWhere = "";
		if (params.length > 0)
			paramWhere = String.format("AND LABEL = '%s' ", params[0]);

		
		String append = "";
		if("diario".equals(frecuencia))
			append= "WHERE current_date = timestamp::date";
		
		
		String sql = String.format("SELECT "
				+ " ROUND(100.0 * (SUM(CASE WHEN ( elapsed < %s) THEN 1 ELSE 0 END)    ) /COUNT(*), 1) AS percent_down ,"
				+ " ROUND(100.0 * (SUM(CASE WHEN ( elapsed >= %s) THEN 1 ELSE 0 END)    ) /COUNT(*), 1) AS percent_up, "
				+ " label AS nameService , "
				+ " AVG (elapsed) AS f_avg"
				+ " FROM CONTAINER_LOGS_DATA.BASIC_LOGS %s  GROUP "
				+ " BY LABEL",timeOut,timeOut,append);


		log.info("SQL: " + sql.toString());
		List<InfoServicesDTO> result = connection.read(InfoServicesDTO.class,
				sql.toString());
		return result;
	}
}
