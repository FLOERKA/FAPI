package ru.floerka.api.database.impl.sql;

import com.zaxxer.hikari.HikariConfig;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import ru.floerka.api.database.impl.HikariDatabase;

public class PostgresSQLDatabase extends HikariDatabase {
    public PostgresSQLDatabase(HikariConfig config) {
        super(config);
    }
    public PostgresSQLDatabase(String host, int port, String database, String username, String password) {
        super(createConfig(host,port,database,username,password));
    }
    public PostgresSQLDatabase(Section section) {
        this(
                section.getString("host", "localhost"),
                section.getInt("port", 3306),
                section.getString("database", ""),
                section.getString("username", "root"),
                section.getString("password", "")
        );
    }

    private static HikariConfig createConfig(String host, int port, String database, String username, String password) {
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
}
