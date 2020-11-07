package be.niedel.proto.comparison.deserialization;

public class EmployeeTO {
    public Id id;
    public Name name;
    public Address address;
    public String[] skills;

    private static class Id {
        public String value;
    }

    private static class Name {
        public String firstName;
        public String lastName;
    }

    private static class Address {
        public String street;
        public String streetNumber;
        public String municipality;
        public String country;
    }
}
