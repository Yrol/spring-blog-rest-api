package com.yrol.blog;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBlogRestApiApplication {

	/**
	 * Injecting the model mapper (dependency) for mapping data with models to the Spring Context
	 * */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBlogRestApiApplication.class, args);
	}

}
