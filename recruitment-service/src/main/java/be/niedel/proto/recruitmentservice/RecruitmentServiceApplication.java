package be.niedel.proto.recruitmentservice;

import be.niedel.proto.employmentservice.contract.CreateEmployerRequest;
import be.niedel.proto.employmentservice.contract.EmployerSize;
import be.niedel.proto.employmentservice.contract.Id;

import java.util.Arrays;

public class RecruitmentServiceApplication {

    public static void main(String[] args) {
        EmploymentClient employmentClient = new EmploymentClient();

        employmentClient.createEmployer(
                CreateEmployerRequest.newBuilder()
                        .setId(Id.newBuilder().setValue("123"))
                        .setEmployerName("Building Corporation Y")
                        .setIsFamilyOwned(true)
                        .setSize(EmployerSize.MEDIUM)
                        .build())
                .thenAccept(response -> {
                    System.out.println(response);
                    System.out.println(Arrays.toString(response.toByteArray()));
                }).join();
    }

}

