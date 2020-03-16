package uk.co.huntersix.spring.rest.referencedata;

import org.springframework.stereotype.Service;
import uk.co.huntersix.spring.rest.Exception.PersonAlreadyExistException;
import uk.co.huntersix.spring.rest.Exception.PersonNotFoundException;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonDataService {
    //Arrays.asList always returns fixed size array and cant add more data to this list. For this reason, used  new ArrayList<>
    public static final List<Person> PERSON_DATA = new ArrayList<>(Arrays.asList(
            new Person("Mary", "Smith"),
            new Person("Brian", "Archer"),
            new Person("Collin", "Brown")
    )
    );

    public Person findPerson(String lastName, String firstName) {
        List<Person> people = PERSON_DATA.stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(firstName)
                        && p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
        if (people.isEmpty()) {
            throw new PersonNotFoundException("Person with given name and surname is not found");
        }
        return people.get(0);
    }

    public List<Person> findPerson(String lastName) {
        List<Person> people = PERSON_DATA.stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
        if (people.isEmpty()) {
            throw new PersonNotFoundException("Person with surname is not found");
        }
        return people;
    }

    public Person updatePerson(Long id, String firstName) {
        Person person = PERSON_DATA.stream()
                .filter(p -> p.getId().equals(id))
                .findAny()
                .orElse(null);
        if (person == null) {
            throw new PersonNotFoundException("Person with given id is not found");
        }
        person.setFirstName(firstName);
        return person;
    }

    public void deletePerson(Long id) {
        boolean deleted = PERSON_DATA.removeIf(p -> p.getId().equals(id));

        if (!deleted) {
            throw new PersonNotFoundException("Person with given id is not found");
        }
    }

    public Person addPerson(Person person) throws PersonAlreadyExistException {
        if (isPersonExist(person))
            throw new PersonAlreadyExistException("Person with given firstName and lastName is already exist");
        person.fillId();
        PERSON_DATA.add(person);
        return person;
    }

    private boolean isPersonExist(Person person) {
        try {
            if (person == null)
                return false;

            findPerson(person.getLastName(), person.getFirstName());
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }


}
