package be.niedel.proto.employmentservice.employer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Employer {

    @Id
    private String id;

    @Column
    private String employerName;

    @Column
    private boolean isFamilyOwned;

    @Column
    private String size;

    protected Employer() {
    }

    public Employer(String id, String employerName, boolean isFamilyOwned, String size) {
        this.id = id;
        this.employerName = employerName;
        this.isFamilyOwned = isFamilyOwned;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public String getEmployerName() {
        return employerName;
    }

    public boolean isFamilyOwned() {
        return isFamilyOwned;
    }

    public String getSize() {
        return size;
    }
}
