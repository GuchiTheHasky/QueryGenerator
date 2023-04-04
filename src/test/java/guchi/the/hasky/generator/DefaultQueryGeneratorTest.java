package guchi.the.hasky.generator;

import guchi.the.hasky.generator.annotations.Column;
import guchi.the.hasky.generator.annotations.Id;
import guchi.the.hasky.generator.entity.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultQueryGeneratorTest {
    private final DefaultQueryGenerator generator = new DefaultQueryGenerator();
    private final Person person = new Person();

    /**
     * SELECT declared fields FROM table name;
     */
    @Test
    @DisplayName("Test, generate select all from class check expected & actual value.")
    public void testGenerateSelectAllFromClassCheckExpectedAndActualValue() {
        String expected = "SELECT person_id, person_name, person_salary FROM Person";
        String actual = generator.findAll(Person.class);
        assertEquals(expected, actual);
    }

    /**
     * SELECT declared fields FROM table name WHERE id = id.value;
     */
    @Test
    @DisplayName("Test, generate find by id from class check expected & actual value.")
    public void testGenerateFindByIdFromClassCheckExpectedAndActualValue() {
        String id = "123";
        String expected = "SELECT person_id, person_name, person_salary FROM Person WHERE id = " + id;
        String actual = generator.findById(person.getClass(), "123");
        assertEquals(expected, actual);
    }

    /**
     * DELETE FROM table name WHERE id = id.value.
     */
    @Test
    @DisplayName("Test, generate delete by id from class check expected & actual value;")
    public void testGenerateDeleteByIdFromClassCheckExpectedAndActualValue() {
        String id = "123";
        String expected = "DELETE FROM Person WHERE id = " + id;
        String actual = generator.deleteById(person.getClass(), "123");
        assertEquals(expected, actual);
    }


    /**
     * INSERT INTO table name (declared fields) VALUES (fields values);
     */
    @Test
    @DisplayName("Test, generate insert object check expected & actual value.")
    public void testGenerateInsertObjectCheckExpectedAndActualValue() {
        Person person = new Person(111, "Obi Van Kenobi", 12000);
        String expected = "INSERT INTO Person (person_id, person_name, person_salary) VALUES " +
                "(" + person.getId() + ", " + person.getName() + ", " + person.getSalary() + ")";
        String actual = generator.insert(person);
        assertEquals(expected, actual);
    }

    /**
     * UPDATE table name SET fields (without id) WHERE id = id.value;
     */
    @Test
    @DisplayName("Test, generate update object check expected & actual value.")
    public void testGenerateUpdateObjectCheckExpectedAndActualValue() {
        Person person = new Person("myId", "Obi Van Kenobi", 12000);
        String expected = "UPDATE Person SET person_name = " + person.getName() +
                ", person_salary = " + person.getSalary() +
                " WHERE id = " + person.getId();
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
    }

    @Test
    @DisplayName("Test, extract id check expected and actual value.")
    public void testExtractIdCheckExpectedAndActualValue() {
        String expectedId = "ABC";
        EntityWithId entity = new EntityWithId(expectedId);
        Object actualId = generator.extractId(entity);
        assertEquals(expectedId, actualId);
    }

    @Test
    @DisplayName("Test, extract field names from entity class check expected & actual value.")
    public void testExtractFieldNamesFromEntityClassCheckExpectedAndActualValue() {
        String expected = "entity_id, entity_name, entity_age";
        String actual = generator.extractAllColumnNamesFromFields(SomeEntity.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test, extract field value with field name from entity class check expected & actual value.")
    public void testExtractFieldValueWithFieldNameFromEntityClassCheckExpectedAndActualValue() {
        SomeEntity entity = new SomeEntity(976, "Guchi", 32);
        String expected = "entity_name = Guchi, entity_age = 32";
        String actual = generator.extractFieldsNamesWithContent(entity);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test, extract field value from entity class check expected & actual value.")
    public void testExtractFieldValueFromEntityClassCheckExpectedAndActualValue() {
        SomeEntity entity = new SomeEntity(976, "Guchi", 32);
        String expected = "976, Guchi, 32";
        String actual = generator.extractColumnValues(entity);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test, extract field name from entity class without id column check expected & actual value.")
    public void testExtractFieldNameFromEntityClassWithoutIdColumnCheckExpectedAndActualValue() {
        SomeEntity entity = new SomeEntity(976, "Guchi", 32);
        String actual = generator.extractColumnNamesWithoutId(entity);
        System.out.println(actual);
    }

    @AllArgsConstructor
    private static class SomeEntity {
        @Id
        @Column(name = "entity_id")
        private Serializable id;
        @Column(name = "entity_name")
        private String name;
        @Column(name = "entity_age")
        private int age;
    }

    private static class NotORMEntity {
    }

    @Getter
    @AllArgsConstructor
    private static class EntityWithId {
        @Id
        private String id;
    }
}
