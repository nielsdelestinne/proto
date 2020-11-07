package be.niedel.proto.comparison.serialization;

import be.niedel.proto.comparison.deserialization.EmployeeTO;
import be.niedel.proto.employmentservice.contract.CreateEmployeeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.util.JsonFormat;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static be.niedel.proto.comparison.ComparisonUtility.createEmployeeRequest;

public class SerializationComparisonService {

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2)
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void serializeToJSONStringUsingJackson(BenchmarkState state, Blackhole blackhole) throws IOException {
        String result = state.getObjectMapper().writeValueAsString(state.getEmployeeForJackson());
        blackhole.consume(result);
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2)
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void serializeToJSONStringUsingProtoJsonPrinter(BenchmarkState state, Blackhole blackhole) throws IOException {
        String result = state.getJsonPrinter().print(state.getEmployeeForProto());
        blackhole.consume(result);
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2)
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void serializeToJSONUtf8EncodedBytesUsingJackson(BenchmarkState state, Blackhole blackhole) throws IOException {
        byte[] result = state.getObjectMapper().writeValueAsBytes(state.getEmployeeForJackson());
        blackhole.consume(result);
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2)
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void serializeToProtoEncodedBytes(BenchmarkState state, Blackhole blackhole) {
        byte[] result = state.getEmployeeForProto().toByteArray();
        blackhole.consume(result);
    }

    @State(Scope.Thread)
    public static class BenchmarkState {

        private ObjectMapper objectMapper;
        private JsonFormat.Printer jsonPrinter;
        private CreateEmployeeRequest employeeForProto;
        private EmployeeTO employeeForJackson;

        @Setup(Level.Trial)
        public void setup() {
            this.employeeForProto = createEmployeeRequest("Some Skill", 3);
            this.employeeForJackson = createEmployeeTO();
            this.jsonPrinter = JsonFormat.printer();
            this.objectMapper = new ObjectMapper();
        }

        private EmployeeTO createEmployeeTO() {
            return new EmployeeTO()
                    .setId(new EmployeeTO.Id().setValue("456"))
                    .setAddress(new EmployeeTO.Address()
                            .setStreet("Broadway")
                            .setStreetNumber("10A")
                            .setMunicipality("Broadville")
                            .setCountry("Broadland")
                    )
                    .setName(new EmployeeTO.Name()
                            .setFirstName("Billy")
                            .setLastName("Bobson")
                    )
                    .setSkills(new String[]{"Some Skill 0", "Some Skill 1", "Some Skill 2"});
        }

        public CreateEmployeeRequest getEmployeeForProto() {
            return employeeForProto;
        }

        public EmployeeTO getEmployeeForJackson() {
            return employeeForJackson;
        }

        public JsonFormat.Printer getJsonPrinter() {
            return jsonPrinter;
        }

        public ObjectMapper getObjectMapper() {
            return objectMapper;
        }
    }

}
