package com.github.bbva.logsReader.entity;

import java.util.Date;

import com.github.bbva.logsReader.annt.Column;
import com.github.bbva.logsReader.annt.Id;
import com.github.bbva.logsReader.annt.Table;

/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 *
 */
@Table(name = "LOGS", schema = "CONTAINER_LOGS_DATA")
public class RetroLogEntity {

	@Column(name = "ID")
	@Id(autoincrement = "id_log")
	private int id = 34;
	
	@Column(name = "ID_SERVICIO")
	private int idServicio= 69;
	
	@Column(name = "ID_RESPUESTA")
	private int idRespuesta= 233;
	
	@Column(name = "URL_RESPUESTA")
	private String url = "http//www.google.es";
	
	@Column(name = "Fecha")
	private Date fecha = new Date(System.currentTimeMillis());

	@Column(name = "Tiempo_respuesta")
	int tiempoRespuesta = 56;

	
	@Column(name = "TRAZA")
	String traza = "TRazadoood";
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdServicio() {
		return idServicio;
	}

	public void setIdServicio(int idServicio) {
		this.idServicio = idServicio;
	}

	public int getIdRespuesta() {
		return idRespuesta;
	}

	public void setIdRespuesta(int idRespuesta) {
		this.idRespuesta = idRespuesta;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public int getTiempoRespuesta() {
		return tiempoRespuesta;
	}

	public void setTiempoRespuesta(int tiempoRespuesta) {
		this.tiempoRespuesta = tiempoRespuesta;
	}

	@Override
	public String toString() {
		return String
				.format("LogsEntity [id=%s, idServicio=%s, idRespuesta=%s, url=%s, fecha=%s, tiempoRespuesta=%s,traza=%s]",
						id, idServicio, idRespuesta, url, fecha,
						tiempoRespuesta , traza);
	}
	
	
}