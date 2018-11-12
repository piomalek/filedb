package ch.pmalek.filedb.model;

import java.util.List;

/*
 * Given the interface:
 *
 * Provide an implementation which will allow multiple users to insert/read/modify the data to/from 
 * the database.
 * 
 * The data should be stored in a file per table in an appropriate format (format is not of 
 * material importance, text based formats like CSV are acceptable).
 * 
 * The server can be, for example, implemented using Spring Boot: https://spring.io/guides/gs/spring-boot/
 *
 * The system shall expose the corresponding operations via a standard REST/Http based 
 * interface: to insert, select and modify the data.
 */

public interface Database {
    // returns the id of the newly inserted row or throws an exception in case of error
    // once an id is returned, the data is guaranteed to be retrievable via subsequent select calls
    public int insert(String tableName, List<String> values);

    // updates the data associated with the list
    // returns true if the data has changed, or throws an exception in case of error
    public boolean update(String tableName, List<String> values, int id);

    // retrieves the data associated to the id (as previously stored/updated),
    // or throws an exception in case of error or missing data
    public List<String> select(String tableName, int id);
}
