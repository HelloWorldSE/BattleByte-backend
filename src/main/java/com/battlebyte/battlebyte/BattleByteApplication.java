package com.battlebyte.battlebyte;

import com.battlebyte.battlebyte.service.MatchService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BattleByteApplication {

	public static void main(String[] args) {
		MatchService.start();
		SpringApplication.run(BattleByteApplication.class, args);
	}

}
