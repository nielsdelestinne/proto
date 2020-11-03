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
    private String size;

    @Column
    private boolean isFamilyOwned;

    protected Employer() {
    }

    public Employer(String id, String employerName, String size, boolean isFamilyOwned) {
        this.id = id;
        this.employerName = employerName;
        this.size = size;
        this.isFamilyOwned = isFamilyOwned;
    }

    public String getId() {
        return id;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getSize() {
        return size;
    }

    public boolean isFamilyOwned() {
        return isFamilyOwned;
    }
}
