package com.github.bbva.logsReader.view;

import java.util.List;

/**
 * 
 * @author jose
 *
 */
public class ResultTableView {
	
	private List<RowTableView> rows;
	private List<ColumnTableView> cols;
	
	public ResultTableView() {
	}
	public ResultTableView( List<ColumnTableView> cols ,  List<RowTableView> rows) {
		this.cols = cols;
		this.rows = rows;
	}
	
	public List<ColumnTableView> getCols() {
		return cols;
	}
	
	public List<RowTableView> getRows() {
		return rows;
	}
	

}
