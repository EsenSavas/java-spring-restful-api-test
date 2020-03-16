package uk.co.huntersix.spring.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import uk.co.huntersix.spring.rest.model.PatchRequest;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class PersonController {
    private PersonDataService personDataService;

    public PersonController(@Autowired PersonDataService personDataService) {
        this.personDataService = personDataService;
    }

    @GetMapping("/person/{lastName}/{firstName}")
    @ResponseStatus(HttpStatus.OK)
    public Person person(@PathVariable(value = "lastName") String lastName,
                         @PathVariable(value = "firstName") String firstName) {

        return personDataService.findPerson(lastName, firstName);
    }

    @GetMapping("/person/{lastName}")
    @ResponseStatus(HttpStatus.OK)
    public List<Person> person(@PathVariable(value = "lastName") String lastName) {

        return personDataService.findPerson(lastName);
    }

    @PostMapping("/person")
    @ResponseStatus(HttpStatus.CREATED)
    public Person addPerson(@RequestBody Person person,
                            HttpServletResponse httpResponse,
                            WebRequest request) {
        Person person1 = personDataService.addPerson(person);
        httpResponse.setHeader("Location", String.format("%s/person/%s",
                request.getContextPath(), person1.getId()));
        return person1;
    }

    @PatchMapping("/person/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Person partialUpdatePerson(@PathVariable(value = "id") Long id, @RequestBody PatchRequest request) {
        return personDataService.updatePerson(id, request.getFirstName());
    }

    @DeleteMapping("/person/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable(value = "id") Long id) {
        personDataService.deletePerson(id);
    }

}