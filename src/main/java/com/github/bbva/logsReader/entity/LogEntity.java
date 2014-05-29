package com.github.bbva.logsReader.entity;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import com.github.bbva.logsReader.annt.Column;
import com.github.bbva.logsReader.annt.Entity;
import com.github.bbva.logsReader.annt.Id;
import com.github.bbva.logsReader.annt.Table;

/**
 * 
 * @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero -
 *         Neoris</a>
 * 
 */
@Entity
@Table(name = "BASIC_LOGS", schema = "CONTAINER_LOGS_DATA")
public class LogEntity {

	@Column(name = "id")
	@Id(autoincrement = "id_BASIC_LOGS")
	private Integer id;

	@Column(name = "Latency")
	private Integer Latency;

	@Column(name = "bytes")
	private Integer bytes;

	@Column(name = "dataType")
	private String dataType;

	@Column(name = "elapsed")
	private Integer elapsed;

	@Column(name = "failureMessage")
	private String failureMessage;

	@Column(name = "label")
	private String label;

	@Column(name = "responseCode")
	private String responseCode;

	@Column(name = "responseMessage")
	private String responseMessage;

	@Column(name = "success")
	private String success;

	@Column(name = "threadName")
	private String threadName;

	@Column(name = "timeStamp")
	private Timestamp timeStamp;

	@Column(name = "application")
	private String application;

	@Column(name = "environment")
	private String environment;

	public Integer getLatency() {
		return Latency;
	}

	public void setLatency(int latency) {
		latency = latency;
	}

	public Integer getBytes() {
		return bytes;
	}

	public void setBytes(int bytes) {
		this.bytes = bytes;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getElapsed() {
		return elapsed;
	}

	public void setElapsed(int elapsed) {
		this.elapsed = elapsed;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	public LogEntity() {
	}

	public LogEntity(Map<String, String> map) {

		this.Latency = Integer.parseInt(map.get("Latency"));
		this.bytes = Integer.parseInt(map.get("bytes"));
		this.dataType = map.get("dataType");
		this.elapsed = Integer.parseInt(map.get("elapsed"));
		this.failureMessage = map.get("failureMessage");
		this.label = map.get("label");
		this.responseCode = map.get("responseCode");
		this.responseMessage = map.get("responseMessage");
		this.success = map.get("success");
		this.threadName = map.get("threadName");
		this.application = map.get("application");
		this.environment = map.get("environment"); 

		// 2014/04/09 00:00:08.960
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		try {
			
			int sec = 600;

			this.timeStamp = new Timestamp(sdf.parse(map.get("timeStamp"))
					.getTime());
			
	        Calendar cal = Calendar.getInstance();
	        cal.setTimeInMillis(timeStamp.getTime());
//	        cal.add(Calendar.HOUR, 2);
	        this.timeStamp = new Timestamp(cal.getTime().getTime());
	        
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String getApplication() {
		return application;
	}

	public String getEnvironment() {
		return environment;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("LogEntity [id=%s, Latency=%s, bytes=%s, dataType=%s, elapsed=%s, failureMessage=%s, label=%s, responseCode=%s, responseMessage=%s, success=%s, threadName=%s, timeStamp=%s, application=%s, environment=%s]",
						id, Latency, bytes, dataType, elapsed, failureMessage,
						label, responseCode, responseMessage, success,
						threadName, timeStamp, application, environment);
	}
	
	

}
