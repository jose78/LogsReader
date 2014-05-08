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

	@Column(name = "label")
	private String service;

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
	public InfoServicesDTO(String service, int aggregate, int standaDesviation,
			int ok, int failUp, int failDown) {
		super();
		this.service = service;
		this.aggregate = aggregate;
		this.standaDesviation = standaDesviation;
		this.ok = ok;
		this.failUp = failUp;
		this.failDown = failDown;
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("InfoServicesDTO [service=%s, aggregate=%s, standaDesviation=%s, ok=%s, failUp=%s, failDown=%s]",
						service, aggregate, standaDesviation, ok, failUp,
						failDown);
	}

}
