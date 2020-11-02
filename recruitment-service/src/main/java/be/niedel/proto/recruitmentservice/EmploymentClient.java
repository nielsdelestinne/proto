package be.niedel.proto.recruitmentservice;

import be.niedel.proto.employmentservice.contract.CreateEmployerRequest;
import be.niedel.proto.employmentservice.contract.CreateEmployerResponse;
import com.google.protobuf.InvalidProtocolBufferException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import static be.niedel.proto.employmentservice.contract.CreateEmployerResponse.parseFrom;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.util.Base64.getEncoder;

public class EmploymentClient {

    public static final String EMPLOYMENT_URL = "http://localhost:8080/employer/create";

    private final HttpClient client;

    public EmploymentClient() {
        client = HttpClient.newHttpClient();
    }

    public CompletableFuture<CreateEmployerResponse> createEmployer(CreateEmployerRequest createEmployerRequest) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(EMPLOYMENT_URL))
                .header("Content-Type", "application/octet-stream")
                .POST(ofString(getEncoder().encodeToString(createEmployerRequest.toByteArray())))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(httpResponse -> {
                    try {
                        System.out.println("---\n" + httpResponse.body() + "\n---\n");
                        return parseFrom(httpResponse.body().getBytes());
                    } catch (InvalidProtocolBufferException e) {
                        throw new IllegalArgumentException("Unable to parse: " + e.getMessage());
                    }
                });
    }

}
