package guchi.the.hasky.generator;

import guchi.the.hasky.generator.annotations.Column;
import guchi.the.hasky.generator.annotations.DefaultModifierForTests;
import guchi.the.hasky.generator.annotations.Id;
import guchi.the.hasky.generator.annotations.Table;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.StringJoiner;

public class DefaultQueryGenerator implements QueryGenerator {
    private final String WHERE = " WHERE ";
    private final String FROM = "FROM ";
    private StringBuilder requestBuilder;

    @Override
    public String findAll(Class<?> type) {
        String tableName = getTableName(type);
        requestBuilder = new StringBuilder();
        requestBuilder.append("SELECT");
        requestBuilder.append(" * ");
        requestBuilder.append(FROM);
        requestBuilder.append(tableName);
        return requestBuilder.toString();
    }

    @Override
    public String findById(Class<?> type, Serializable id) {
        requestBuilder = new StringBuilder();
        requestBuilder.append(findAll(type));
        requestBuilder.append(WHERE);
        requestBuilder.append("id = ");
        requestBuilder.append(id);
        return requestBuilder.toString();
    }

    @Override
    public String deleteById(Class<?> type, Serializable id) {
        requestBuilder = new StringBuilder("DELETE ");
        String tableName = getTableName(type);
        requestBuilder.append(FROM);
        requestBuilder.append(tableName);
        requestBuilder.append(WHERE);
        requestBuilder.append("id = ");
        requestBuilder.append(id);
        return requestBuilder.toString();
    }

    @Override
    public String insert(Object value) {
        Table tableAnnotation = getTable(value.getClass());
        String tableName = getTable(value.getClass(), tableAnnotation);
        String columnNames = getAllColumnNamesFromFields(value.getClass());
        String columnValues = getColumnValues(value);
        requestBuilder = new StringBuilder("INSERT INTO ");
        requestBuilder.append(tableName);
        requestBuilder.append(" (");
        requestBuilder.append(columnNames);
        requestBuilder.append(")");
        requestBuilder.append(" VALUES ");
        requestBuilder.append("(");
        requestBuilder.append(columnValues);
        requestBuilder.append(")");
        return requestBuilder.toString();
    }

    @Override
    public String update(Object value) {
        Table tableAnnotation = getTable(value.getClass());
        String tableName = getTable(value.getClass(), tableAnnotation);
        Object id = getId(value);
        String fields = getFieldsNamesWithContent(value);
        requestBuilder = new StringBuilder("UPDATE ");
        requestBuilder.append(tableName);
        requestBuilder.append(" SET ");
        requestBuilder.append(fields);
        requestBuilder.append(WHERE);
        requestBuilder.append("id = ");
        requestBuilder.append(id);
        return requestBuilder.toString();

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
            throw new IllegalArgumentException("class is not ORM entity");
        }
        return tableAnnotation;
    }

    @DefaultModifierForTests
    String getColumnValues(Object value) {
        StringJoiner columnValues = new StringJoiner(", ");
        for (Field decleriedField : value.getClass().getDeclaredFields()) {
            Column columnAnnotation = decleriedField.getAnnotation(Column.class);
            if (columnAnnotation != null && !Objects.equals(columnAnnotation.name(), "id")) {
                decleriedField.setAccessible(true);
                Object fieldValue = null;
                try {
                    fieldValue = decleriedField.get(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("You don't have access to read this file.");
                }
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
    String getFieldsNamesWithContent(Object value) {
        StringJoiner fieldNamesWithValues = new StringJoiner("");
        for (Field decleriedField : value.getClass().getDeclaredFields()) {
            StringJoiner fieldValues = new StringJoiner(" = ");
            Column columnAnnotation = decleriedField.getAnnotation(Column.class);
            if (columnAnnotation != null && !columnAnnotation.name().contains("id")) {
                decleriedField.setAccessible(true);
                Object fieldName = columnAnnotation.name();
                Object fieldValue = null;
                try {
                    fieldValue = quoteIfNeeded(decleriedField.get(value));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("You don't have access to read this file.");
                }
                fieldValues.add(fieldName.toString());
                if (fieldValue == null) {
                    throw new NullPointerException("Current value is null.");
                }
                fieldValues.add(fieldValue.toString());
            }
            if (fieldNamesWithValues.length() > 0) {
                fieldNamesWithValues.add(", ");
            }
            fieldNamesWithValues.add(fieldValues.toString());
        }
        return fieldNamesWithValues.toString();
    }

    private Object quoteIfNeeded(Object value) {
        if (value instanceof String) {
            return "'" + value + "'";
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

