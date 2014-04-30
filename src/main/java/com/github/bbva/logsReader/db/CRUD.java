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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.github.bbva.logsReader.annt.Column;
import com.github.bbva.logsReader.annt.Id;
import com.github.bbva.logsReader.annt.Table;
import com.github.bbva.logsReader.utils.ClassUtils;
import com.github.bbva.logsReader.utils.LoadData;
import com.github.bbva.logsReader.utils.LogsReaderException;

/**
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */

public class CRUD implements DBConnection {

	private static Logger log = Logger.getLogger(CRUD.class);

	@Value("${com.github.bbva.logReader.autoCommit}")
	private Boolean dbAutoCommit;
	@Value("${com.github.bbva.logReader.db.driver}")
	private String dbDriver;
	@Value("${com.github.bbva.logReader.db.url}")
	private String dbUrl;
	@Value("${com.github.bbva.logReader.db.user}")
	private String dbUser;
	@Value("${com.github.bbva.logReader.db.pass}")
	private String dbPass;

	@Autowired
	private ClassUtils classUtils;

	@Autowired
	private DriverManagerDataSource ds;

	

	public  void init() {
		log.info("-------- PostgreSQL JDBC Connection Testing ------------");
		ds.setDriverClassName(dbDriver);
		ds.setPassword(dbPass);
		ds.setUrl(dbUrl);
		ds.setUsername(dbUser);
	}

	//
	// try {
	// Class.forName(dbDriver);
	// log.info("PostgreSQL JDBC Driver Registered!");
	// } catch (ClassNotFoundException e) {
	// log.error("Where is your PostgreSQL JDBC Driver? "
	// + "Include in your library path!", e);
	// e.printStackTrace();
	// return;
	// }
	// }
	// private Connection createConnection() {
	//
	// try {
	// Connection connection = DriverManager.getConnection(dbUrl,
	// dbUser,dbPass);
	// connection.setAutoCommit(dbAutoCommit);
	// return connection;
	//
	// } catch (SQLException e) {
	// log.error("Connection Failed! Check output console");
	// e.printStackTrace();
	// return null;
	// }
	// }

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
		try {
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

				if (clazz.getAnnotation(Table.class) != null) {
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
				ps.close();
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

			ps = getLocalConnection().prepareStatement(sql);
			setValuesInPreparedStatement(ps, value);
			int numberRows = ps.executeUpdate();
			log.info(String.format("Number of rows afected %s", numberRows));
			return numberRows;
		} catch (Exception e) {
			throw new LogsReaderException(e, "Error deleting the data:[%s]",
					data);
		} finally {
			try {
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

			ps = getLocalConnection().prepareStatement(sql);
			setValuesInPreparedStatement(ps, ArrayUtils.addAll(value, valueIds));
			int numberRows = ps.executeUpdate();
			log.info(String.format("Number of rows afected %s", numberRows));
			return numberRows;
		} catch (Exception e) {
			throw new LogsReaderException(e, "Error updating the data:[%s]",
					data);
		} finally {
			try {
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
	public <T> T insert(T data) {
		PreparedStatement ps = null;
		ResultSet rs = null;
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

			ps = getLocalConnection().prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);

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
		} catch (Exception e) {
			throw new LogsReaderException(e, "Error inserting the data:[%s]",
					data);
		} finally {
			try {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.bbva.logsReader.db.DBConnection#commit()
	 */
	@Override
	public void commit() {
		if (!dbAutoCommit)
			try {
				getLocalConnection().commit();

			} catch (SQLException e) {
				throw new LogsReaderException(e, "Error making commit.");
			}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.bbva.logsReader.db.DBConnection#rollbak()
	 */
	@Override
	public void rollbak() {
		try {
			getLocalConnection().rollback();
		} catch (SQLException e) {
			throw new LogsReaderException(e, "Error making rollback.");
		}
	}

	@Override
	public <T> List<T> read(Class<T> clazz, String sql, LoadData<T> loader,
			Object... params) {
		List<T> lst = new ArrayList<T>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
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
				T data = loader.load(rs);
				lst.add(data);
			}
			return lst;
		} catch (Exception e) {
			throw new LogsReaderException(e, "Error reading the SQL:[%s]", sql);
		} finally {
			try {
				ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Connection getLocalConnection() throws SQLException {

		Connection cnx = ds.getConnection();
		cnx.setAutoCommit(dbAutoCommit);
		return cnx;
	}
}
