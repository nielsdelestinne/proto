package be.niedel.proto.comparison;

import be.niedel.proto.employmentservice.contract.*;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static be.niedel.proto.employmentservice.contract.EmployerSize.MEDIUM;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SizeAndCompressionComparisonUtility {

    public static byte[] encodeFromStringToBytesUsingUtf8(String objectToString) {
        return objectToString.getBytes(UTF_8);
    }

    public static byte[] encodeFromProtoGeneratedMessageToBytesUsingProtoEncoding(GeneratedMessageV3 protoGeneratedMessage) {
        return protoGeneratedMessage.toByteArray();
    }

    public static String toJSON(GeneratedMessageV3 protoGeneratedMessage) {
        try {
            return JsonFormat.printer().print(protoGeneratedMessage);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    public static CreateEmployerRequest createEmployerRequest() {
        return CreateEmployerRequest.newBuilder()
                .setId(Id.newBuilder().setValue("123"))
                .setEmployerName("Building Corporation Y")
                .setIsFamilyOwned(true)
                .setSize(MEDIUM)
                .build();

    }

    public static CreateEmployeeRequest createEmployeeRequest(String repeatedValue, int amountOfDuplicationIterations) {
        return CreateEmployeeRequest.newBuilder()
                .setId(Id.newBuilder().setValue("456"))
                .setAddress(Address.newBuilder()
                        .setStreet("Broadway")
                        .setStreetNumber("10A")
                        .setMunicipality("Broadville")
                        .setCountry("Broadland")
                        .build())
                .setName(Name.newBuilder().setFirstName("Billy").setLastName("Bobson").build())
                .addAllSkills(IntStream.range(0, amountOfDuplicationIterations)
                        .mapToObj(index -> repeatedValue + index)
                        .collect(Collectors.toList())).build();
    }

    public static byte[] compressUsingGzip(byte[] uncompressedData) {
        byte[] compressedData = new byte[]{};
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(uncompressedData.length);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteStream)) {
            gzipOutputStream.write(uncompressedData);
            gzipOutputStream.close();
            compressedData = byteStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedData;
    }

    public static byte[] unCompress(byte[] gzipCompressedData) {
        byte[] uncompressedData = new byte[]{};
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(gzipCompressedData);
             ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteInputStream)) {
            byte[] buffer = new byte[1024];
            int bytesLength;
            while ((bytesLength = gzipInputStream.read(buffer)) != -1) {
                byteOutputStream.write(buffer, 0, bytesLength);
            }
            uncompressedData = byteOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uncompressedData;
    }

}
