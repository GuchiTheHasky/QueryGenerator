package guchi.the.hasky.generator;

import guchi.the.hasky.generator.entities.AnotherEntity;
import guchi.the.hasky.generator.entities.Person;
import guchi.the.hasky.generator.entities.NotORMEntity;
import guchi.the.hasky.generator.entities.SomeEntity;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class DefaultQueryGeneratorTest {
    private final DefaultQueryGenerator generator = new DefaultQueryGenerator();

    /**
     * SELECT column names FROM table_name;
     */
    @Test
    @DisplayName("Test, generate query select current columns from table check expected & actual value.")
    public void testGenerateQuerySelectColumnsFromTable() {
        String expected = "SELECT person_id, person_name, person_salary FROM People";
        String actual = generator.findAll(Person.class);
        assertEquals(expected, actual);
    }

    /**
     * SELECT column names FROM table_name WHERE id=?;
     */
    @Test
    @DisplayName("Test, generate query find person by id from table check expected & actual value.")
    public void testGenerateQueryFindEntityById() {
        String expected = "SELECT person_id, person_name, person_salary FROM People WHERE id=123";
        String actual = generator.findById(Person.class, "123");
        assertEquals(expected, actual);
    }

    /**
     * DELETE FROM table_name WHERE id=?;
     */
    @Test
    @DisplayName("Test, generate query delete entity by id from class check expected & actual value;")
    public void testGenerateQueryDeleteEntityById() {
        String expected = "DELETE FROM People WHERE id=123";
        String actual = generator.deleteById(Person.class, "123");
        assertEquals(expected, actual);
    }

    /**
     * INSERT INTO table_name (column_names) VALUES (column_values);
     */
    @Test
    @DisplayName("Test, generate insert object check expected & actual value.")
    public void testGenerateQueryInsertEntityInTable() {
        Person person = new Person(111, "Obi Van Kenobi", 12000);
        String expected = "INSERT INTO People (person_id, person_name, person_salary) " +
                "VALUES (111, 'Obi Van Kenobi', 12000.0)";
        String actual = generator.insert(person);
        assertEquals(expected, actual);
    }

    /**
     * UPDATE table_name SET column = value1,... WHERE id=?;
     */
    @Test
    @DisplayName("Test, generate update object check expected & actual value.")
    public void testGenerateQueryUpdateEntityById() {
        Person person = new Person(777, "Obi Van Kenobi", 12000);
        String expected = "UPDATE People SET person_name='Obi Van Kenobi', person_salary=12000.0 WHERE id=777";
        String actual = generator.update(person);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test, throw IllegalArgumentException if it is not ORM entity.")
    public void testThrowIllegalArgumentExceptionIfItIsNotORMEntity() {
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> {
            generator.getTable(NotORMEntity.class);
        });
        assertNotNull(thrown.getMessage());
        String expected = "Class is not ORM entity";
        String actual = thrown.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test, extract id from entity.")
    public void testExtractIdFromEntity() {
        String expectedId = "ABC";
        AnotherEntity entity = new AnotherEntity(expectedId);
        Object actualId = generator.getId(entity);
        assertEquals(expectedId, actualId);
    }

    @Test
    @DisplayName("Test, extract field names from entity.")
    public void testExtractFieldNamesFromEntityClass() {
        String expected = "entity_id, entity_name, entity_age";
        String actual = generator.getAllColumnNamesFromFields(SomeEntity.class);
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    @DisplayName("Test, extract field value with field name from entity.")
    public void testExtractFieldValueWithFieldNameFromEntity() {
        SomeEntity entity = new SomeEntity(976, "Guchi", 32);
        String expected = "entity_name='Guchi', entity_age=32";
        String actual = generator.getFieldsNamesWithContent(entity);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test, extract field value from entity.")
    public void testExtractFieldValueFromEntityClass() {
        SomeEntity entity = new SomeEntity(976, "Guchi", 32);
        String expected = "976, 'Guchi', 32";
        String actual = generator.getColumnValues(entity);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test, throw exception if entity type is null")
    public void testThrowExceptionIfEntityTypeIsNull() {
        Throwable thrown = assertThrows(NullPointerException.class, () -> {
            generator.notNullValidation(null);
        });
        assertNotNull(thrown.getMessage());
        String expected = "Value can't be null.";
        String actual = thrown.getMessage();
        assertEquals(expected, actual);
    }
}
