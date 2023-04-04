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
    private final String WHERE = " WHERE id = ";
    private final String SELECT = "SELECT ";
    private final String FROM = " FROM ";

    @Override
    public String findAll(Class<?> type) {
        Table tableAnnotation = getTable(type);
        String tableName = getTableName(type, tableAnnotation);
        String columnNames = extractAllColumnNamesFromFields(type);
        return findAll(tableName, columnNames);
    }

    @Override
    public String findById(Class<?> type, Serializable id) {
        Table tableAnnotation = getTable(type);
        String tableName = getTableName(type, tableAnnotation);
        String columnNames = extractAllColumnNamesFromFields(type);
        return findById(id, tableName, columnNames);
    }

    @Override
    public String deleteById(Class<?> type, Serializable id) {
        Table tableAnnotation = getTable(type);
        String tableName = getTableName(type, tableAnnotation);
        return deleteById(id, tableName);
    }

    @Override
    public String insert(Object value) {
        Table tableAnnotation = getTable(value.getClass());
        String tableName = getTableName(value.getClass(), tableAnnotation);
        String columnNames = extractAllColumnNamesFromFields(value.getClass());
        String columnValues = extractColumnValues(value);
        return insert(tableName, columnNames, columnValues);
    }

    @Override
    public String update(Object value) {
        Table tableAnnotation = getTable(value.getClass());
        String tableName = getTableName(value.getClass(), tableAnnotation);
        Object id = extractId(value);
        String fields = extractFieldsNamesWithContent(value);
        return update(tableName, id, fields);
    }

    @DefaultModifierForTests
    String extractFieldsNamesWithContent(Object value) {
        StringJoiner fieldNamesWithValues = new StringJoiner("");
        for (Field decleriedField : value.getClass().getDeclaredFields()) {
            StringJoiner fieldValues = new StringJoiner(" = ");
            Column columnAnnotation = decleriedField.getAnnotation(Column.class);
            if (columnAnnotation != null && !columnAnnotation.name().contains("id")) {
                decleriedField.setAccessible(true);
                Object fieldName = columnAnnotation.name();
                Object fieldValue = null;
                try {
                    fieldValue = decleriedField.get(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error, you don't have access to read this file.");
                }
                fieldValues.add(fieldName.toString());
                if (fieldValue == null) {
                    throw new NullPointerException("Error, current value is null");
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

    @DefaultModifierForTests
    Object extractId(Object value) {
        Object id = null;
        Field[] decleriedField = value.getClass().getDeclaredFields();
        for (Field field : decleriedField) {
            Id idAnnotation = field.getAnnotation(Id.class);
            if (idAnnotation != null) {
                field.setAccessible(true);
                try {
                    id = field.get(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error, you don't have access to read this file.");
                }
            }
        }
        return id;
    }

    @DefaultModifierForTests
    String extractAllColumnNamesFromFields(Class<?> type) {
        StringJoiner columnNames = new StringJoiner(", ");
        for (Field decleriedField : type.getDeclaredFields()) {
            Column columnAnnotation = decleriedField.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? decleriedField.getName() : columnAnnotation.name();
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
    String extractColumnValues(Object value) {
        StringJoiner columnValues = new StringJoiner(", ");
        for (Field decleriedField : value.getClass().getDeclaredFields()) {
            Column columnAnnotation = decleriedField.getAnnotation(Column.class);
            if (columnAnnotation != null && !Objects.equals(columnAnnotation.name(), "id")) {
                decleriedField.setAccessible(true);
                Object fieldValue = null;
                try {
                    fieldValue = decleriedField.get(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error, you don't have access to read this file.");
                }
                columnValues.add(fieldValue.toString());
            }
        }
        return columnValues.toString();
    }

    @DefaultModifierForTests
    String extractColumnNamesWithoutId(Object value) {
        StringJoiner columnNames = new StringJoiner(", ");
        for (Field decleriedField : value.getClass().getDeclaredFields()) {
            Column columnAnnotation = decleriedField.getAnnotation(Column.class);
            if (columnAnnotation != null && !columnAnnotation.name().contains("id")) {
                String columnName =
                        columnAnnotation.name().isEmpty() ? decleriedField.getName() : columnAnnotation.name();
                columnNames.add(columnName);
            }
        }
        return columnNames.toString();
    }

    private String update(String tableName, Object id, String fields) {
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(tableName);
        query.append(" SET ");
        query.append(fields);
        query.append(WHERE);
        query.append(id);
        return query.toString();
    }

    private static String insert(String tableName, String columnNames, String columnValues) {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(tableName);
        query.append(" (");
        query.append(columnNames);
        query.append(')');
        query.append(" VALUES ");
        query.append('(');
        query.append(columnValues);
        query.append(')');
        return query.toString();
    }

    private String findAll(String tableName, String columnNames) {
        StringBuilder query = new StringBuilder(SELECT);
        query.append(columnNames);
        query.append(FROM);
        query.append(tableName);
        return query.toString();
    }

    private String findById(Serializable id, String tableName, String columnNames) {
        StringBuilder query = new StringBuilder(SELECT);
        query.append(columnNames);
        query.append(FROM);
        query.append(tableName);
        query.append(WHERE);
        query.append(id);
        return query.toString();
    }

    private String deleteById(Serializable id, String tableName) {
        StringBuilder query = new StringBuilder("DELETE");
        query.append(FROM);
        query.append(tableName);
        query.append(WHERE);
        query.append(id);
        return query.toString();
    }

    private String getTableName(Class<?> clazz, Table tableAnnotation) {
        return tableAnnotation.name().isEmpty() ? clazz.getSimpleName() : tableAnnotation.name();
    }
}

