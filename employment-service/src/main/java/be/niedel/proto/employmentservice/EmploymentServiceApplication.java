package be.niedel.proto.employmentservice;

import be.niedel.proto.employmentservice.contract.CreateEmployerRequest;
import be.niedel.proto.employmentservice.contract.EmployerSize;
import be.niedel.proto.employmentservice.contract.Id;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.apache.tomcat.util.codec.binary.Base64.encodeBase64String;

@SpringBootApplication
public class EmploymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmploymentServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println(
                    encodeBase64String(CreateEmployerRequest.newBuilder()
                            .setId(Id.newBuilder().setValue("123"))
                            .setEmployerName("Large Corp XY")
                            .setSize(EmployerSize.LARGE)
                            .setIsFamilyOwned(true)
                            .build()
                            .toByteArray())
            );

        };
    }

}

