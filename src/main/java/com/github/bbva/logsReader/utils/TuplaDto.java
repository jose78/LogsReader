package com.github.bbva.logsReader.utils;

/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 *
 */
public class TuplaDto {

	String x;
	String y;

	public TuplaDto(String x, String y) {
		this.x = x;
		this.y = y;
	}

	public String getX() {
		return x;
	}

	public String getY() {
		return y;
	}

	@Override
	public String toString() {
		return String.format("TuplaDto [x=%s, y=%s]", x, y);
	}

}
