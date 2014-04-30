package com.github.bbva.logsReader.entity;

import com.github.bbva.logsReader.annt.Column;
import com.github.bbva.logsReader.annt.Id;
import com.github.bbva.logsReader.annt.Table;

/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 *
 */
@Table(name = "RESPUESTA", schema = "CONTAINER_LOGS_DATA")
public class RespuestaEntity {

	@Column(name = "ID")
	@Id(autoincrement = "id_respuesta")
	private int id = 23;

	@Id(autoincrement = "id_respuesta")
	@Column(name = "CODIGO_RESPUESTA")
	private int idServicio = 10000;

	@Column(name = "MENSAJE_RESPUESTA")
	private String idRespuesta = "PERREA PERREA ";

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

	public String getIdRespuesta() {
		return idRespuesta;
	}

	public void setIdRespuesta(String idRespuesta) {
		this.idRespuesta = idRespuesta;
	}

	@Override
	public String toString() {
		return String.format(
				"RespuestaEntity [id=%s, idServicio=%s, idRespuesta=%s]", id,
				idServicio, idRespuesta);
	}

}