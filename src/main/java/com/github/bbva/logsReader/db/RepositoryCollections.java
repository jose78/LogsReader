package com.github.bbva.logsReader.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.github.bbva.logsReader.dto.ErrorNowDTO;
import com.github.bbva.logsReader.dto.InfoServicesDTO;
import com.github.bbva.logsReader.dto.TuplaDto;

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
	@Qualifier("crudBean")
	private DBConnection connection;


	public java.lang.Object[][] getDonutServiceApplication(String application,
			String service) {

		Object[] params = { application, service };
		String sql = "SELECT COUNT(*) AS NUMBER , 'HTTP Status:'||RESPONSECODE FROM CONTAINER_LOGS_DATA.BASIC_LOGS  WHERE APPLICATION = ? AND lABEL = ?  GROUP  BY  RESPONSECODE ORDER BY RESPONSECODE ";
		RowMapper<Object[][]> row = new RowMapper<Object[][]>() {

			@Override
			public Object[][] mapRow(ResultSet rs, int arg1)
					throws SQLException {
				List<TuplaDto> result = new ArrayList<TuplaDto>();
				do {
					TuplaDto tupla = new TuplaDto(rs.getString(2), rs.getInt(1));
					result.add(tupla);
				} while (rs.next());

				Object[][] matrix = new Object[result.size() + 1][2];
				int row = 1;
				for (TuplaDto tuplaDto : result) {
					matrix[row][0] = tuplaDto.getY();
					matrix[row][1] = tuplaDto.getX();
					row++;

				}
				matrix[0][0] = "Http Status";
				matrix[0][1] = "Nº de veces que se han producido.";
				return matrix;
			}
		};

		Object[][] result = connection.read(row, Object[][].class, sql, params)
				.get(0);
		return result;
	}

	/**
	 * Retrieve the list of calleds gruped by
	 * 
	 * @param serviceName
	 * @param application
	 * @return
	 */
	public List<List> getListaByGroupTimeElapsed(String ancho,
			String serviceName, String timeOut, String application) {

		String sql = String
				.format(""
						+ "SELECT  MAX (elapsed)  as maxi, "
						+ "(CASE WHEN (char_length(substring (''||elapsed from 1 for  %s)) < char_length(''||elapsed)) THEN "
						+ "float4(substring (''||elapsed from 1 for  %s)||repeat('0',char_length (''||elapsed)-%s))|| '-'||  "
						+ "float4(substring (''||elapsed from 1 for  %s)||repeat('9',char_length (''||elapsed)-%s))"
						+ " ELSE ''||elapsed END )as X , "
						+ "count(*) AS Y "
						// + " , 25 as timeOut"
						+ " from CONTAINER_LOGS_DATA.BASIC_LOGS WHERE label = ?  AND application = ? group by X ORDER BY maxi ",
						ancho, ancho, ancho, ancho, ancho, timeOut);

		List<List> lst = connection.read(new RowMapper<List>() {

			@Override
			public List mapRow(ResultSet rs, int arg1) throws SQLException {
				List<Object> lst = new ArrayList<Object>();
				lst.add(rs.getObject("x"));
				lst.add(rs.getObject("y"));
				// lst.add(rs.getObject("timeOut"));
				return lst;
			}
		}, List.class, sql, serviceName, application);

		return lst;
	}


	/**
	 * Retrieve a list with the name of all services executed.
	 * @param fecha 
	 * @param percentil 
	 * 
	 * @return List with the name of services.
	 */
	public List<InfoServicesDTO> getListEstadisticas(String fecha, int percentil) {

		
		String sqlP = "(select float4(max((case when rownum*1.0/numrows <= %s then elapsed end) ))/1000 as percentile "
						+ "from (select elapsed, "
						+ "             row_number() over (order by elapsed) as rownum, "
						+ "             count(*) over (partition by NULL) as numrows "
						+ "       FROM COnTAINER_LOGS_DATA.BASIC_LOGS"
						+ "       WHERE label = sub.label  AND sub.application = sub.application"
						+ "     ) t) AS %s ";
		
		
		String sqlFecha = "";
		if(fecha != null) 
			sqlFecha = String.format(
				"WHERE '%s' = to_char(timestamp, 'yyyy-mm-DD') AND (RESPONSECODE  LIKE '2%' OR RESPONSECODE LIKE '3%') ", fecha);
		
		
		StringBuilder sql = new StringBuilder();
		sql.append("  SELECT sub.label AS nameService , sub.application, ROUND(sub.F_AVG) AS F_AVG, ROUND(sub.standaDesviation)  AS standaDesviation,  float4(sub.F_MAX)/1000 AS f_max,float4(F_MIN)/1000 AS f_min,");
		sql.append("  ROUND(");
		sql.append("  100.0 * (");
		sql.append("  SUM(CASE WHEN (logs.elapsed <= sub.F_MAX AND logs.elapsed >=F_MIN) THEN 1 ELSE 0 END)");
		sql.append("  ) /COUNT(*), 1) AS percent_total,");
		sql.append("  ROUND(");
		sql.append("    100.0 * (");
		sql.append("        SUM(CASE WHEN ( logs.elapsed <F_MIN) THEN 1 ELSE 0 END)");
		sql.append("    ) /COUNT(*), 1) AS percent_down,");
		sql.append("  ROUND(");
		sql.append("    100.0 * (");
		sql.append("        SUM(CASE WHEN ( logs.elapsed >F_MAX) THEN 1 ELSE 0 END)");
		sql.append("    ) /COUNT(*), 1) AS percent_up , ");
		sql.append(String.format(sqlP,  (Double.valueOf(percentil +"")/100) , "PERCENTILE")).append(" , ");
		sql.append(String.format(sqlP, 0.25, "Q1")).append(" , ");
		sql.append(String.format(sqlP, 0.50, "Q2")).append(" , ");
		sql.append(String.format(sqlP, 0.75, "Q3"));
		sql.append("  FROM (");
		sql.append("  SELECT * , (main.F_AVG - main.standaDesviation ) AS I_MIN   ,  (main.F_AVG + main.standaDesviation ) AS I_MAX");
		sql.append("  FROM  (");
		sql.append("  SELECT LABEL ,  application, COUNT (*) AS TOTAL,");
		sql.append("  avg(elapsed) AS F_AVG , ");
		sql.append("  stddev_pop(elapsed) AS standaDesviation , MAX (elapsed) AS F_MAX , MIN (elapsed) AS F_MIN ");
		sql.append("  FROM CONTAINER_LOGS_DATA.BASIC_LOGS aa  ");
		sql.append(sqlFecha);
		sql.append("  GROUP by LABEL , application ) main ");
		sql.append("  ) sub, CONTAINER_LOGS_DATA.BASIC_LOGS  logs ");
		sql.append(sqlFecha);
		sql.append("  GROUP BY sub.label , sub.application, sub.F_AVG ,sub.standaDesviation ,  sub.F_MAX , F_MIN ORDER BY percent_up DESC");

		log.info("SQL: " + sql.toString());
		List<InfoServicesDTO> result = connection.read(InfoServicesDTO.class,
				sql.toString());
		return result;

	}

	/**
	 * Retrieve a list with the name of all services executed.
	 * 
	 * @return List with the name of services.
	 */
	public List<InfoServicesDTO> getListService(Integer timeOut,
			String frecuencia, String fecha, String... params) {

		String append = "";
		if ("diario".equals(frecuencia))
			append = String.format(
					"WHERE '%s' = to_char(timestamp, 'yyyy-mm-DD')", fecha);

		String sql = String
				.format("SELECT "
						+ " ROUND(100.0 * (SUM(CASE WHEN ( elapsed < %s) THEN 1 ELSE 0 END)    ) /COUNT(*), 1) AS percent_down ,"
						+ " ROUND(100.0 * (SUM(CASE WHEN ( elapsed >= %s) THEN 1 ELSE 0 END)    ) /COUNT(*), 1) AS percent_up, "
						+ " label AS nameService , application , "
						+ " float4(AVG (elapsed))/1000 AS f_avg"
						+ " FROM CONTAINER_LOGS_DATA.BASIC_LOGS %s  GROUP  BY application , LABEL ORDER BY LABEL",
						timeOut, timeOut, append);

		log.info("SQL: " + sql.toString());
		List<InfoServicesDTO> result = connection.read(InfoServicesDTO.class,
				sql.toString());
		return result;
	}

	public List<ErrorNowDTO> getErrorNowTable(Integer ancho, Integer minutos) {

		String sql = String
				.format(" SELECT application , label , COUNT(*) AS NUM_ERR FROM  CONTAINER_LOGS_DATA.BASIC_LOGS WHERE "
						+ " substring(RESPONSECODE FROM 1 FOR 1) != '2' AND substring(RESPONSECODE FROM 1 FOR 1) != '3' AND timestamp > current_timestamp - interval  '%s minutes' GROUP BY application , label ORDER BY NUM_ERR DESC LIMIT %s",
						minutos, ancho);

		List<ErrorNowDTO> result = connection.read(ErrorNowDTO.class, sql);

		return result;

	}

	public java.lang.Object[][] getErrorNowGraphic(String ancho,
			String application) {

		String sqlREsponse = " RESPONSECODE NOT LIKE '2%' AND  RESPONSECODE NOT LIKE '3%' ";

		String sql = String
				.format(  "(SELECT COUNT(*) AS F_COUNT , label , 'min_1m' AS KEY "
						+ " FROM  CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE %s AND application = ? AND a.timestamp > current_timestamp - interval  '1 minutes' GROUP BY LABEL  order by F_COUNT DESC limit %s )"
						+ " UNION "
						+ "(SELECT COUNT(*) AS F_COUNT , label , 'min_10m' AS KEY"
						+ " FROM  CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE %s AND application = ? AND a.timestamp > current_timestamp - interval  '10 minutes' GROUP BY LABEL  order by F_COUNT DESC limit %s)"
						+ " UNION "
						+ "(SELECT COUNT(*) AS F_COUNT , label , 'min_2h' AS KEY "
						+ " FROM  CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE %s AND application = ? AND a.timestamp > current_timestamp - interval  '125 minutes' GROUP BY LABEL  order by F_COUNT DESC limit %s)"
						+ " UNION "
						+ "(SELECT COUNT(*) AS F_COUNT , label , 'min_24h' AS KEY "
						+ " FROM  CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE %s AND application = ? AND a.timestamp > current_timestamp - interval  '1440 minutes' GROUP BY LABEL  order by F_COUNT DESC limit %s)"
		,sqlREsponse, ancho,sqlREsponse, ancho,sqlREsponse, ancho,sqlREsponse, ancho
		);


		RowMapper<Object[][]> maper = new RowMapper<Object[][]>() {

			String[] keys = { "min_1m", "min_10m", "min_2h", "min_24h" };
			Set<String> heads = new TreeSet<String>();
			Map<String, Map<String, Integer>> result = new TreeMap<String, Map<String, Integer>>();
			Object[][] matrix = null;

			@Override
			public Object[][] mapRow(ResultSet rs, int arg1)
					throws SQLException {

				for (String key : keys) {
					result.put(key, new TreeMap<String, Integer>());
				}

				do {
					Integer f_count = rs.getInt("f_count");
					String label = rs.getString("label");
					String key = rs.getString("key");

					result.get(key).put(label, f_count);
					heads.add(label);
				} while (rs.next());

				matrix = new Object[result.size() + 1][heads.size() + 1];
				String[] arrayHeads = heads.toArray(new String[heads.size()]);

				/*
				 * Fila 0
				 */
				for (int index_i = 1; index_i <= arrayHeads.length; index_i++) {
					String head = arrayHeads[index_i - 1];
					matrix[0][index_i] = head;
				}
				/*
				 * Columna 0
				 */
				for (int index_j = 0; index_j <= keys.length; index_j++) {

					String item = "";
					if (index_j == 0) {
						item = "Frecuencia.";
					} else {
						item = keys[index_j - 1];
					}
					matrix[index_j][0] = item;

				}

				/*
				 * Insertamos el cuerpo.
				 */
				for (int index_j = 1; index_j <= keys.length; index_j++) {
					String root = keys[index_j - 1];
					for (int index_i = 1; index_i <= arrayHeads.length; index_i++) {
						String head = arrayHeads[index_i - 1];
						Integer time = result.get(root).get(head);
						matrix[index_j][index_i] = time == null ? 0 : time;
					}
				}

				/*
				 * Matrix genreada
				 */
				return matrix;
			}
		};
		List<Object[][]> lstResult = connection.read(maper, Object[][].class,
				sql, application, application, application, application);

		if (lstResult.size() > 0)
			return lstResult.get(0);

		return null;

	}

	/**
	 * 
	 * @param limInf
	 * @param limSup
	 * @param serviceName
	 * @param application
	 * @return
	 */
	public List<Object[]> getListNumberOfAccesByHour(String limInf, String limSup,
			String serviceName, String application) {

		String sql = String.format("SELECT to_char(timestamp ,'HH24') AS Y , COUNT(*) AS X"
				+ " FROM CONTAINER_LOGS_DATA.BASIC_LOGS"
				+ " WHERE elapsed >=  %s AND elapsed <= %s AND application = ? AND label = ? GROUP BY y ORDER BY Y;" , limInf, limSup);
		
		RowMapper<Object[]> row= new RowMapper<Object[]>() {
			@Override
			public Object[] mapRow(ResultSet rs, int arg1) throws SQLException {
				Object[] result= {rs.getString(1) , rs.getInt(2)};
				return result;
			}
		};
		
		List<Object[]> result= connection.read(row , Object[].class, sql, application , serviceName);
		
		return result;
	}
}
