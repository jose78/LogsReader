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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

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

	private @Value("#{ environment['OPENSHIFT_POSTGRESQL_DB_USERNAME'] }") String dbUser;
	private @Value("#{ environment['OPENSHIFT_POSTGRESQL_DB_PASSWORD'] }") String dbPass;
	private @Value("#{ environment['OPENSHIFT_POSTGRESQL_DB_HOST'] }") String dbHost;
	private @Value("#{ environment['OPENSHIFT_POSTGRESQL_DB_PORT'] }") String dbPort;
	private @Value("${logReader.db.autoCommit}") Boolean dbAutoCommit;
	private @Value("${logReader.db.driver}") String dbDriver;

	@Autowired
	private ClassUtils classUtils;

	@Qualifier("dataSource")
	@Autowired
	private DriverManagerDataSource ds;

	private Connection reader;
	private Connection writer;

	public void init() {
		String dbUrl = String.format("jdbc:postgresql://%s:%s/app", dbHost,
				dbPort);
		log.info("-------- PostgreSQL JDBC Connection Testing ------------");
		log.info(String.format("     -> Usuario:%s\n     ->nURL:%s", dbUser,
				dbUrl));

		try {
			Class.forName(dbDriver);
			log.info("PostgreSQL JDBC Driver Registered!");
		} catch (ClassNotFoundException e) {
			log.error("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!", e);
			e.printStackTrace();
			return;
		}

		ds.setDriverClassName(dbDriver);
		ds.setPassword(dbPass);
		ds.setUrl(dbUrl);
		ds.setUsername(dbUser);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.bbva.logsReader.db.DbConnection#read(java.lang.Class,
	 * java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> read(Class<T> clazz, String sql, Object... params) {

		List<T> lst = new ArrayList<T>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		log.info("SQL: " + sql);
		
		try {
			
			ps = getReaderStatement(sql);
			ps = getLocalConnection().prepareStatement(sql);
			setValuesInPreparedStatement(ps, params);
			rs = ps.executeQuery();
			ResultSetMetaData rsMetaData = rs.getMetaData();
			int countColumn = rsMetaData.getColumnCount();
			String[] columnNames = new String[countColumn];

			for (int index = 0; index < countColumn; index++) {
				columnNames[index] = rsMetaData.getColumnName(index + 1);
			}

			while (rs.next()) {
				T data = null;

				if (clazz.getAnnotation(Entity.class) != null
						|| clazz.getAnnotation(DTO.class) != null) {
					data = clazz.newInstance();

					for (String columnName : columnNames) {
						Object result = rs.getObject(columnName);
						classUtils.setValueInColumn(data, columnName, result);
					}
				} else
					data = (T) rs.getObject(1);
				lst.add(data);
			}
			return lst;
		} catch (Exception e) {
			throw new LogsReaderException(e, "Error reading the SQL:[%s]", sql);
		} finally {
			try {

				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.bbva.logsReader.db.DbConnection#delete(T)
	 */
	@Override
	public <T> int delete(T data) {
		PreparedStatement ps = null;
		Connection cnx = writer;
		try {
			Table anntTable = (Table) data.getClass()
					.getAnnotation(Table.class);
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
				params = String.format("%s %s",
						StringUtils.join(columns, " = ? AND "), " = ?");
			else
				params = "(1 = 1)";

			String sql = String.format("DELETE FROM %s WHERE %s", nameTable,
					params);

			ps = cnx.prepareStatement(sql);
			setValuesInPreparedStatement(ps, value);
			int numberRows = ps.executeUpdate();
			log.info(String.format("Number of rows afected %s", numberRows));
			return numberRows;
		} catch (Exception e) {
			throw new LogsReaderException(e, "Error deleting the data:[%s]",
					data);
		} finally {
			try {

				if (ps != null)
					ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.bbva.logsReader.db.DbConnection#update(T)
	 */
	@Override
	public <T> int update(T data) {
		PreparedStatement ps = null;
		Connection cnx = writer;
		try {
			Table anntTable = (Table) data.getClass()
					.getAnnotation(Table.class);
			String nameTable = anntTable.schema() + "." + anntTable.name();

			Object value[] = classUtils.getValueClass(data, Column.class,
					Id.class);
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
				column = String.format("%s %s",
						StringUtils.join(columns, " = ? , "), " = ?");

			String id = null;
			if (ids.length > 0)
				id = String.format("%s", StringUtils.join(ids, " = ? AND "))
						+ " = ?";
			else
				id = "(1 = 1)";

			String sql = String.format("UPDATE %s SET %s WHERE %s", nameTable,
					column, id);

			log.info(sql);

			
			ps = cnx.prepareStatement(sql);

			setValuesInPreparedStatement(ps, ArrayUtils.addAll(value, valueIds));
			int numberRows = ps.executeUpdate();
			log.info(String.format("Number of rows afected %s", numberRows));
			return numberRows;
		} catch (Exception e) {
			throw new LogsReaderException(e, "Error updating the data:[%s]",
					data);
		} finally {
			try {

				if (ps != null)
					ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.bbva.logsReader.db.DbConnection#insert(T)
	 */
	@Override
	synchronized public <T> T insert(T data) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection cnx = null;
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
			Object[] params = classUtils.getValueClass(data, Column.class,
					Id.class);
			String[] columns = classUtils.getValueAnnt(data, Column.class,
					new ClassUtils.ProviderValueAnnt<String>() {

						public String get(Object annt) {
							Column anntColumn = (Column) annt;
							return anntColumn.name();
						}
					}, true);

			String paramsStr = StringUtils.repeat(", ?", params.length)
					.replaceFirst(",", "");

			String nextVal = "";
			if (nameAutoIncrement.length > 0) {
				nextVal = String.format("%s", StringUtils.repeat(
						"nextval('%s'), ", nameAutoIncrement.length));
				nextVal = String.format(nextVal, nameAutoIncrement);

			}

			String sql = String.format("INSERT INTO %s (%s) VALUES (%s %s)",
					nameTable, StringUtils.join(columns, ", "), nextVal,
					paramsStr);

			cnx = writer;

			ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			setValuesInPreparedStatement(ps, params);

			int numberRows = ps.executeUpdate();

			rs = ps.getGeneratedKeys();
			Column[] columnsTmp = classUtils.getIdColumnd(data.getClass());
			if (rs.next()) {
				for (Column column : columnsTmp) {
					Object result = rs.getObject(column.name());
					classUtils.setIdInEntity(data, column.name(), result);
				}
			}

			return data;
		} catch (SQLException e) {
			if (e.getErrorCode() != 23505)
				throw new LogsReaderException(e, "Error EC:[%s] inserting the data:[%s]",e.getErrorCode(),data);
			return data;
		}catch (Exception e) {
			throw new LogsReaderException(e, "Error inserting the data:[%s]",
					data);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	public <T> List<T> read(RowMapper<T> loader, Class<T> clazz, String sql,
			Object... params) {
		List<T> lst = new ArrayList<T>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		log.info("SQL: " + sql);
		
		try {
			
			ps = getReaderStatement(sql);
			setValuesInPreparedStatement(ps, params);
			rs = ps.executeQuery();
			ResultSetMetaData rsMetaData = rs.getMetaData();
			int countColumn = rsMetaData.getColumnCount();
			String[] columnNames = new String[countColumn];

			for (int index = 0; index < countColumn; index++) {
				columnNames[index] = rsMetaData.getColumnName(index + 1);
			}

			int count = 0;
			while (rs.next()) {
				T data = loader.mapRow(rs, count++);
				lst.add(data);
			}
			return lst;
		} catch (Exception e) {
			throw new LogsReaderException(e, "Error reading the SQL:[%s]", sql);
		} finally {
			try {

				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void openWriter() {
		try {
			this.writer = getLocalConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeWriter() {
		try {
			if (this.writer != null)
				this.writer.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private PreparedStatement getReaderStatement(String sql) throws SQLException {
		try {
			return this.reader.prepareStatement(sql);
		} catch (Exception e) {
			log.info("Connection Reader created.");
			this.reader = getLocalConnection();
			return this.reader.prepareStatement(sql);			
		}
	}

	private Connection getLocalConnection() throws SQLException {

		return ds.getConnection();

		// cnx = ds.getConnection();
		// cnx.setAutoCommit(dbAutoCommit);
		// return cnx;
	}
}
