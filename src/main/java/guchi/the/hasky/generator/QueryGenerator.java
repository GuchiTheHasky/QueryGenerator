package guchi.the.hasky.generator;

import java.io.Serializable;

public interface QueryGenerator {
    /**
     * Takes Entity.class as input
     * Returns SQL command
     * SELECT declared fields FROM Table.name()
     */
    String findAll(Class<?> type);

    /**
     * Takes Entity.class and Serializable id as input;
     * Returns SQL command;
     * SELECT declared fields FROM Table.name() WHERE id = Entity.getId();
     */

    String findById(Class<?> type, Serializable id);

    /**
     * Takes Entity.class and Serializable id as input;
     * Returns SQL command;
     * DELETE FROM Table.name() WHERE id = id;
     */

    String deleteById(Class<?> type, Serializable id);
    /** Takes Object value;
     * Return SQL command;
     * INSERT INTO table name (declared fields) VALUES (fields values);*/

    String insert(Object value);

    String update(Object value);
}
