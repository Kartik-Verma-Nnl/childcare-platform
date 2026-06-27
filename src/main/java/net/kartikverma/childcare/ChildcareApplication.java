package net.kartikverma.childcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ChildcareApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChildcareApplication.class, args);
    }

}
