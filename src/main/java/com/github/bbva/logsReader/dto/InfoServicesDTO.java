package com.github.bbva.logsReader.dto;

import com.github.bbva.logsReader.annt.Column;
import com.github.bbva.logsReader.annt.DTO;

/**
 * 
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
@DTO
public class InfoServicesDTO {

	@Column(name = "PERCENTILE")
	private String percentile;

	@Column(name = "Q2")
	private long medianColum;

	@Column(name = "Q1")
	private long q1;

	@Column(name = "Q3")
	private long q3;

	@Column(name = "nameService")
	private String service;

	@Column(name = "application")
	private String application;

	@Column(name = "f_max")
	private long max;
	@Column(name = "f_min")
	private long min;

	@Column(name = "f_avg")
	private long aggregate;

	@Column(name = "standadesviation")
	private long standaDesviation;

	@Column(name = "percent_total")
	private double ok;

	@Column(name = "percent_up")
	private double failUp;

	@Column(name = "percent_down")
	private double failDown;

	public InfoServicesDTO() {
	}
	
	
	
	public long getMax() {
		return max;
	}
	public long getMin() {
		return min;
	}
	public long getMedianColum() {
		return medianColum;
	}
	public String getPercentile() {
		return percentile;
	}
	public long getQ1() {
		return q1;
	}
	public long getQ3() {
		return q3;
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	public String getApplication() {
		return application;
	}

	/**
	 * 
	 * @return the aggregate
	 */
	public long getAggregate() {
		return aggregate;
	}

	/**
	 * @return the covariance
	 */
	public long getStandaDesviation() {
		return standaDesviation;
	}

	/**
	 * @return the ok
	 */
	public double getOk() {
		return ok;
	}

	/**
	 * @return the failUp
	 */
	public double getFailUp() {
		return failUp;
	}

	/**
	 * @return the failDown
	 */
	public double getFailDown() {
		return failDown;
	}

	@Override
	public String toString() {
		return "InfoServicesDTO [percentile=" + percentile + ", medianColum="
				+ medianColum + ", q1=" + q1 + ", q3=" + q3 + ", service="
				+ service + ", application=" + application + ", aggregate="
				+ aggregate + ", standaDesviation=" + standaDesviation
				+ ", ok=" + ok + ", failUp=" + failUp + ", failDown="
				+ failDown + "]";
	}

}
