package uk.co.huntersix.spring.rest.model;

import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;

public class Person {
    //A static variable stays in the memory for the entire lifetime of the application, and is initialised during class loading.
    //A non-static variable is being initialised each time you construct a new object.If it is only final, it will be always zero for each instance.
    private static final AtomicLong counter = new AtomicLong();

    private Long id;
    private String firstName;
    private String lastName;

    private Person() {
        // empty
    }

    public Person(String firstName, String lastName) {
        this.id = counter.incrementAndGet();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Person.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .toString();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void fillId() {
        if (this.id == null)
            this.id = counter.incrementAndGet();
    }


}
