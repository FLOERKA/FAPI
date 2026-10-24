package ru.floerka.api.database.impl.sql;

import com.zaxxer.hikari.HikariConfig;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.java.JavaPlugin;
import ru.floerka.api.database.DatabaseFactory;
import ru.floerka.api.database.impl.HikariDatabase;

import java.io.File;

public class SQLiteDatabase extends HikariDatabase {
    public SQLiteDatabase(HikariConfig config) {
        super(config);
    }
    public SQLiteDatabase(String filePath) {
        super(DatabaseFactory.createSqlite(filePath));
    }
    public SQLiteDatabase(File file) {
        super(DatabaseFactory.createSqlite(file.getPath()));
    }
    public SQLiteDatabase(String fileName, JavaPlugin plugin) {
        this(new File(plugin.getDataFolder(), fileName));
    }
    public SQLiteDatabase(Section section, JavaPlugin plugin) {
        this(
                section.getString("file", "database.db"),plugin
        );
    }
}
