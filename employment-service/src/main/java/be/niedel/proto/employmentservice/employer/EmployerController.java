package be.niedel.proto.employmentservice.employer;

import be.niedel.proto.employmentservice.contract.CreateEmployerRequest;
import be.niedel.proto.employmentservice.contract.CreateEmployerResponse;
import be.niedel.proto.employmentservice.contract.Id;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping(path = "employer")
public class EmployerController {

    private final EmployerRepository employerRepository;

    public EmployerController(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    @PostMapping(path = "create")
    public ResponseEntity<byte[]> createEmployer(@RequestBody String createEmployerRequestAsBase64) throws InvalidProtocolBufferException {
        CreateEmployerRequest createEmployerRequest = CreateEmployerRequest.parseFrom(decodeBase64(createEmployerRequestAsBase64));

        var employer = employerRepository.save(
                new Employer(
                        createEmployerRequest.getId().getValue(),
                        createEmployerRequest.getName()));

        return new ResponseEntity<>(
                CreateEmployerResponse.newBuilder()
                        .setId(Id.newBuilder().setValue(employer.getId()))
                        .build()
                        .toByteArray(),
                OK);
    }

}
