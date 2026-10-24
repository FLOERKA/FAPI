package ru.floerka.api.database.impl.sql;

import com.zaxxer.hikari.HikariConfig;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import ru.floerka.api.database.DatabaseFactory;
import ru.floerka.api.database.impl.HikariDatabase;

public class MySQLDatabase extends HikariDatabase {
    public MySQLDatabase(HikariConfig config) {
        super(config);
    }
    public MySQLDatabase(String host, int port, String database, String username, String password) {
        super(DatabaseFactory.createMysql(host,port,database,username,password));
    }
    public MySQLDatabase(Section section) {
        this(
                section.getString("host", "localhost"),
                section.getInt("port", 3306),
                section.getString("database", ""),
                section.getString("username", "root"),
                section.getString("password", "")
        );
    }
}
