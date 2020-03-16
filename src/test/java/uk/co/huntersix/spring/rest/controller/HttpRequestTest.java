package uk.co.huntersix.spring.rest.controller;

import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.huntersix.spring.rest.model.Person;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;


    public MockMvc getMockMvc() {
        if (mockMvc == null)
            return mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        return mockMvc;
    }

    @Test
    public void shouldReturnPersonDetails() {
        assertThat(
                this.restTemplate.getForObject(
                        getBaseURI() + "/person/smith/mary",
                        String.class
                )
        ).contains("Mary");
    }

    @Test
    public void shouldThrownNotFoundError_whenPersonNotFound_givenFirstNameAndLastName() {
        assertThat(
                this.restTemplate.getForEntity(
                        getBaseURI() + "/person/smith/mary2",
                        String.class
                ).getStatusCode()
        ).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    public void shouldReturnPersonList_whenPersonFound_givenLastName() {
        ResponseEntity<Person[]> resp = this.restTemplate.getForEntity(getBaseURI() + "/person/smith", Person[].class);
        Assertions.assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(resp.getBody().length).isEqualTo(1);
        Assertions.assertThat(resp.getBody()[0].getLastName()).isEqualTo("Smith");
    }

    @Test
    public void shouldThrownNotFoundError_whenPersonNotFound_givenLastName() {
        Assertions.assertThat(this.restTemplate.getForEntity(getBaseURI() + "/person/smith2", Person.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldThrown409_whenPersonAlreadyExist_givenFirstNameAndLastName() throws Exception {
        JSONObject request = new JSONObject();
        request.put("firstName", "Brian");
        request.put("lastName", "Archer");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(getBaseURI() + "/person")
                .accept(MediaType.APPLICATION_JSON)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = getMockMvc().perform(requestBuilder).andReturn();

        Assertions.assertThat(result.getResponse().getStatus()).isEqualTo(409);
    }

    @Test
    public void shouldAddPersonToTheList_whenPersonNotExist_givenUniqueNameAndSurname() throws Exception {
        JSONObject request = new JSONObject();
        request.put("firstName", "firstName");
        request.put("lastName", "lastName");

        MvcResult result = getMockMvc().perform(MockMvcRequestBuilders
                .post(getBaseURI() + "/person")
                .accept(MediaType.APPLICATION_JSON)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());

        Assertions.assertThat(obj.getString("id")).isNotBlank();
        Assertions.assertThat(obj.getString("firstName")).isEqualToIgnoringCase("firstName");
        Assertions.assertThat(obj.getString("lastName")).isEqualToIgnoringCase("lastName");
    }


    @Test
    public void shouldUpdateThePersonDetail_whenPersonIsExist_givenIdAndFirstName() throws Exception {
        JSONObject request = new JSONObject();
        request.put("firstName", "firstName");

        MvcResult result = getMockMvc().perform(MockMvcRequestBuilders
                .patch(getBaseURI() + "/person/1")
                .accept(MediaType.APPLICATION_JSON)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());

        Assertions.assertThat(obj.getLong("id")).isEqualTo(1);
        Assertions.assertThat(obj.getString("firstName")).isEqualToIgnoringCase("firstName");
        Assertions.assertThat(obj.getString("lastName")).isEqualToIgnoringCase("Smith");

    }

    @Test
    public void shouldThrownNotFoundError_whenPersonNotFound_ForGivenId() throws Exception {
        JSONObject request = new JSONObject();
        request.put("firstName", "firstName");

        Assertions.assertThat(getMockMvc().perform(MockMvcRequestBuilders
                .patch(getBaseURI() + "/person/5")
                .accept(MediaType.APPLICATION_JSON)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)).andReturn()
                .getResponse()
                .getStatus()).isEqualTo(404);

    }


    @Test
    public void shouldThrownNotFoundErrorForDeleteOperation_whenPersonNotFound_givenId() throws Exception {
        Assertions.assertThat(getMockMvc().perform(MockMvcRequestBuilders
                .delete(getBaseURI() + "/person/10")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andReturn()
                .getResponse()
                .getStatus()).isEqualTo(404);
    }

    @Test
    public void shouldReturn204ForDeleteOperation_whenPersonIsDeleted_givenId() throws Exception {
        Assertions.assertThat(getMockMvc().perform(MockMvcRequestBuilders
                .delete(getBaseURI() + "/person/3")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andReturn()
                .getResponse()
                .getStatus()).isEqualTo(204);
    }

    public String getBaseURI() {
        return "http://localhost:" + port;
    }

}