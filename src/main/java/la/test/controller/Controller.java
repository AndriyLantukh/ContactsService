package la.test.controller;

import la.test.exception.ContactsServiceException;
import la.test.model.Contact;
import la.test.service.ContactsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/hello")
public class Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    private static final String PAGE_LIMIT = "200";
    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String CONTACTS_KEY = "contacts";
    private static final String PAGES_TOTAL_KEY = "pages_total";
    private static final String PAGE_KEY = "page";
    private static final String PAGE_LIMIT_KEY = "page_limit";


    @Autowired
    private ContactsService contactsService;


    @GetMapping("/contacts")
    public ResponseEntity<Map<String, Object>> getContacts(@RequestParam("nameFilter") String nameRegex,
                                                           @RequestParam(value = "pageLimit", required = false, defaultValue = PAGE_LIMIT) String pageLimitParam,
                                                           @RequestParam(value = "page", required = false, defaultValue = DEFAULT_PAGE_NUMBER) String pageParam) {
        if (!contactsService.isPatternValid(nameRegex)) {
            LOGGER.error("Wrong regular expression for nameFilter {}", nameRegex);
            throw new ContactsServiceException("Wrong regular expression for nameFilter", HttpStatus.BAD_REQUEST);
        }

        List<Contact> filteredContacts = contactsService.getFilteredContactsFromCache(nameRegex);

        int pageLimit = StringUtils.isNumeric(pageLimitParam) ? Integer.valueOf(pageLimitParam) : Integer.valueOf(PAGE_LIMIT);
        int page = StringUtils.isNumeric(pageParam) ? Integer.valueOf(pageParam) : Integer.valueOf(DEFAULT_PAGE_NUMBER);

        int pagesTotal = filteredContacts.size() / pageLimit;
        if(pagesTotal == 0 || filteredContacts.size() % pageLimit != 0) {
            pagesTotal++;
        }

        if (page >= pagesTotal) {
            LOGGER.error("Wrong page number {} total pages {}", page, pagesTotal);
            throw new ContactsServiceException("Wrong page number", HttpStatus.BAD_REQUEST);
        }

        int startIndex = page * pageLimit;
        int endIndexCandidat = startIndex + pageLimit;
        int endIndex = endIndexCandidat > filteredContacts.size() ? filteredContacts.size() : endIndexCandidat;

        List<Contact> onePageContacts = filteredContacts.subList(startIndex, endIndex);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(CONTACTS_KEY, onePageContacts);
        responseMap.put(PAGES_TOTAL_KEY, pagesTotal);
        responseMap.put(PAGE_KEY, page);
        responseMap.put(PAGE_LIMIT_KEY, pageLimit);

        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }
}

