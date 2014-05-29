package com.github.bbva.logsReader.view;

import com.github.bbva.logsReader.annt.DTO;

/**
 * 
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
@DTO
public class ResultBarView<T> {

	private T listData;	
	private String axisX;
	private String axisY;
	

	public ResultBarView() {
	}

	public ResultBarView(String axisX, String axisY, T result) {
		
		this.axisX = axisX;
		this.axisY = axisY;
		this.listData = result;

	

	}



	public String getAxisX() {
		return axisX;
	}

	public String getAxisY() {
		return axisY;
	}

	public T getListData() {
		return listData;
	}

	public void setAxisX(String axisX) {
		this.axisX = axisX;
	}

	public void setAxisY(String axisY) {
		this.axisY = axisY;
	}

	public void setListData(T listData) {
		this.listData = listData;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("ResultBarView [listData=%s, axisX=%s, axisY=%s]",
				listData,  axisX, axisY);
	}

}
