package ru.floerka.api.utils.file;

import dev.dejvokep.boostedyaml.YamlDocument;
import ru.floerka.api.utils.plugin.PluginUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    public static YamlDocument getDocument(String name) {
        Path path = PluginUtil.getCurrentPlugin().getDataFolder().toPath().resolve(name);
        if(Files.notExists(path)) {

            Path parent = path.getParent();
            if(Files.notExists(path)) {
                try {
                    Files.createDirectories(parent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try (InputStream stream = FileUtil.class.getResourceAsStream("/" + name)) {
                if (stream != null) {
                    Files.copy(stream, path);
                } else {
                    Files.createFile(path);
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }

        }

        try {
            return YamlDocument.create(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
