package la.test.repository;

import la.test.model.Contact;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactsRepository {

    List<Contact> getAllContacts();

    long getRowsCount();

    void saveContacts(List<Contact> contacts);

    void deleteContacts(List<Contact> contacts);


}

