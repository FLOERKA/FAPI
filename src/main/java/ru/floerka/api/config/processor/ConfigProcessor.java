package ru.floerka.api.config.processor;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.Block;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.Nullable;
import ru.floerka.api.config.models.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ConfigProcessor {

    private static final List<CachedConfig<?>> cache;
    private static final String cachedPluginName;

    static {
        cache = new ArrayList<>();
        cachedPluginName = getPluginName();
    }


    @SafeVarargs
    public static <T> Set<T> loadMore(Class<T>... classes) {
        Set<T> set = new HashSet<>();
        for(var clazz : classes) {
            set.add(load(clazz));
        }
        return set;
    }

    public static <T> T load(Class<T> configClazz, Object... args) {
        if(!configClazz.isAnnotationPresent(Config.class)) {
            throw new RuntimeException("Class " + configClazz.getName() + " not has @Config");
        }
        Config annotation = configClazz.getAnnotation(Config.class);
        String configName = annotation.name();

        Path path = getPath(configName);
        assert Files.exists(path);

        try {
            YamlDocument document = YamlDocument.create(path.toFile());

            T instance = create(configClazz, document, args);

            cache.add(new CachedConfig<>(configName, document, instance));

            return instance;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T get(Class<T> clazz, Object... args) {
        if(!clazz.isAnnotationPresent(Config.class)) {
            throw new RuntimeException("Class " + clazz.getName() + " not has @Config");
        }
        CachedConfig<?> cachedConfig = cache.stream().filter(c -> c.name().equals(clazz.getAnnotation(Config.class).name()))
                .findAny().orElse(null);
        if(cachedConfig == null) {
            return load(clazz, args);
        }
        return clazz.cast(cachedConfig.instance());
    }

    public static <T> T create(Class<T> clazz, Section document, Object... consArgs) {
        try {

            Class<?>[] consParams = new Class<?>[consArgs.length];
            for(int i = 0; i < consArgs.length; i++) {
                consParams[i] = consArgs[i].getClass();
            }
            Constructor<T> constructor = clazz.getConstructor(consParams);
            T instance = constructor.newInstance(consArgs);

            for(Field field : clazz.getFields()) {
                field.setAccessible(true);


                if(field.isAnnotationPresent(ConfigIgnore.class)) {
                    continue;
                }

                ConfigField configField = null;
                String name = field.getName();
                if(field.isAnnotationPresent(ConfigField.class)) {
                    configField = field.getAnnotation(ConfigField.class);
                    if(!configField.fieldName().isEmpty()) {
                        name = configField.fieldName();
                    }
                }

                if(isDefaultType(field)) {
                    Object j = document.get(name);
                    if(j != null) {
                        field.set(instance, j);
                    }
                } else {
                    if(field.getType().equals(SerializableField.class)) {
                        SerializableField<?> serializable = (SerializableField<?>) field.get(instance);
                        String j = document.getString(name);
                        serializable.setObject(j);
                    } else if(field.getType().equals(Map.class)) {
                        Map<Object, Block<?>> map = document.getSection(name).getStoredValue();
                        Map<Object,Object> objectMap = new HashMap<>();
                        map.forEach((o1,o2) -> {
                            Object value = o2.getStoredValue();
                            objectMap.put(o1,value);
                        });
                        field.set(instance, objectMap);
                    } else if(field.getType().equals(List.class)) {
                        List<?> list = document.getList(name);
                        field.set(instance, list);
                    } else {
                        if(configField != null && configField.isSection()) {
                            field.set(instance, create(field.getType(), document.getSection(name)));
                        }
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static boolean isDefaultType(Field field) {
        Class<?> type = field.getType();
        if(type == int.class || type == Integer.class
        || type == String.class || type == long.class || type == Long.class
        || type == float.class || type == Float.class || type == double.class || type == Double.class)
            return true;
        return false;
    }

    public static Path getPath(String name) {
        Path dataFolder = getPluginFolder().toPath();
        Path file = dataFolder.resolve(name);
        if(Files.notExists(file)) {
            try(InputStream stream = ConfigProcessor.class.getResourceAsStream(name)) {
                if(stream != null) {
                    Files.copy(stream,file);
                } else throw new RuntimeException();
            } catch (IOException ignore) {
                try {
                    Files.createFile(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return file;
    }


    private static File getPluginFolder() {
        try {
            File jarFile = new File(ConfigProcessor.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());

            File parentFolder = jarFile.getParentFile();

            File dataFolder = new File(parentFolder, cachedPluginName);

            if (!dataFolder.exists())
                dataFolder.mkdirs();

            return dataFolder;

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static @Nullable String getPluginName() {
        try(InputStream stream = ConfigProcessor.class.getResourceAsStream("plugin.yml")) {
            if(stream != null) {
                YamlDocument document = YamlDocument.create(stream);
                return document.getString("name");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
