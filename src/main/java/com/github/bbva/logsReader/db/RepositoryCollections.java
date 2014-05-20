package com.github.bbva.logsReader.db;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.github.bbva.logsReader.dto.DonutDTO;
import com.github.bbva.logsReader.dto.ErrorNowDTO;
import com.github.bbva.logsReader.dto.InfoServicesDTO;
import com.github.bbva.logsReader.dto.ResultDTO;
import com.github.bbva.logsReader.dto.TuplaDto;
import com.github.bbva.logsReader.entity.FileEnvironmentEntity;
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

	protected static final Object[][] Object = null;

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

	public java.lang.Object[][] getDonutServiceApplication(String application, String service) {
	
		Object[] params= {application , service};
		String sql = "SELECT COUNT(*) AS NUMBER , RESPONSECODE FROM CONTAINER_LOGS_DATA.BASIC_LOGS  WHERE APPLICATION = ? AND lABEL = ?  GROUP  BY  RESPONSECODE ORDER BY RESPONSECODE ";
		RowMapper<Object[][]> row= new RowMapper<Object[][]>() {
			
			@Override
			public Object[][] mapRow(ResultSet rs, int arg1) throws SQLException {
				List<TuplaDto> result= new ArrayList<TuplaDto>();
				do{
					TuplaDto tupla = new TuplaDto(rs.getString(2), rs.getInt(1));
					result.add(tupla);					
				}while (rs.next()) ;
				
				Object[][] matrix= new Object[result.size()+1][2];
				int row= 1;
				for (TuplaDto tuplaDto : result) {
					matrix[row][0] = tuplaDto.getY();
					matrix[row][1] = tuplaDto.getX();
					row++;
					
				}
				matrix[0][0]= "Http Status";
				matrix[0][1]= "Nº de veces que se han producido.";
				return matrix;
			}
		};
		
		Object[][] result = connection.read(row , Object[][].class, sql, params).get(0);
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
						+ "substring (''||elapsed from 1 for  %s)||repeat('0',char_length (''||elapsed)-%s)|| '-'||  substring (''||elapsed from 1 for  %s)||repeat('9',char_length (''||elapsed)-%s)"
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
		}, List.class, sql, serviceName , application);

		return lst;
	}

	// /**
	// * Retrieve a list with the name of all services executed.
	// *
	// * @return List with the name of services.
	// */
	// public List<InfoServicesDTO> getListService(String... params) {
	//
	// String paramWhere = "";
	// if (params.length > 0)
	// paramWhere = String.format("AND LABEL = '%s' ", params[0]);
	//
	// StringBuilder sql = new StringBuilder();
	// sql.append("  SELECT  sub.label, ROUND(sub.F_AVG) AS F_AVG, ROUND(sub.standaDesviation)  AS standaDesviation,  sub.F_MAX,F_MIN,");
	// sql.append("  ROUND(");
	// sql.append("  100.0 * (");
	// sql.append("  SUM(CASE WHEN (logs.elapsed <= sub.F_MAX AND logs.elapsed >=F_MIN) THEN 1 ELSE 0 END)");
	// sql.append("  ) /COUNT(*), 1) AS percent_total,");
	// sql.append("  ROUND(");
	// sql.append("    100.0 * (");
	// sql.append("        SUM(CASE WHEN ( logs.elapsed <F_MIN) THEN 1 ELSE 0 END)");
	// sql.append("    ) /COUNT(*), 1) AS percent_down,");
	// sql.append("  ROUND(");
	// sql.append("    100.0 * (");
	// sql.append("        SUM(CASE WHEN ( logs.elapsed >F_MAX) THEN 1 ELSE 0 END)");
	// sql.append("    ) /COUNT(*), 1) AS percent_up");
	// sql.append("  FROM (");
	// sql.append("  SELECT * , (main.F_AVG - main.standaDesviation ) AS I_MIN   ,  (main.F_AVG + main.standaDesviation ) AS I_MAX");
	// sql.append("  FROM  (");
	// sql.append("  SELECT LABEL , COUNT (*) AS TOTAL,");
	// sql.append("  avg(elapsed) AS F_AVG , ");
	// sql.append("  stddev_pop(elapsed) AS standaDesviation , MAX (elapsed) AS F_MAX , MIN (elapsed) AS F_MIN ");
	// sql.append("  FROM CONTAINER_LOGS_DATA.BASIC_LOGS ");
	// sql.append("  WHERE responsecode = '200' ");
	// sql.append(paramWhere);
	// sql.append("  GROUP by LABEL ) main ");
	// sql.append("  ) sub, CONTAINER_LOGS_DATA.BASIC_LOGS  logs WHERE logs.responsecode = '200' GROUP BY sub.label , sub.F_AVG ,sub.standaDesviation ,  sub.F_MAX , F_MIN ORDER BY percent_up DESC");
	//
	// log.info("SQL: " + sql.toString());
	// List<InfoServicesDTO> result = connection.read(InfoServicesDTO.class,
	// sql.toString());
	// return result;
	// }
	// }

	/**
	 * Retrieve a list with the name of all services executed.
	 * 
	 * @return List with the name of services.
	 */
	public List<InfoServicesDTO> getListService(Integer timeOut,
			String frecuencia, String fecha, String... params) {

		String paramWhere = "";
		if (params.length > 0)
			paramWhere = String.format("AND LABEL = '%s' ", params[0]);

		String append = "";
		if ("diario".equals(frecuencia))
			append = String.format(
					"WHERE '%s' = to_char(timestamp, 'yyyy-mm-DD')", fecha);

		String sql = String
				.format("SELECT "
						+ " ROUND(100.0 * (SUM(CASE WHEN ( elapsed < %s) THEN 1 ELSE 0 END)    ) /COUNT(*), 1) AS percent_down ,"
						+ " ROUND(100.0 * (SUM(CASE WHEN ( elapsed >= %s) THEN 1 ELSE 0 END)    ) /COUNT(*), 1) AS percent_up, "
						+ " label AS nameService , application , "
						+ " AVG (elapsed) AS f_avg"
						+ " FROM CONTAINER_LOGS_DATA.BASIC_LOGS %s  GROUP  BY application , LABEL ORDER BY LABEL",
						timeOut, timeOut, append);

		log.info("SQL: " + sql.toString());
		List<InfoServicesDTO> result = connection.read(InfoServicesDTO.class,
				sql.toString());
		return result;
	}

	public List<ErrorNowDTO> getErrorNowTable(Integer ancho, Integer minutos) {

		//
		// String sql =
		// "(SELECT COUNT(*) AS F_COUNT , label AS service,  responseCode "
		// +
		// "FROM  CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE RESPONSECODE != '200' AND "
		// + "application = 'BUZZ' AND "
		// + "a.timestamp > current_timestamp - interval  '120 minutes' "
		// +
		// "GROUP BY LABEL  , responseCode HAVING COUNT(DISTINCT(LABEL)) <= 3   order by F_COUNT DESC ) ";

		String sql = String
				.format(" SELECT application , label , COUNT(*) AS NUM_ERR FROM  CONTAINER_LOGS_DATA.BASIC_LOGS WHERE responsecode != '200' AND timestamp > current_timestamp - interval  '%s minutes' GROUP BY application , label ORDER BY NUM_ERR DESC LIMIT %s",
						minutos, ancho);

		List<ErrorNowDTO> result = connection.read(ErrorNowDTO.class, sql);

		return result;

	}

	public java.lang.Object[][] getErrorNowGraphic(String ancho,
			String application) {

		Object[] params = { application, application, application, application };

		String sql = String
				.format("(SELECT COUNT(*) AS F_COUNT , label , 'min_1m' AS KEY                                                                                                                                                   "
						+ " FROM  CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE RESPONSECODE != '200' AND application = ? AND a.timestamp > current_timestamp - interval  '1 minutes' GROUP BY LABEL  order by F_COUNT DESC limit %s )"
						+ " UNION                                                 "
						+ " (SELECT COUNT(*) AS F_COUNT , label , 'min_10m' AS KEY"
						+ " FROM  CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE RESPONSECODE != '200' AND application = ? AND a.timestamp > current_timestamp - interval  '10 minutes' GROUP BY LABEL  order by F_COUNT DESC limit %s)"
						+ " UNION                                                 "
						+ " (SELECT COUNT(*) AS F_COUNT , label , 'min_1h' AS KEY "
						+ " FROM  CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE RESPONSECODE != '200' AND application = ? AND a.timestamp > current_timestamp - interval  '60 minutes' GROUP BY LABEL  order by F_COUNT DESC limit %s)"
						+ " UNION                                                  "
						+ " (SELECT COUNT(*) AS F_COUNT , label , 'min_24h' AS KEY "
						+ " FROM  CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE RESPONSECODE != '200' AND application = ? AND a.timestamp > current_timestamp - interval  '1440 minutes' GROUP BY LABEL  order by F_COUNT DESC limit %s)",
						ancho, ancho, ancho, ancho);

		// String sql = String
		// .format("SELECT t1.name as MIN_1, time||'' AS time_MIN_1,'' as MIN_10, '' AS time_MIN_10,'' as MIN_60, '' AS time_MIN_60,'' as MIN_24h ,'' AS time_MIN_24h FROM                        "
		// +
		// "( SELECT DISTINCT(label) as name , MAX(elapsed) as time FROM CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE a.timestamp > current_timestamp - interval      '100 minutes'  GROUP BY     "
		// +
		// "label  ORDER BY time DESC limit %s) t1                                                                                                                                         "
		// +
		// "UNION                                                                                                                                                                         "
		// +
		// "SELECT '' as MIN_1, '' AS time_MIN_1, name as MIN_10, time||'' AS time_MIN_10,'' as MIN_60, '' AS time_MIN_60,'' as MIN_24h ,'' AS time_MIN_24h FROM                          "
		// +
		// "( SELECT DISTINCT(label) as name , MAX(elapsed) as time FROM CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE a.timestamp > current_timestamp - interval      '300 minutes'  GROUP BY    "
		// +
		// "label  ORDER BY time DESC limit %s) t1                                                                                                                                         "
		// +
		// "UNION                                                                                                                                                                         "
		// +
		// "SELECT '' as MIN_1, '' AS time_MIN_1,'' as MIN_10, '' AS time_MIN_10, name as MIN_60, time||'' AS time_MIN_60,'' as MIN_24h ,'' AS time_MIN_24h FROM                          "
		// +
		// "( SELECT DISTINCT(label) as name , MAX(elapsed) as time FROM CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE a.timestamp > current_timestamp - interval      '600 minutes'  GROUP BY    "
		// +
		// "label  ORDER BY time DESC limit %s) t1                                                                                                                                         "
		// +
		// "UNION                                                                                                                                                                         "
		// +
		// "SELECT  '' as MIN_1, '' AS time_MIN_1,'' as MIN_10, '' AS time_MIN_10,'' as MIN_60, '' AS time_MIN_60, name as MIN_24h ,time||'' AS time_MIN_24h FROM                         "
		// +
		// "( SELECT DISTINCT(label) as name , MAX(elapsed) as time FROM CONTAINER_LOGS_DATA.BASIC_LOGS a WHERE a.timestamp > current_timestamp - interval      '1440 minutes'  GROUP BY  "
		// + "label  ORDER BY time DESC limit %s) t1", ancho,
		// ancho, ancho, ancho);

		RowMapper<Object[][]> maper = new RowMapper<Object[][]>() {

			String[] keys = { "min_1m", "min_10m", "min_1h", "min_24h" };
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

				ResultDTO result = new ResultDTO();
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

	public FileEnvironmentEntity getFileEnvironmentEntity(File file,
			String environment) {

		String sql = "SELECT * FROM CONTAINER_LOGS_DATA.FILE_ENVIRONMENT WHERE nameFile = ? AND environment = ?";
		List<FileEnvironmentEntity> result = connection.read(
				FileEnvironmentEntity.class, sql, file.getName(), environment);

		if (result.size() == 0)
			return null;
		return result.get(0);
	}
}
