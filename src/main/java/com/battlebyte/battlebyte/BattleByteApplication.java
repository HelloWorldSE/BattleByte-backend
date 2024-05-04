package com.battlebyte.battlebyte;

import com.battlebyte.battlebyte.config.SimpleJpaRepositoryImpl;
import com.battlebyte.battlebyte.service.MatchService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(repositoryBaseClass = com.battlebyte.battlebyte.config.SimpleJpaRepositoryImpl.class)
@SpringBootApplication
public class BattleByteApplication {

	public static void main(String[] args) {
		SpringApplication.run(BattleByteApplication.class, args);
		MatchService.start();
	}

}
