package dev.erencalbay.merge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MergeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MergeApplication.class, args);
	}

	@GetMapping("/")
	public String apiRoot() {
		return  "Hello World!";
	}
}

