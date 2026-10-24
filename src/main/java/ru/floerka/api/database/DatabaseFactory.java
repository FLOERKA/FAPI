package ru.floerka.api.database;

import com.zaxxer.hikari.HikariConfig;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public class DatabaseFactory {

    public static HikariConfig create(Section section, JavaPlugin plugin) {
        String type = section.getString("type");

        if(type.equalsIgnoreCase("sqlite")) {
            String fileName = section.getString("file", "database.db");
            File file = new File(plugin.getDataFolder(), fileName);
            String path = file.getAbsolutePath();
            return createSqlite(path);
        }

        String host = section.getString("host");
        int port = section.getInt("port");
        String database = section.getString("database");
        String username = section.getString("username");
        String password = section.getString("password");
        if(type.equalsIgnoreCase("mysql") || type.equalsIgnoreCase("mariadb"))
            return createMysql(host,port,database,username,password);
        else if(type.equalsIgnoreCase("postgres")) {
            return createPostgres(host,port,database,username,password);
        }
        throw new RuntimeException("Not found correct database from config");
    }

    public static HikariConfig createMysql(String host, int port, String database, String username, String password) {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, database);
        config.setJdbcUrl(jdbcUrl);

        config.setUsername(username);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        config.setMaximumPoolSize(5);

        return config;
    }
    public static HikariConfig createPostgres(String host, int port, String database, String username, String password) {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName("org.postgresql.Driver");

        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
        config.setJdbcUrl(jdbcUrl);

        config.setUsername(username);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        config.setMaximumPoolSize(5);

        return config;
    }
    public static HikariConfig createSqlite(String filePath) {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName("org.sqlite.JDBC");

        String jdbcUrl = "jdbc:sqlite:"+filePath;
        config.setJdbcUrl(jdbcUrl);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        config.setMaximumPoolSize(1);

        return config;
    }
}
