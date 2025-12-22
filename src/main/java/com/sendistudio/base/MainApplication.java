package com.sendistudio.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.sendistudio.base.app.properties.AppProperties;
import com.sendistudio.base.app.properties.DatabaseProperties;
import com.sendistudio.base.app.properties.ServerProperties;

@SpringBootApplication()
@EnableConfigurationProperties({ AppProperties.class, ServerProperties.class, DatabaseProperties.class })
public class MainApplication {

	/*
	 * for run this project with profile use command below
	 * .\gradlew bootRun --args='--spring.profiles.active=local'
	 */
	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

}
