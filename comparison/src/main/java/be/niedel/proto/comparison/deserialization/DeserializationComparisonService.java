package be.niedel.proto.comparison.deserialization;

import be.niedel.proto.employmentservice.contract.CreateEmployeeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static be.niedel.proto.comparison.ComparisonUtility.*;

public class DeserializationComparisonService {

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2)
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void deserializeJSONString(BenchmarkState state, Blackhole blackhole) throws IOException {
        var result = deserializeJSONString(state, state.getJSONString(), EmployeeTO.class);
        blackhole.consume(result);
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2)
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void deserializeUtf8EncodedBinaryJSONMessage(BenchmarkState state, Blackhole blackhole) throws IOException {
        var result = deserializeUtf8EncodedBinaryJSONMessage(state, state.getUtf8EncodedBytes(), EmployeeTO.class);
        blackhole.consume(result);
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2)
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void deserializeProtoEncodedBinaryMessage(BenchmarkState state, Blackhole blackhole) throws InvalidProtocolBufferException {
        var result = deserializeProtoEncodedBinaryMessage(state.getProtoEncodedBytes());
        blackhole.consume(result);
    }

    private <T> T deserializeJSONString(BenchmarkState state, String jsonString, Class<T> clazz) throws IOException {
        return state.getObjectMapper().readValue(jsonString, clazz);
    }

    private <T> T deserializeUtf8EncodedBinaryJSONMessage(BenchmarkState state, byte[] utf8EncodedJSON, Class<T> clazz) throws IOException {
        return state.getObjectMapper().readValue(utf8EncodedJSON, clazz);
    }

    private CreateEmployeeRequest deserializeProtoEncodedBinaryMessage(byte[] protoEncodedMessage) throws InvalidProtocolBufferException {
        return CreateEmployeeRequest.parseFrom(protoEncodedMessage);
    }

    @State(Scope.Thread)
    public static class BenchmarkState {

        private ObjectMapper objectMapper;
        private byte[] utf8EncodedBytes;
        private byte[] protoEncodedBytes;
        private String utf8EncodedJsonString;

        @Setup(Level.Trial)
        public void setup() {
            CreateEmployeeRequest employeeRequest = createEmployeeRequest("Some Skill", 10);
            this.utf8EncodedJsonString = toJSON(employeeRequest);
            this.utf8EncodedBytes = encodeFromStringToBytesUsingUtf8(utf8EncodedJsonString);
            this.protoEncodedBytes = encodeFromProtoGeneratedMessageToBytesUsingProtoEncoding(employeeRequest);
            this.objectMapper = new ObjectMapper();
        }

        public byte[] getUtf8EncodedBytes() {
            return utf8EncodedBytes;
        }

        public byte[] getProtoEncodedBytes() {
            return protoEncodedBytes;
        }

        public String getJSONString() {
            return utf8EncodedJsonString;
        }

        public ObjectMapper getObjectMapper() {
            return objectMapper;
        }
    }

}
