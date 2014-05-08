package com.github.bbva.logsReader.utils;

public class FrecuencyGaussianDto {

	int yFrecuency;
	int xGaussian;
	int x;

	public FrecuencyGaussianDto(int yFrecuency, int xGaussian, int x) {
		this.yFrecuency = yFrecuency;
		this.xGaussian = xGaussian;
		this.x = x;
	}

	public int getyFrecuency() {
		return yFrecuency;
	}

	public int getX() {
		return x;
	}

	public int getxGaussian() {
		return xGaussian;
	}

	@Override
	public String toString() {
		return String.format("%s [yFrecuency=%s, xGaussian=%s, x=%s]", this
				.getClass().getSimpleName(), yFrecuency, xGaussian, x);
	}

}
