package la.test.repository.impl;

import la.test.model.Contact;
import la.test.repository.ContactsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

@Transactional
@Repository
public class ContactsRepositoryImpl extends JdbcDaoSupport implements ContactsRepository {

    private static final String SQL_ALL_CONTACTS = "SELECT * FROM test.contacts";
    private static final String SQL_CONTACTS_COUNT = "SELECT COUNT(*) FROM test.contacts";
    private static final String SQL_SAVE_CONTACT = "INSERT INTO test.contacts(name) VALUES(?)";
    private static final String SQL_REMOVE_CONTACT = "DELETE FROM test.contacts WHERE id = ?";

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    @Override
    public List<Contact> getAllContacts() {
        return getJdbcTemplate().query(SQL_ALL_CONTACTS,
                new Object[]{},
                new BeanPropertyRowMapper(Contact.class));

    }

    @Override
    public long getRowsCount() {
        return this.getJdbcTemplate().queryForObject(
                SQL_CONTACTS_COUNT, new Object[]{}, Long.class);
    }

    @Override
    @Transactional
    public void saveContacts(List<Contact> contacts) {

        contacts.stream()
                .forEachOrdered(contact -> {
                    this.getJdbcTemplate().update(SQL_SAVE_CONTACT, contact.getName());
                });

    }

    @Override
    public void deleteContacts(List<Contact> contacts) {
        contacts.stream()
                .forEachOrdered(contact -> {
                    this.getJdbcTemplate().update(SQL_REMOVE_CONTACT, contact.getId());
                });
    }

}

