package be.niedel.proto.comparison.deserialization;

public class EmployeeTO {
    public Id id;
    public Name name;
    public Address address;
    public String[] skills;

    public EmployeeTO setId(Id id) {
        this.id = id;
        return this;
    }

    public EmployeeTO setName(Name name) {
        this.name = name;
        return this;
    }

    public EmployeeTO setAddress(Address address) {
        this.address = address;
        return this;
    }

    public EmployeeTO setSkills(String[] skills) {
        this.skills = skills;
        return this;
    }

    public static class Id {
        public String value;

        public Id setValue(String value) {
            this.value = value;
            return this;
        }
    }

    public static class Name {
        public String firstName;
        public String lastName;

        public Name setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Name setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
    }

    public static class Address {
        public String street;
        public String streetNumber;
        public String municipality;
        public String country;

        public Address setStreet(String street) {
            this.street = street;
            return this;
        }

        public Address setStreetNumber(String streetNumber) {
            this.streetNumber = streetNumber;
            return this;
        }

        public Address setMunicipality(String municipality) {
            this.municipality = municipality;
            return this;
        }

        public Address setCountry(String country) {
            this.country = country;
            return this;
        }
    }
}
