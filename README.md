# ContactsService
Contacts test project

DB Postgress
1. Create database "contactsdb", owner "postgres", password "postgres"
   or edit application.yml to connect with existing DB.
2. create db schema "test"
3. create table "contacts" "CREATE TABLE test.contacts(
                                id bigserial primary key,
                            	name VARCHAR NOT NULL
                            );"
                            
note: You can use ContactsRepositoryTest to create contacts 
with random names in DB.                             
