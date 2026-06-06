package ru.floerka.api.database.impl.sql;

import com.zaxxer.hikari.HikariConfig;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.java.JavaPlugin;
import ru.floerka.api.database.impl.HikariDatabase;

import java.io.File;

public class SQLiteDatabase extends HikariDatabase {
    public SQLiteDatabase(HikariConfig config) {
        super(config);
    }
    public SQLiteDatabase(String filePath) {
        super(createConfig(filePath));
    }
    public SQLiteDatabase(File file) {
        super(createConfig(file.getPath()));
    }
    public SQLiteDatabase(String fileName, JavaPlugin plugin) {
        this(new File(plugin.getDataFolder(), fileName));
    }
    public SQLiteDatabase(Section section, JavaPlugin plugin) {
        this(
                section.getString("file", "database.db"),plugin
        );
    }

    private static HikariConfig createConfig(String filePath) {
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
