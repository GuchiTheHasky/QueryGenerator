package guchi.the.hasky.generator;

import guchi.the.hasky.generator.annotations.Column;
import guchi.the.hasky.generator.annotations.DefaultModifierForTests;
import guchi.the.hasky.generator.annotations.Id;
import guchi.the.hasky.generator.annotations.Table;
import lombok.SneakyThrows;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.StringJoiner;

public class DefaultQueryGenerator implements QueryGenerator {
    private final String SELECT = "SELECT ";
    private final String WHERE = " WHERE ";
    private final String FROM = " FROM ";

    @Override
    public String findAll(Class<?> type) {
        notNullValidation(type);
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
        notNullValidation(type);
        StringBuilder queryBuilder = new StringBuilder(SELECT);
        String columnNames = getAllColumnNamesFromFields(type);
        String tableName = getTableName(type);
        String idFieldName = getIdFieldName(type);
        Object filteredId = quoteIfNeeded(id);
        queryBuilder.append(columnNames);
        queryBuilder.append(FROM);
        queryBuilder.append(tableName);
        queryBuilder.append(WHERE);
        queryBuilder.append(idFieldName);
        queryBuilder.append(filteredId);
        return queryBuilder.toString();
    }

    @Override
    public String deleteById(Class<?> type, Serializable id) {
        notNullValidation(type);
        StringBuilder queryBuilder = new StringBuilder("DELETE");
        String tableName = getTableName(type);
        String idFieldName = getIdFieldName(type);
        Object filteredId = quoteIfNeeded(id);
        queryBuilder.append(FROM);
        queryBuilder.append(tableName);
        queryBuilder.append(WHERE);
        queryBuilder.append(idFieldName);
        queryBuilder.append(filteredId);
        return queryBuilder.toString();
    }

    @Override
    public String insert(Object value) {
        notNullValidation(value);
        Table tableAnnotation = getTable(value.getClass());
        String tableName = getTable(value.getClass(), tableAnnotation);
        String columnNames = getAllColumnNamesFromFields(value.getClass());
        String columnValues = getColumnValues(value);
        return "INSERT INTO " + tableName + " (" + columnNames + ") VALUES (" + columnValues + ")";
    }

    @Override
    public String update(Object value) {
        notNullValidation(value);
        Table tableAnnotation = getTable(value.getClass());
        String tableName = getTable(value.getClass(), tableAnnotation);
        Object id = getId(value);
        String fields = getFieldsNamesWithContent(value);
        String idFieldName = getIdFieldName(value);
        return "UPDATE " + tableName + " SET " + fields + WHERE + idFieldName + id;
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
            if (columnAnnotation != null) {
                decleriedField.setAccessible(true);
                Object fieldValue = decleriedField.get(value);
                columnValues.add(quoteIfNeeded(fieldValue).toString());
            }
        }
        return columnValues.toString();
    }

    @SneakyThrows
    @DefaultModifierForTests
    Object getId(Object value) {
        notNullValidation(value);
        Object id = null;
        Field[] decleriedField = value.getClass().getDeclaredFields();
        for (Field field : decleriedField) {
            Id idAnnotation = field.getAnnotation(Id.class);
            if (idAnnotation != null) {
                field.setAccessible(true);
                id = field.get(value);
            }
        }
        return id;
    }

    @DefaultModifierForTests
    String getFieldsNamesWithContent(Object value) {
        notNullValidation(value);
        LinkedHashMap<String, String> fieldNamesWithValues = new LinkedHashMap<>();
        for (Field decleriedField : value.getClass().getDeclaredFields()) {
            Column columnAnnotation = decleriedField.getAnnotation(Column.class);
            Id idAnnotation = decleriedField.getAnnotation(Id.class);
            if (columnAnnotation != null && idAnnotation == null) {
                decleriedField.setAccessible(true);
                Object fieldValue;
                try {
                    fieldValue = decleriedField.get(value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                fieldNamesWithValues.put(columnAnnotation.name(), quoteIfNeeded(fieldValue).toString());
            }
        }
        return fieldNamesWithValues.toString().substring(1, fieldNamesWithValues.toString().length() - 1);
    }

    @DefaultModifierForTests
    void notNullValidation(Object value) {
        if (value == null) {
            throw new NullPointerException("Value can't be null.");
        }
    }

    private String getIdFieldName(Class<?> type) {
        StringBuilder fieldName = new StringBuilder();
        for (Field decleriedField : type.getDeclaredFields()) {
            Id idAnnotation = decleriedField.getAnnotation(Id.class);
            if (idAnnotation != null) {
                decleriedField.setAccessible(true);
                fieldName.append(decleriedField.getName());
                fieldName.append("=");
            }
        }
        return fieldName.toString();
    }
    private String getIdFieldName(Object value) {
        StringBuilder fieldName = new StringBuilder();
        for (Field decleriedField : value.getClass().getDeclaredFields()) {
            Id idAnnotation = decleriedField.getAnnotation(Id.class);
            if (idAnnotation != null) {
                decleriedField.setAccessible(true);
                fieldName.append(decleriedField.getName());
                fieldName.append("=");
            }
        }
        return fieldName.toString();
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

    private String getTable(Class<?> type, Table tableAnnotation) {
        return tableAnnotation.name().isEmpty() ? type.getSimpleName() : tableAnnotation.name();
    }


}

