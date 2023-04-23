package guchi.the.hasky.generator;

import java.io.Serializable;

public interface QueryGenerator {
    /**
     * Takes Entity class as input.
     * Returns SQL command.
     * Generates a SELECT query to retrieve all rows from the table associated with the Entity class.
     *
     * @param type The Class object representing the Entity class.
     * @return The SQL SELECT query as a String.
     */
    String findAll(Class<?> type);

    /**
     * Takes Entity class and Serializable id as input.
     * Returns SQL command.
     * Generates a SELECT query to retrieve a row from the table associated with the Entity class,
     * with a specific id value.
     *
     * @param type The Class object representing the Entity class.
     * @param id   The id value of the row to retrieve.
     * @return The SQL SELECT query as a String.
     */

    String findById(Class<?> type, Serializable id);

    /**
     * Takes Entity class and Serializable id as input.
     * Returns SQL command.
     * Generates a DELETE query to delete a row from the table associated with the Entity class,
     * with a specific id value.
     *
     * @param type The Class object representing the Entity class.
     * @param id   The id value of the row to delete.
     * @return The SQL DELETE query as a String.
     */

    String deleteById(Class<?> type, Serializable id);

    /**
     * Takes an Object value.
     * Returns SQL command.
     * Generates an INSERT query to insert a new row into the table associated with the Entity class,
     * with values from the fields of the Object value.
     *
     * @param value The Object representing the data to be inserted.
     * @return The SQL INSERT query as a String.
     */
    String insert(Object value);

    /**
     * Takes an Object value.
     * Returns SQL command.
     * Generates an UPDATE query to update a row in the table associated with the Entity class,
     * with values from the fields of the Object value.
     *
     * @param value The Object representing the data to be updated.
     * @return The SQL UPDATE query as a String.
     */
    String update(Object value);
}
