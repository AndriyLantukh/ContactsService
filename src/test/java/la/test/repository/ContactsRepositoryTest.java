package la.test.repository;

import la.test.app.Application;
import la.test.model.Contact;
import la.test.service.ContactsService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ContactsRepositoryTest {

    private final Logger LOGGER = LoggerFactory.getLogger(ContactsRepositoryTest.class);

    private static final int MIN_RANDOM_NAME_LENGTH = 3;
    private static final int MAX_RANDOM_NAME_LENGTH = 15;
    private static final Random random = new Random();

    @Autowired
    private ContactsRepository contactsRepository;

    @Before
    public void clearContacts() {
        //comment it if you want to fill DB
        contactsRepository.deleteContacts(contactsRepository.getAllContacts());
    }

    @Test
    public void countTest() {

        int addCount = 100;

        List<Contact> contactsWithRandomNames = createContactsWithRandomNames(addCount);

        long timeBefore = System.currentTimeMillis();

        contactsRepository.saveContacts(contactsWithRandomNames);

        LOGGER.info("Time to insert: " + (System.currentTimeMillis() - timeBefore));

        long countAfter = contactsRepository.getRowsCount();

        assertEquals(addCount, countAfter);
    }

    @Test
    public void saveAndGetAllTest() {

        int addCount = 1000;

        List<Contact> contactsWithRandomNames = createContactsWithRandomNames(addCount);

        contactsRepository.saveContacts(contactsWithRandomNames);

        List<Contact> allContacts = contactsRepository.getAllContacts();

        List<String> contactNamesFromDb = allContacts.stream()
                .map(contact -> contact.getName())
                .sorted()
                .collect(Collectors.toList());

        List<String> contactNamesToDb = contactsWithRandomNames.stream()
                .map(contact -> contact.getName())
                .sorted()
                .collect(Collectors.toList());


        assertEquals(contactNamesToDb, contactNamesFromDb);
    }


    @After
    public void clearContactsAfter() {
        //comment it if you want to fill DB
        contactsRepository.deleteContacts(contactsRepository.getAllContacts());
    }

    public List<Contact> createContactsWithRandomNames(int contactsCount) {

        List<Contact> resultList = new ArrayList<>();
        for (int i = 0; i < contactsCount; i++) {
            resultList.add(createContactWithRandomName());
        }
        return resultList;
    }

    public Contact createContactWithRandomName() {
        return new Contact(RandomStringUtils.random(generateRandomLength(MIN_RANDOM_NAME_LENGTH, MAX_RANDOM_NAME_LENGTH), true, false));
    }

    private int generateRandomLength(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }


}
