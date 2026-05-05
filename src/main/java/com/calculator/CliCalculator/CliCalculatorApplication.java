package com.calculator.CliCalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/calc")
public class CliCalculatorApplication {

	@GetMapping("/add")
	public int add(@RequestParam int a, @RequestParam int b) {
		return a + b;
	}

	public static void main(String[] args) {
		SpringApplication.run(CliCalculatorApplication.class, args);
	}

}
