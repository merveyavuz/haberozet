package com.nlp.haber.ozet.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"controller","service"})
//@SpringBootApplication
public class HaberOzetApplication {

	public static void main(String[] args) {
		SpringApplication.run(HaberOzetApplication.class, args);
	}

}
