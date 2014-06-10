package com.github.bbva.logsReader.utils.enums;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;

public enum EnumSource {

	ONE(0, 10, "http://54.194.98.47/html/jmeter/ErrorNow/",
			"logscsv_enpp2_%s.csv", "logscsv_enpp_%s.csv");



	private  File file= null;
	public final static String APP_BUZZ = "BUZZ";
	public final static String APP_NXT = "NXT/WALLET";
	int numLines;
	int cont;
	int numExecutions;
	String url;
	String nameFileBuzz;
	String nameFileNxt;

	private EnumSource(int cont, int numLines, String url, String nameFileBuzz,
			String nameFileNxt) {
		this.numLines = numLines;
		this.url = url;
		this.nameFileBuzz = nameFileBuzz;
		this.nameFileNxt = nameFileNxt;
		this.cont = cont;
	}

	public boolean isExecutable() {

		boolean flag = cont == numExecutions++;

		if (cont < numExecutions)
			numExecutions = cont;

		return flag;
	}

	public File getFileBuzz() {
		return getFile(nameFileBuzz);
	}

	public File getFileNxt() {
		return getFile(nameFileNxt);
	}

	private File getFile(String fileNameApp) {

		try {

			
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			cal.add(Calendar.MINUTE, -120);
			SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");
			String date = sdf.format(cal.getTime());

			String spec = String.format(url + fileNameApp, date);
			URL url = new URL(spec);

			file = new File("tempo");
			FileUtils.copyURLToFile(url, file);
			return file;
		} catch (Exception e) {
			return null;
		}
	}
	
	public void clear(){
		if(file != null)
			file.delete();
	}

	public int getNumLines() {
		return numLines;
	}

}
