package ru.floerka.api.database.impl.sql;

import com.zaxxer.hikari.HikariConfig;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import ru.floerka.api.database.DatabaseFactory;
import ru.floerka.api.database.impl.HikariDatabase;

public class PostgresSQLDatabase extends HikariDatabase {
    public PostgresSQLDatabase(HikariConfig config) {
        super(config);
    }
    public PostgresSQLDatabase(String host, int port, String database, String username, String password) {
        super(DatabaseFactory.createPostgres(host,port,database,username,password));
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
}
