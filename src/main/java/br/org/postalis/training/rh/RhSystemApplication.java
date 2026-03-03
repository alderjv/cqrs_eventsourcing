package br.org.postalis.training.rh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RhSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RhSystemApplication.class, args);
    }

}
