package be.niedel.proto.comparison;

import be.niedel.proto.employmentservice.contract.CreateEmployerRequest;
import com.google.protobuf.GeneratedMessageV3;

import static be.niedel.proto.comparison.SizeAndCompressionComparisonUtility.*;

public class ComparisonApplication {

    public static void main(String[] args) {

        System.out.println("===============================\nMessage with zero duplication (outliner)\n===============================");
        compressionComparison(createEmployerRequest());
        System.out.println("\n===============================\nMessage with minimal duplication\n===============================");
        compressionComparison(createEmployeeRequest("", 1));
        System.out.println("\n===============================\nMessage with high duplication\n===============================");
        compressionComparison(createEmployeeRequest("", 1000));
        System.out.println("\n===============================\nMessage with majority of data being duplication (outliner)\n===============================");
        compressionComparison(createEmployeeRequest("duplication duplication", 1000));

        if (args != null && args.length >= 1 && "printExamples".equals(args[0])) {
            printExamples();
        }
    }

    private static void printExamples() {
        System.out.println("\n-------------------\n");

        CreateEmployerRequest employerRequest = createEmployerRequest();
        System.out.println("(A) Proto-generated object (toString) encoded to bytes using UTF-8");
        System.out.println(employerRequest.toString());
        System.out.println("(B) Proto-generated object encoded to bytes using Protobuf encoding");
        System.out.println(new String(employerRequest.toByteArray()));
        System.out.println("(C) JSON (text) encoded to bytes using UTF-8 encoding");
        System.out.println(toJSON(employerRequest));
    }

    private static void compressionComparison(GeneratedMessageV3 message) {
        byte[] protoMessageEmployee = encodeFromProtoGeneratedMessageToBytesUsingProtoEncoding(message);
        byte[] jsonMessageEmployee = encodeFromStringToBytesUsingUtf8(toJSON(message));
        System.out.println(String.format("Proto encoded - Size (uncompressed):\t\t\t %d bytes", protoMessageEmployee.length));
        System.out.println(String.format("Proto encoded - Size (gzip):\t\t\t\t\t %d bytes", compressUsingGzip(protoMessageEmployee).length));
        printSizeDifferenceFactor("uncompressed", "gzip", protoMessageEmployee.length * 1.0 / compressUsingGzip(protoMessageEmployee).length);

        System.out.println(String.format("JSON (UTF-8) encoded - Size (uncompressed):\t\t %d bytes", jsonMessageEmployee.length));
        System.out.println(String.format("JSON (UTF-8) encoded - Size (gzip):\t\t\t\t %d bytes", compressUsingGzip(jsonMessageEmployee).length));
        printSizeDifferenceFactor("uncompressed", "gzip", jsonMessageEmployee.length * 1.0 / compressUsingGzip(jsonMessageEmployee).length);

        printSizeDifferenceFactor("JSON (uncompressed)", "Proto (uncompressed)", jsonMessageEmployee.length * 1.0 / protoMessageEmployee.length);
        printSizeDifferenceFactor("JSON (gzip)", "Proto (gzip)", compressUsingGzip(jsonMessageEmployee).length * 1.0 / compressUsingGzip(protoMessageEmployee).length);
    }

    private static void printSizeDifferenceFactor(String bigger, String smaller, double compressionFactor) {
        String message = String.format("Size difference factor:\t\t\t\t\t\t\t %s is %.3f times smaller than %s", smaller, compressionFactor, bigger);
        if ((compressionFactor < 1)) {
            System.out.println(message + " (!!)");
        } else {
            System.out.println(message);
        }
    }

}
