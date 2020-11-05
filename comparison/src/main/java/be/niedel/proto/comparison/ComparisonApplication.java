package be.niedel.proto.comparison;

import java.util.Arrays;

public class ComparisonApplication {

    public static void main(String[] args) {
        var comparisonService = new SizeComparisonService();
        var compressedSizeComparisonService = new CompressedSizeComparisonService();
        var employerRequest = comparisonService.createEmployerRequest();

        byte[] encodeFromStringToBytesUsingUtf8 = comparisonService.encodeFromStringToBytesUsingUtf8(employerRequest.toString());
        byte[] encodeFromProtoGeneratedObjectToBytesUsingProtoEncoding = comparisonService.encodeFromProtoGeneratedObjectToBytesUsingProtoEncoding(employerRequest);
        byte[] encodeFromJSONToBytesUsingUtf8 = comparisonService.encodeFromStringToBytesUsingUtf8(comparisonService.toJSON(employerRequest));

        displayResults(
                encodeFromStringToBytesUsingUtf8,
                employerRequest.toString(),
                "(A) Proto-generated object (toString) encoded to bytes using UTF-8");
        System.out.println("Compressed size (GZIP): " + compressedSizeComparisonService.compressUsingGzip(encodeFromStringToBytesUsingUtf8).length);

        displayResults(
                encodeFromProtoGeneratedObjectToBytesUsingProtoEncoding,
                new String(employerRequest.toByteArray()),
                "(B) Proto-generated object encoded to bytes using Protobuf encoding");
        System.out.println("Compressed size (GZIP): " + compressedSizeComparisonService.compressUsingGzip(encodeFromProtoGeneratedObjectToBytesUsingProtoEncoding).length);

        displayResults(
                encodeFromJSONToBytesUsingUtf8,
                comparisonService.toJSON(employerRequest),
                "(C) JSON (text) encoded to bytes using UTF-8 encoding");
        System.out.println("Compressed size (GZIP): " + compressedSizeComparisonService.compressUsingGzip(encodeFromJSONToBytesUsingUtf8).length);

    }

    public static void displayResults(byte[] bytes, String employerRequest, String message) {
        System.out.println("\n------------------------\n------------------------");
        System.out.println(message);
        System.out.println("------------------------");
        System.out.println(employerRequest);
        System.out.println("\nSize: " + bytes.length + " -> " + Arrays.toString(bytes));
        System.out.println("------------------------");
    }

}
