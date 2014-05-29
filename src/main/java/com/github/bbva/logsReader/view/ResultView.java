package com.github.bbva.logsReader.view;


/**
 * 
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */

public class ResultView {

	private ResultTableView tableView;
	private ResultBarView<?> barView;
	private ResultDonutView donutView;

	/**
	 * @return the tableView
	 */
	public ResultTableView getTableView() {
		return tableView;
	}

	/**
	 * @param tableView
	 *            the tableView to set
	 */
	public void setTableView(ResultTableView tableView) {
		this.tableView = tableView;
	}

	/**
	 * @return the barView
	 */
	public ResultBarView getBarView() {
		return barView;
	}

	/**
	 * @param barView
	 *            the barView to set
	 */
	public void setBarView(ResultBarView barView) {
		this.barView = barView;
	}

	/**
	 * @return the donutView
	 */
	public ResultDonutView getDonutView() {
		return donutView;
	}

	/**
	 * @param donutView
	 *            the donutView to set
	 */
	public void setDonutView(ResultDonutView donutView) {
		this.donutView = donutView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"ResultDTO [tableView=%s, barView=%s, donutView=%s]",
				tableView, barView, donutView);
	}

}
