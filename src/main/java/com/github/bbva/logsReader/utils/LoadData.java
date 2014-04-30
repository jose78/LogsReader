package com.github.bbva.logsReader.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 *
 * @param <T>
 */
public interface LoadData<T> {
	
	public T load(ResultSet rs) throws SQLException;

}
