package com.CAN301_ios.forum_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ForumServerApplication {

	public static void main(String[] args) throws ClassNotFoundException {
		SpringApplication.run(ForumServerApplication.class, args);
	}

}
