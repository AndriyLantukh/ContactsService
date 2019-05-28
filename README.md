# ContactsService
##Contacts test project

### DB Postgress
1. Create database "contactsdb", owner "postgres", password "postgres"
   or edit application.yml to connect with existing DB.
2. create db schema "test"
3. create table "contacts" "CREATE TABLE test.contacts(
                                id bigserial primary key,
                            	name VARCHAR NOT NULL
                            );"
                            
Run application as standard Spring boot application 
../ContactsService/src/main/java/la/test/app/Application.java                            
                            
note: You can use ContactsRepositoryTest to create contacts 
with random names in DB. 
Comment "Before" and "After" and change "addCount". 
For example addCount=1000000.                            
