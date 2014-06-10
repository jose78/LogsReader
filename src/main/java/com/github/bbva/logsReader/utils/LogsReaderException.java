package com.github.bbva.logsReader.utils;


/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 *
 */
public class LogsReaderException extends RuntimeException {

	
//	private static Logger log = Logger.getLogger(LogsReaderException.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LogsReaderException(Exception e) {
		super(e);
//		log.error(e.getMessage() , e);
	}

	public LogsReaderException(String msg , Object... data) {
		super(String.format(msg, data));
//		log.error( String.format(msg, data));
	}

	public LogsReaderException(Exception e, String msg, Object... data) {
		super(String.format(msg, data), e);
//		log.error(String.format(msg, data), e);
	}
}
