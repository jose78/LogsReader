package com.github.bbva.logsReader.db;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;

/**
 * Interface that provides a pattern CRUD  as façade to access  to DataBase . 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 *
 */
public interface DBConnection {

	/**
	 * Execute the select and map the result in a collection of <b>T</b>
	 * @param clazz
	 * @param sql
	 * @param params
	 * @return
	 */
	public abstract <T> List<T> read(Class<T> clazz, String sql,
			Object... params);
	
	/**
	 * Execute the select and map the result in a collection of <b>T</b>
	 * @param clazz
	 * @param sql
	 * @param params
	 * @return
	 */
	public abstract <T> List<T> read(RowMapper<T> loader, Class<T> clazz, String sql,
			Object... params);

	/**
	 * Delete this item.
	 * @param data
	 * @return
	 */
	public abstract <T> int delete(T data);

	/**
	 * Update this item
	 * @param data
	 * @return
	 */
	public abstract <T> int update(T data);

	/**
	 * Insert this item and return the same entity with the ID generated.
	 * @param data
	 * @return
	 */
	public abstract <T> T insert(T data);
	
	
//	/**
//	 * Commit the transactions.
//	 */
//	public void commit();
//	
//	/**
//	 * Rollback the transactions.
//	 */
//	public void rollbak();

}