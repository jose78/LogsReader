package com.github.bbva.logsReader.view;

import java.util.Arrays;

public class ResultDonutView {

	private Object[][] donutData;

	public ResultDonutView(Object[][] donutData) {
		this.donutData = donutData;
	}

	public Object[][] getDonutData() {
		return donutData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("ResultDonutView [donutData=%s]",
				Arrays.toString(donutData));
	}

}
