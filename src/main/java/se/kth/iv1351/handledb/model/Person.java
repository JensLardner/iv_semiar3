package se.kth.iv1351.handledb.model;


public class Person {
    private final String employmentId;
    private final String firstName;
    private final String lastName;
    private final String personalNumber;

    public Person(String employmentId, String firstName, String lastName, String personalNumber) {
        this.employmentId = employmentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalNumber = personalNumber;
    }

    public String getEmploymentId() {
        return employmentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

}

