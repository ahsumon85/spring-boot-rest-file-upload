package com.ahasan.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ahasan.file.common.utils.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationRunner.class, args);
		System.out.println("Spring boot application running.....");
	}

}
