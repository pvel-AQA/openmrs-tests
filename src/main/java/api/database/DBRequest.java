package api.database;

import api.database.dao.BaseDaoModel;
import lombok.Builder;
import lombok.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Data
@Builder
public class DBRequest {
    private RequestType requestType;
    private String table;
    private List<Condition> conditions;
    private List<JoinClause> joins;
    private List<String> selectColumns;
    private BaseDaoModel<?> modelMapper;

    public enum RequestType {
        SELECT, INSERT, UPDATE, DELETE
    }

    @Data
    @Builder
    public static class JoinClause {
        private JoinType type;  // INNER, LEFT, RIGHT
        private String table;
        private String onCondition;  // Например: "p.patient_id = pa.patient_id"

        public enum JoinType {
            INNER, LEFT, RIGHT
        }
    }

    public <T> T extractAs(Class<T> clazz) {
        if (modelMapper != null) {
            @SuppressWarnings("unchecked")
            BaseDaoModel<T> typedMapper = (BaseDaoModel<T>) modelMapper;
            return executeQuery(rs -> {
                if (rs.next()) {
                    return typedMapper.mapRow(rs);
                }
                return null;
            });
        }
        return executeQuery(rs -> AutoMapper.mapToEntity(rs, clazz));
    }

    public <T> List<T> extractAsList(Class<T> clazz) {
        if (modelMapper != null) {
            @SuppressWarnings("unchecked")
            BaseDaoModel<T> typedMapper = (BaseDaoModel<T>) modelMapper;
            return executeQuery(rs -> {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(typedMapper.mapRow(rs));
                }
                return results;
            });
        }
        return executeQuery(rs -> AutoMapper.mapToList(rs, clazz));
    }

    private <T> T executeQuery(ResultSetHandler<T> handler) {
        String sql = buildSQL();

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setParameters(statement);

            try (ResultSet resultSet = statement.executeQuery()) {
                return handler.handle(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        }
    }

    private void setParameters(PreparedStatement statement) throws SQLException {
        if (conditions != null) {
            for (int i = 0; i < conditions.size(); i++) {
                statement.setObject(i + 1, conditions.get(i).getValue());
            }
        }
    }

    private String buildSQL() {
        StringBuilder sql = new StringBuilder();

        switch (requestType) {
            case SELECT:
                if (selectColumns != null && !selectColumns.isEmpty()) {
                    sql.append("SELECT ");
                    sql.append(String.join(", ", selectColumns));
                } else {
                    sql.append("SELECT *");
                }

                sql.append(" FROM ").append(table);

                if (joins != null && !joins.isEmpty()) {
                    for (JoinClause join : joins) {
                        sql.append(" ")
                                .append(join.getType().name())
                                .append(" JOIN ")
                                .append(join.getTable())
                                .append(" ON ")
                                .append(join.getOnCondition());
                    }
                }

                if (conditions != null && !conditions.isEmpty()) {
                    sql.append(" WHERE ");
                    for (int i = 0; i < conditions.size(); i++) {
                        if (i > 0) sql.append(" AND ");
                        sql.append(conditions.get(i).getColumn())
                                .append(" ")
                                .append(conditions.get(i).getOperator())
                                .append(" ?");
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException(
                        "Request type " + requestType + " not implemented");
        }

        return sql.toString();
    }

    /**
     * Универсальный авто-маппер.
     * Автоматически маппит ResultSet на entity с вложенными объектами.
     */
    public static class AutoMapper {

        public static <T> T mapToEntity(ResultSet rs, Class<T> clazz) throws SQLException {
            if (!rs.next()) {
                return null;
            }
            return mapCurrentRow(rs, clazz);
        }

        public static <T> List<T> mapToList(ResultSet rs, Class<T> clazz) throws SQLException {
            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapCurrentRow(rs, clazz));
            }
            return results;
        }

        private static <T> T mapCurrentRow(ResultSet rs, Class<T> clazz) {
            try {
                T entity = clazz.getDeclaredConstructor().newInstance();
                mapSimpleFields(rs, entity);
                mapNestedObjects(rs, entity);
                return entity;
            } catch (Exception e) {
                throw new RuntimeException("Failed to map entity: " + clazz.getName(), e);
            }
        }

        private static void mapSimpleFields(ResultSet rs, Object entity) throws Exception {
            for (java.lang.reflect.Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                if (Collection.class.isAssignableFrom(field.getType()) || isComplexType(field.getType())) {
                    continue;
                }

                String columnName = camelToSnake(field.getName());

                try {
                    Object value = rs.getObject(columnName);
                    if (value != null) {
                        field.set(entity, value);
                    }
                } catch (SQLException e) {
                    // Колонка не найдена - пропускаем
                }
            }
        }

        private static void mapNestedObjects(ResultSet rs, Object entity) throws Exception {
            for (java.lang.reflect.Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                if (isComplexType(field.getType())) {
                    // Маппим вложенный объект по префиксу
                    String prefix = camelToSnake(field.getName()) + "_";
                    Object nestedObject = mapNestedFromPrefix(rs, field.getType(), prefix);
                    field.set(entity, nestedObject);
                }
            }
        }

        private static Object mapNestedFromPrefix(ResultSet rs, Class<?> type, String prefix) throws Exception {
            Object nested = type.getDeclaredConstructor().newInstance();

            for (java.lang.reflect.Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                String columnName = prefix + camelToSnake(field.getName());

                try {
                    Object value = rs.getObject(columnName);
                    if (value != null) {
                        field.set(nested, value);
                    }
                } catch (SQLException e) {
                    // Колонка не найдена
                }
            }

            return nested;
        }

        private static boolean isComplexType(Class<?> type) {
            return !type.isPrimitive() && !type.getName().startsWith("java.") && !type.isEnum();
        }

        private static String camelToSnake(String camelCase) {
            return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        }
    }

    public static class DBRequestBuilder {
        private RequestType requestType;
        private String table;
        private List<Condition> conditions = new ArrayList<>();
        private List<JoinClause> joins = new ArrayList<>();
        private List<String> selectColumns = new ArrayList<>();
        private BaseDaoModel<?> modelMapper;

        public DBRequestBuilder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public DBRequestBuilder where(Condition condition) {
            this.conditions.add(condition);
            return this;
        }

        public DBRequestBuilder table(String table) {
            this.table = table;
            return this;
        }

        public DBRequestBuilder leftJoin(String table, String onCondition) {
            return addJoin(JoinClause.JoinType.LEFT, table, onCondition);
        }

        public DBRequestBuilder rightJoin(String table, String onCondition) {
            return addJoin(JoinClause.JoinType.RIGHT, table, onCondition);
        }

        public DBRequestBuilder innerJoin(String table, String onCondition) {
            return addJoin(JoinClause.JoinType.INNER, table, onCondition);
        }

        private DBRequestBuilder addJoin(JoinClause.JoinType type, String table, String onCondition) {
            this.joins.add(JoinClause.builder()
                    .type(type)
                    .table(table)
                    .onCondition(onCondition)
                    .build());
            return this;
        }

        public DBRequestBuilder select(String... columns) {
            this.selectColumns.addAll(Arrays.asList(columns));
            return this;
        }

        public DBRequestBuilder withModel(BaseDaoModel<?> mapper) {
            this.modelMapper = mapper;
            return this;
        }

        public <T> T extractAs(Class<T> clazz) {
            return buildRequest().extractAs(clazz);
        }

        public <T> List<T> extractAsList(Class<T> clazz) {
            return buildRequest().extractAsList(clazz);
        }

        private DBRequest buildRequest() {
            return DBRequest.builder()
                    .requestType(requestType)
                    .table(table)
                    .conditions(conditions)
                    .joins(joins)
                    .selectColumns(selectColumns)
                    .modelMapper(modelMapper)
                    .build();
        }
    }

    @FunctionalInterface
    private interface ResultSetHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }
}
