/**
 * 
 */
package com.github.bbva.logsReader.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.github.bbva.logsReader.annt.Column;
import com.github.bbva.logsReader.annt.DTO;
import com.github.bbva.logsReader.annt.Entity;
import com.github.bbva.logsReader.annt.Id;
import com.github.bbva.logsReader.annt.Table;
import com.github.bbva.logsReader.utils.ClassUtils;
import com.github.bbva.logsReader.utils.LogsReaderException;

/**
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */

public class CRUD implements DBConnection {

	private static Logger log = Logger.getLogger(CRUD.class);

	private @Value("#{ environment['OPENSHIFT_POSTGRESQL_DB_USERNAME'] }") String dbUser="reader_logs";
	private @Value("#{ environment['OPENSHIFT_POSTGRESQL_DB_PASSWORD'] }") String dbPass= "tempo";
	private @Value("#{ environment['OPENSHIFT_POSTGRESQL_DB_HOST'] }") String dbHost= "localhost";
	private @Value("#{ environment['OPENSHIFT_POSTGRESQL_DB_PORT'] }") String dbPort= "5432";
	private @Value("#{ environment['PGDATABASE']}") String dbName;
	private @Value("${logReader.db.autoCommit}") Boolean dbAutoCommit;
	private @Value("${logReader.db.driver}") String dbDriver;
	
	@Qualifier("jdbcTemplate")
	@Autowired
	private JdbcTemplate template;	
	
	@Autowired
	private ClassUtils classUtils;


	@Qualifier("datasource")
	@Autowired
	SingleConnectionDataSource ds;

	public void init() {
		
//		try {
//			Class.forName(dbDriver);
//			log.info("PostgreSQL JDBC Driver Registered!");
//		} catch (ClassNotFoundException e) {
//			log.error("Where is your PostgreSQL JDBC Driver? "
//					+ "Include in your library path!", e);
//			e.printStackTrace();
//			return;
//		}
//
		String dbUrl = String.format("jdbc:postgresql://%s:%s/%s", dbHost,dbPort, dbName);
		ds.setDriverClassName(dbDriver);
		ds.setPassword(dbPass);
		ds.setUrl(dbUrl);
		ds.setUsername(dbUser);
		ds.setAutoCommit(dbAutoCommit);
		
		log.info("-------- PostgreSQL JDBC Connection Testing ------------");
		log.info(String.format("     -> Usuario:%s\n     ->nURL:%s", dbUser,dbUrl));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.bbva.logsReader.db.DbConnection#read(java.lang.Class,
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public <T> List<T> read(Class<T> clazz, String sql, Object... params) {
		List<T> lst = new ArrayList<T>();
		log.info("SQL: " + sql);
		
		try {
			lst = template.query(sql, new InternalRowMapper<T>(clazz));
			return lst;
		} catch (Exception e) {
			throw new LogsReaderException(e, "Error reading the SQL:[%s]", sql);
		} 
	}

	private  class InternalRowMapper<T> implements RowMapper<T>{

		private Class<T> clazz;
		private String[] columnNames= null;
		
		public InternalRowMapper(Class<T> clazz) {
			this.clazz = clazz;
		}
				
		@Override
		public T mapRow(ResultSet rs, int numLine) throws SQLException {
			
			if(columnNames == null){
				ResultSetMetaData rsMetaData = rs.getMetaData();
				columnNames= new String[rsMetaData.getColumnCount()];
				for (int index = 0; index < rsMetaData.getColumnCount(); index++) {
					columnNames[index] = rsMetaData.getColumnName(index + 1);
				}
			}
			T data = null;

			if (clazz.getAnnotation(Entity.class) != null || clazz.getAnnotation(DTO.class) != null) {
				try {
					data = clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}

				for (String columnName : columnNames) {
					Object result = rs.getObject(columnName);
					classUtils.setValueInColumn(data, columnName, result);
				}
			} else{
				data = (T) rs.getObject(1);
			}
			
			return data;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.bbva.logsReader.db.DbConnection#delete(T)
	 */
	@Override
	public <T> int delete(T data) {

		try {
			Table anntTable = (Table) data.getClass().getAnnotation(Table.class);
			String nameTable = anntTable.schema() + "." + anntTable.name();

			Object value[] = classUtils.getValueClass(data, Column.class);
			String columns[] = classUtils.getValueAnnt(data, Column.class,
					new ClassUtils.ProviderValueAnnt<String>() {
						public String get(Object annt) {
							Column anntColumn = (Column) annt;
							return anntColumn.name();
						}
					}, false);
			String params = "";
			if (columns.length > 0)
				params = String.format("%s %s",	StringUtils.join(columns, " = ? AND "), " = ?");
			else
				params = "(1 = 1)";

			String sql = String.format("DELETE FROM %s WHERE %s", nameTable,params);
			
			int numberRows = template.update(sql , value);			
			log.info(String.format("Number of rows afected %s", numberRows));

			return numberRows;
		} catch (Exception e) {
			throw new LogsReaderException(e, "Error deleting the data:[%s]",data);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.bbva.logsReader.db.DbConnection#update(T)
	 */
	@Override
	public <T> int update(T data) {
		
		try {
			Table anntTable = (Table) data.getClass().getAnnotation(Table.class);
			String nameTable = anntTable.schema() + "." + anntTable.name();
			Object value[] = classUtils.getValueClass(data, Column.class,Id.class);
			Object valueIds[] = classUtils.getValueClass(data, Id.class);
			String ids[] = classUtils.getIdColumndName(data.getClass());
			String columns[] = classUtils.getValueAnnt(data, Column.class,
					new ClassUtils.ProviderValueAnnt<String>() {

						public String get(Object annt) {
							Column anntColumn = (Column) annt;
							return anntColumn.name();
						}
					}, false, Id.class);
			String column = "";
			if (columns.length > 0)
				column = String.format("%s %s",	StringUtils.join(columns, " = ? , "), " = ?");

			String id = null;
			if (ids.length > 0)
				id = String.format("%s", StringUtils.join(ids, " = ? AND "))+ " = ?";
			else
				id = "(1 = 1)";

			String sql = String.format("UPDATE %s SET %s WHERE %s", nameTable,column, id);
			log.info(sql);
			
			int numberRows = template.update(sql,value);
			log.info(String.format("Number of rows afected %s", numberRows));
			return numberRows;
		} catch (Exception e) {
			throw new LogsReaderException(e, "Error updating the data:[%s]",
					data);
		} 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.bbva.logsReader.db.DbConnection#insert(T)
	 */
	@Override
	synchronized public <T> T insert(T data) {
		try {
			Table anntTable = (Table) data.getClass()
					.getAnnotation(Table.class);
			String nameTable = anntTable.schema() + "." + anntTable.name();

			String nameAutoIncrement[] = classUtils.getValueAnnt(data,
					Id.class, new ClassUtils.ProviderValueAnnt<String>() {
						public String get(Object annt) {
							Id anntColumn = (Id) annt;
							return anntColumn.autoincrement();
						}
					}, true);
			final Object[] params = classUtils.getValueClass(data, Column.class,Id.class);
			
			String[] columns = classUtils.getValueAnnt(data, Column.class,
					new ClassUtils.ProviderValueAnnt<String>() {
						public String get(Object annt) {
							Column anntColumn = (Column) annt;
							return anntColumn.name();
						}
					}, true);

			String paramsStr = StringUtils.repeat(", ?", params.length).replaceFirst(",", "");

			String nextVal = "";
			if (nameAutoIncrement.length > 0) {
				nextVal = String.format("%s", StringUtils.repeat("nextval('%s'), ", nameAutoIncrement.length));
				nextVal = String.format(nextVal, nameAutoIncrement);

			}

			final String sql = String.format("INSERT INTO %s (%s) VALUES (%s %s)",
					nameTable, 
					StringUtils.join(columns, ", "), 
					nextVal,	
					paramsStr);

			 KeyHolder keyHolder = new GeneratedKeyHolder();
			 int numberRows = template.update(
	                new PreparedStatementCreator() {
						
						@Override
						public PreparedStatement createPreparedStatement(Connection cnx)
								throws SQLException {
	                        PreparedStatement ps =
	                            cnx.prepareStatement(sql,  Statement.RETURN_GENERATED_KEYS);
	                        setValuesInPreparedStatement(ps, params);
	                        return ps;
	                    }
	                },
	                keyHolder);
			
			for (Entry<String, Object> entry: keyHolder.getKeys().entrySet()) {
				classUtils.setIdInEntity(data, entry.getKey(), entry.getValue());
			}

			return data;
		}catch (Exception e) {
			throw new LogsReaderException(e, "Error inserting the data:[%s]",
					data);
		}
	}

	private void setValuesInPreparedStatement(PreparedStatement ps,
			Object[] params) throws SQLException {
		for (int index = 1; index <= params.length; index++) {
			if (params[index - 1] instanceof Timestamp) {
				ps.setTimestamp(index, (Timestamp) params[index - 1]);
			} else
				ps.setObject(index, params[index - 1]);
		}
	}


	@Override
	public <T> List<T> read(RowMapper<T> loader, Class<T> clazz, String sql,Object... params) {
		List<T> lst = new ArrayList<T>();
		log.info("SQL: " + sql);
		try {
			lst = template.query(sql, params,loader);
			return lst;
		} catch (Exception e) {
			throw new LogsReaderException(e, "Error reading the SQL:[%s]", sql);
		} 
	}
}
