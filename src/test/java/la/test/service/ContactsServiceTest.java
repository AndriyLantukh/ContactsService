package la.test.service;

import la.test.app.Application;
import la.test.model.Contact;
import la.test.repository.ContactsRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ContactsServiceTest {

    @MockBean
    private ContactsRepository contactsRepository;

    @Autowired
    private ContactsService contactsService;

    @Test
    public void isPatternValidTest() throws Exception {

        String notStartWithANameFilter = "^A.*$";
        assertTrue(contactsService.isPatternValid(notStartWithANameFilter));

        String notConteinsAeiNameFilter = " ^.*[aei].*$";
        assertTrue(contactsService.isPatternValid(notConteinsAeiNameFilter));

        String nullFilter = null;
        assertFalse(contactsService.isPatternValid(nullFilter));

        String emptyNameFilter = StringUtils.EMPTY;
        assertFalse(contactsService.isPatternValid(emptyNameFilter));

        String notValidNameFilter = "*";
        assertFalse(contactsService.isPatternValid(notValidNameFilter));
    }

    @Test
    public void getFilteredContactsFromCache1Test() throws Exception {
        Contact notStartWithA = new Contact(0, "Vasya");
        given(contactsRepository.getAllContacts()).willReturn(Arrays.asList(new Contact(1, "Ann"), new Contact(2, "Amely"), notStartWithA, new Contact(2, "Anrdew")));

        String notStartWithANameFilter = "^A.*$";

        List<Contact> filteredContactsFromCache = contactsService.getFilteredContactsFromCache(notStartWithANameFilter);

        assertTrue(filteredContactsFromCache.size() == 1);

        assertEquals(notStartWithA.getName(), filteredContactsFromCache.get(0).getName());

    }

    @Test
    public void getFilteredContactsFromCache2Test() throws Exception {

        Contact notContains_aei = new Contact(0, "Ann");

        given(contactsRepository.getAllContacts()).willReturn(Arrays.asList(new Contact(1, "Vasya"), new Contact(2, "Amely"), notContains_aei, new Contact(2, "Anrdew")));

        String notConteinsAeiNameFilter = "^.*[aei].*$";

        List<Contact> filteredContactsFromCache = contactsService.getFilteredContactsFromCache(notConteinsAeiNameFilter);

        assertTrue(filteredContactsFromCache.size() == 1);

        assertEquals(notContains_aei.getName(), filteredContactsFromCache.get(0).getName());
    }

}