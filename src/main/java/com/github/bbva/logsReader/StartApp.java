package com.github.bbva.logsReader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


/**
 * 
 *  @author <a href="mailto:jose.clavero.contractor@bbva.com">jose.clavero - Neoris</a> 
 *
 */

@ImportResource("classpath:applicationContext.xml")
@ComponentScan
@Configuration
@EnableAutoConfiguration
public class StartApp {
	
	public static void main(String[] args) {
		SpringApplication.run(StartApp.class, args);
	}
}
