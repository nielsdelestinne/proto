package be.niedel.proto.employmentservice.employer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Employer {

    @Id
    private String id;

    @Column
    private String name;

    protected Employer() {
    }

    public Employer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
