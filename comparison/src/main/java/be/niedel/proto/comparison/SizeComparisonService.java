package be.niedel.proto.comparison;

import be.niedel.proto.employmentservice.contract.CreateEmployerRequest;
import be.niedel.proto.employmentservice.contract.Id;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import static be.niedel.proto.employmentservice.contract.EmployerSize.MEDIUM;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SizeComparisonService {

    public byte[] encodeFromStringToBytesUsingUtf8(String employer) {
        return employer.getBytes(UTF_8);
    }

    public byte[] encodeFromProtoGeneratedObjectToBytesUsingProtoEncoding(CreateEmployerRequest employer) {
        return employer.toByteArray();
    }

    public String toJSON(CreateEmployerRequest employer) {
        try {
            return JsonFormat.printer().print(employer);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    public CreateEmployerRequest createEmployerRequest() {
        return CreateEmployerRequest.newBuilder()
                .setId(Id.newBuilder().setValue("123"))
                .setEmployerName("Building Corporation Y")
                .setIsFamilyOwned(true)
                .setSize(MEDIUM)
                .build();

    }

}
