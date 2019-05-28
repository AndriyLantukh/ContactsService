package la.test.controller;

import la.test.app.Application;
import la.test.model.Contact;
import la.test.service.ContactsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ControllerTest {

    private static final String BASE_URL = "/hello/contacts";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ContactsService contactsService;

    @Test
    public void getContactsTest() throws Exception {
        given(contactsService.getFilteredContactsFromCache(any())).willReturn(Arrays.asList(new Contact(1, "Vasya")));
        given(contactsService.isPatternValid(any())).willReturn(true);


        mvc.perform(get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .param("nameFilter", "^A.*$")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['page_limit']", is(200)))
                .andExpect(jsonPath("$['pages_total']", is(1)))
                .andExpect(jsonPath("$['page']", is(0)))
                .andExpect(jsonPath("$['contacts'].[0].name", is("Vasya")));
    }

    @Test
    public void getContactsWrongPageTest() throws Exception {
        given(contactsService.getFilteredContactsFromCache(any())).willReturn(Arrays.asList(new Contact(1, "Vasya")));
        given(contactsService.isPatternValid(any())).willReturn(true);

        mvc.perform(get("/hello/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .param("nameFilter", "^A.*$")
                .param("page", "2")

        )
                .andExpect(status().isBadRequest());
    }


}