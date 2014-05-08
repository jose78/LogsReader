package com.github.bbva.logsReader.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class TsvMessageConverter extends AbstractHttpMessageConverter<Map<String, String>> {
	
	   public static final MediaType MEDIA_TYPE = new MediaType("text", "tsv" ,Charset.forName("utf-8"));
	   
	   
	   public TsvMessageConverter() {
	       super(MEDIA_TYPE);
	   }

	   
//
//	   protected void writeInternal(TsvResponse response, HttpOutputMessage output) throws IOException, HttpMessageNotWritableException {
//	       output.getHeaders().setContentType(MEDIA_TYPE);
//	       output.getHeaders().set("Content-Disposition", "attachment; filename=\"" + response.getFilename() + "\"");
//	       OutputStream out = output.getBody();
//	       CsvWriter writer = new CsvWriter(new OutputStreamWriter(out), '\u0009');
//	       List<CompositeRequirement> allRecords = response.getRecords();
//	       for (int i = 1; i < allRecords.size(); i++) {
//	            CompositeRequirement aReq = allRecords.get(i);
//	            writer.write(aReq.toString());
//	       }
//	       writer.close();
//	   }

	@Override
	protected Map<String, String> readInternal(
			Class<? extends Map<String, String>> clazz,
			HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void writeInternal(Map<String, String> t,
			HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {
		// TODO Auto-generated method stub
		
	}
	


	@Override
	protected boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return false;
	}
	
}