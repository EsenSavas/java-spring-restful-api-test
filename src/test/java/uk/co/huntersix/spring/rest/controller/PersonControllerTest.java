package uk.co.huntersix.spring.rest.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.huntersix.spring.rest.Exception.PersonAlreadyExistException;
import uk.co.huntersix.spring.rest.Exception.PersonNotFoundException;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonDataService personDataService;

    @Test
    public void shouldReturnPersonFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(new Person("Mary", "Smith"));
        this.mockMvc.perform(get("/person/smith/mary"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("firstName").value("Mary"))
                .andExpect(jsonPath("lastName").value("Smith"));
    }

    @Test
    public void shouldThrownNotFoundError_whenPersonNotExist_givenFirstNameAndLastName() throws Exception {
        when(personDataService.findPerson(any(), any())).thenThrow(new PersonNotFoundException());
        this.mockMvc.perform(get("/person/smith/esen"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnPersonListFromService_givenLastName() throws Exception {
        Person person1 = new Person("name1", "surname");
        Person person2 = new Person("name2", "surname");
        Person person3 = new Person("name3", "surname");

        when(personDataService.findPerson(any())).thenReturn(Arrays.asList(person1, person2, person3));
        this.mockMvc.perform(get("/person/surname"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].firstName", is("name1")))
                .andExpect(jsonPath("$[0].lastName", is("surname")))
                .andExpect(jsonPath("$[1].firstName", is("name2")))
                .andExpect(jsonPath("$[1].lastName", is("surname")))
                .andExpect(jsonPath("$[2].firstName", is("name3")))
                .andExpect(jsonPath("$[2].lastName", is("surname")));
    }

    @Test
    public void shouldReturnPersonFromService_givenLastName() throws Exception {
        Person person1 = new Person("name1", "surname");

        when(personDataService.findPerson(any())).thenReturn(Arrays.asList(person1));
        this.mockMvc.perform(get("/person/surname"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("name1")))
                .andExpect(jsonPath("$[0].lastName", is("surname")));
    }

    @Test
    public void shouldThrownNotFoundError_whenPersonNotExist_givenLastName() throws Exception {
        when(personDataService.findPerson(any(), any())).thenThrow(new PersonNotFoundException("message"));
        this.mockMvc.perform(get("/person/smith/esen"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldAddThePersonToTheList_givenUniqueFirstNameAndLastName() throws Exception {
        Person newPerson = new Person("firstname", "lastname");
        when(personDataService.addPerson(any(Person.class))).thenReturn(newPerson);

        this.mockMvc.perform(post("/person")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/person/" + newPerson.getId()))
                .andExpect(jsonPath("id").value(newPerson.getId()))
                .andExpect(jsonPath("$.firstName", is("firstname")))
                .andExpect(jsonPath("$.lastName", is("lastname")));
    }


    @Test
    public void shouldThrowException_givenDublicateFirstNameAndLastName() throws Exception {

        when(personDataService.addPerson(any(Person.class))).thenThrow(new PersonAlreadyExistException());

        this.mockMvc.perform(post("/person")
                .content("{}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(409));
    }

    @Test
    public void shouldUpdatePersonFirstName_givenId() throws Exception {
        Person updatedPerson = new Person("Esen", "lastname");
        when(personDataService.updatePerson(any(), any())).thenReturn(updatedPerson);

        this.mockMvc.perform(patch("/person/1")
                .content("{\"firstName\" : \"Esen\"}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Esen")));
    }


    @Test
    public void shouldThrowException_whenPersonNotExist_givenId() throws Exception {

        when(personDataService.updatePerson(any(), any())).thenThrow(new PersonNotFoundException());

        this.mockMvc.perform(patch("/person/5")
                .content("{}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    public void shouldDeletePerson_givenId() throws Exception {
        doNothing().when(personDataService).deletePerson(any());

        this.mockMvc.perform(delete("/person/1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldThrowExceptionForDeleteOperation_whenPersonNotFound_givenId() throws Exception {

        doThrow(new PersonNotFoundException()).when(personDataService).deletePerson(any());

        this.mockMvc.perform(delete("/person/5")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404));
    }

}