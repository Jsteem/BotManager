package botmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class BotManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotManagerApplication.class, args);
	}

}
