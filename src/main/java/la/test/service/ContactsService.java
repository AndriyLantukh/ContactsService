package la.test.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import la.test.model.Contact;
import la.test.repository.ContactsRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Service
public class ContactsService {

    private static final int CACHE_DURATION = 10;
    private static final int CACHE_MAX_SIZE = 20;
    public static final String OPPOSIT_PREFIX = "^";

    private List<Contact> contactsCache;

    private Cache<String, List<Contact>> filteredContactsCache;

    @Autowired
    private ContactsRepository contactsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactsService.class);

    @PostConstruct
    private void postConstruct() {

        filteredContactsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(CACHE_DURATION, TimeUnit.MINUTES)
                .maximumSize(CACHE_MAX_SIZE)
                .build(new CacheLoader<String, List<Contact>>() {
                    @Override
                    public List<Contact> load(final String nameRegex) throws Exception {
                        return getFilteredContacts(nameRegex);
                    }
                });
    }

    public List<Contact> getFilteredContactsFromCache(String nameRegex) {
        try {
            long timeBefore = System.currentTimeMillis();
            List<Contact> contacts = this.filteredContactsCache.get(nameRegex);
            LOGGER.info("Time to get from cache {} contacts = {}", contacts.size(), (System.currentTimeMillis() - timeBefore));
            return contacts;
        } catch (ExecutionException e) {
            LOGGER.error("Cache error", e);
            e.printStackTrace();
            return getFilteredContacts(nameRegex);
        }
    }

    public boolean isPatternValid(String stringRegex) {

        if (StringUtils.isBlank(stringRegex)) {
            return false;
        }

        if (stringRegex.startsWith(OPPOSIT_PREFIX)) {
            stringRegex = stringRegex.substring(1);
        }

        try {
            Pattern.compile(stringRegex);
        } catch (PatternSyntaxException e) {
            return false;
        }
        return true;
    }

    private List<Contact> getFilteredContacts(String stringRegex) {

        boolean isOpposit = stringRegex.startsWith(OPPOSIT_PREFIX);
        String preperedStringRegex = isOpposit ? stringRegex.substring(1) : stringRegex;

        Pattern filter = Pattern.compile(preperedStringRegex);

        List<Contact> contacts = getContacts();

        long timeBefore = System.currentTimeMillis();

        List<Contact> filteredContacts = contacts.parallelStream()
                .filter(contact -> isContactMatch(contact, filter, stringRegex, isOpposit))
                .collect(Collectors.toList());

        LOGGER.info("Time to filter {} contacts = {}", contacts.size(), (System.currentTimeMillis() - timeBefore));

        return filteredContacts;
    }

    private boolean isContactMatch(Contact contact, Pattern filter, String stringRegex, boolean isOpposit) {

        String name = contact.getName();

        if (stringRegex.equals(name)) {
            return false;
        }

        boolean matches = filter.matcher(name).matches();

        if (isOpposit) {
            return !matches;
        }

        return matches;
    }

    private List<Contact> getContacts() {

        long timeBefore = System.currentTimeMillis();


        long count = contactsRepository.getRowsCount();

        if (this.contactsCache == null || count != this.contactsCache.size()) {
            updateContactsCash();
        }

        LOGGER.info("Time to select from db {} contacts = {}", count, (System.currentTimeMillis() - timeBefore));

        return this.contactsCache;
    }

    private void updateContactsCash() {
        this.contactsCache = contactsRepository.getAllContacts();
    }

}
