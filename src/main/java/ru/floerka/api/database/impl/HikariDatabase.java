package ru.floerka.api.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ru.floerka.api.config.models.SerializableField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class HikariDatabase extends AbstractDatabase {

    private final HikariDataSource dataSource;

    public HikariDatabase(HikariConfig config) {
        this.dataSource = new HikariDataSource(config);
    }

    public void execute(String command, Object... args) {
        command = prepareCommand(command, args);
        try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(command)) {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void executeQuery(String command, ResultMapper mapper, Object... args) {
        command = prepareCommand(command, args);
        try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(command)) {
            ResultSet resultSet = statement.executeQuery();
            mapper.map(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public String prepareCommand(String command, Object... args) {
        for(Object object : args) {
            String replaceTo;
            Class<?> type = object.getClass();
            if(isPrimitiveType(type)) {
                replaceTo = String.valueOf(object);
            } else {
                if(object instanceof SerializableField<?> serializableField) {
                    replaceTo = serializableField.getAsString();
                } else {
                    replaceTo = String.valueOf(object);
                }
            }

            command = command.replaceFirst("\\?", replaceTo);
        }
        return command;
    }
    private boolean isPrimitiveType(Class<?> clazz) {
        return clazz.equals(int.class) || clazz.equals(Integer.class)
                || clazz.equals(Long.class) || clazz.equals(long.class)
                || clazz.equals(Double.class) || clazz.equals(double.class)
                || clazz.equals(Float.class) || clazz.equals(float.class)
                || clazz.equals(String.class) || clazz.equals(Character.class)
                || clazz.equals(char.class);
    }

    public void execute(ConnectionExecutor executor) {
        try(Connection connection = dataSource.getConnection()) {
            executor.execute(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void execute(String command, StatementExecutor executor) {
        try(Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(command)) {
            executor.execute(connection, statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public interface ConnectionExecutor {
        void execute(Connection connection);
    }
    public interface StatementExecutor {
        void execute(Connection connection, PreparedStatement statement);
    }
    public interface ResultMapper {
        void map(ResultSet resultSet);
    }
}
