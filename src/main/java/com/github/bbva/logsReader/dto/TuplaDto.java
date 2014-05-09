package com.github.bbva.logsReader.dto;

import com.github.bbva.logsReader.annt.Column;
import com.github.bbva.logsReader.annt.DTO;

/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 *
 */
@DTO
public class TuplaDto {

	@Column(name="X")
	int x;
	
	@Column(name= "Y")
	String y;

	public TuplaDto() {
	}
	public TuplaDto( String y ,  int x) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public String getY() {
		return y;
	}

	@Override
	public String toString() {
		return String.format(" %s, %s",  y , x);
	}

}
