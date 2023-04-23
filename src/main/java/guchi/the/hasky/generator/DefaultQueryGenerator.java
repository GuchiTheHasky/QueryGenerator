package guchi.the.hasky.generator;

import guchi.the.hasky.generator.annotations.Column;
import guchi.the.hasky.generator.annotations.DefaultModifierForTests;
import guchi.the.hasky.generator.annotations.Id;
import guchi.the.hasky.generator.annotations.Table;
import lombok.SneakyThrows;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.StringJoiner;

public class DefaultQueryGenerator implements QueryGenerator {
    private final String SELECT = "SELECT ";
    private final String WHERE = " WHERE ";
    private final String FROM = " FROM ";

    @Override
    public String findAll(Class<?> type) {
        validateEntityNotNul(type);
        String tableName = getTableName(type);
        StringBuilder queryBuilder = new StringBuilder(SELECT);
        String columnNames = getAllColumnNamesFromFields(type);
        queryBuilder.append(columnNames);
        queryBuilder.append(FROM);
        queryBuilder.append(tableName);
        return queryBuilder.toString();
    }

    @Override
    public String findById(Class<?> type, Serializable id) {
        validateEntityNotNul(type);
        StringBuilder queryBuilder = new StringBuilder(SELECT);
        String columnNames = getAllColumnNamesFromFields(type);
        queryBuilder.append(columnNames);
        queryBuilder.append(FROM);
        String tableName = getTableName(type);
        queryBuilder.append(tableName);
        queryBuilder.append(WHERE);
        queryBuilder.append("id = ");
        queryBuilder.append(id);
        return queryBuilder.toString();
    }

    @Override
    public String deleteById(Class<?> type, Serializable id) {
        validateEntityNotNul(type);
        StringBuilder queryBuilder = new StringBuilder("DELETE");
        String tableName = getTableName(type);
        queryBuilder.append(FROM);
        queryBuilder.append(tableName);
        queryBuilder.append(WHERE);
        queryBuilder.append("id = ");
        queryBuilder.append(id);
        return queryBuilder.toString();
    }

    @Override
    public String insert(Object value) {
        validateEntityNotNul(value);
        Table tableAnnotation = getTable(value.getClass());
        String tableName = getTable(value.getClass(), tableAnnotation);
        String columnNames = getAllColumnNamesFromFields(value.getClass());
        String columnValues = getColumnValues(value);
        return "INSERT INTO " + tableName + " (" + columnNames + ") VALUES (" + columnValues + ")";
    }

    @Override
    public String update(Object value) {
        validateEntityNotNul(value);
        Table tableAnnotation = getTable(value.getClass());
        String tableName = getTable(value.getClass(), tableAnnotation);
        Object id = getId(value);
        String fields;
        try {
            fields = getFieldsNamesWithContent(value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return "UPDATE " + tableName + " SET " + fields + WHERE + "id = " + id;
    }

    @DefaultModifierForTests
    String getAllColumnNamesFromFields(Class<?> type) {
        StringJoiner columnNames = new StringJoiner(", ");
        for (Field decleriedField : type.getDeclaredFields()) {
            Column columnAnnotation = decleriedField.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String columnName =
                        columnAnnotation.name().isEmpty() ? decleriedField.getName() : columnAnnotation.name();
                columnNames.add(columnName);
            }
        }
        return columnNames.toString();
    }

    @DefaultModifierForTests
    Table getTable(Class<?> type) {
        Table tableAnnotation = type.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new IllegalArgumentException("Class is not ORM entity");
        }
        return tableAnnotation;
    }

    @SneakyThrows
    @DefaultModifierForTests
    String getColumnValues(Object value) {
        StringJoiner columnValues = new StringJoiner(", ");
        for (Field decleriedField : value.getClass().getDeclaredFields()) {
            Column columnAnnotation = decleriedField.getAnnotation(Column.class);
            if (columnAnnotation != null && !Objects.equals(columnAnnotation.name(), "id")) {
                decleriedField.setAccessible(true);
                Object fieldValue = decleriedField.get(value);
                columnValues.add(quoteIfNeeded(fieldValue).toString());
            }
        }
        return columnValues.toString();
    }

    @DefaultModifierForTests
    Object getId(Object value) {
        Object id = null;
        Field[] decleriedField = value.getClass().getDeclaredFields();
        for (Field field : decleriedField) {
            Id idAnnotation = field.getAnnotation(Id.class);
            if (idAnnotation != null) {
                field.setAccessible(true);
                try {
                    id = field.get(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("You don't have access to read this file.");
                }
            }
        }
        return id;
    }

    @DefaultModifierForTests
    String getFieldsNamesWithContent(Object value) throws IllegalAccessException {
        StringBuilder fieldNamesWithValues = new StringBuilder(); // змінив на білдера
        for (Field decleriedField : value.getClass().getDeclaredFields()) {
            StringJoiner fieldValues = new StringJoiner(" = ");
            Column columnAnnotation = decleriedField.getAnnotation(Column.class);
            // !columnAnnotation.name().contains("id") , метод повертає строку ключ/значення для квері update,
            // тобто формує строку з полів, але не включно з id, тому що воно має бути в кінці, після команди WHERE.
            if (columnAnnotation != null && !columnAnnotation.name().contains("id")) {
                decleriedField.setAccessible(true);
                Object fieldName = columnAnnotation.name();
                Object fieldValue = quoteIfNeeded(decleriedField.get(value));
                fieldValues.add(fieldName.toString());
                if (fieldValue == null) {
                    throw new NullPointerException("Current value is null.");
                }
                fieldValues.add(fieldValue.toString());
            }
            if (fieldNamesWithValues.length() > 0) { // це код залишив, бо інакше поля неправильно розділяються.
                fieldNamesWithValues.append(", ");
            }
            fieldNamesWithValues.append(fieldValues);
        }
        return fieldNamesWithValues.toString();
    }

    @DefaultModifierForTests
    void validateEntityNotNul(Object type) {
        if (type == null) {
            throw new NullPointerException("Entity can't be null.");
        }
    }

    private Object quoteIfNeeded(Object value) {
        if (value != null) {
            if (value instanceof String) {
                return "'" + value + "'";
            }
        }
        return value;
    }

    private String getTableName(Class<?> type) {
        Table tableAnnotation = getTable(type);
        return getTable(type, tableAnnotation);
    }

    private String getTable(Class<?> clazz, Table tableAnnotation) {
        return tableAnnotation.name().isEmpty() ? clazz.getSimpleName() : tableAnnotation.name();
    }
}

