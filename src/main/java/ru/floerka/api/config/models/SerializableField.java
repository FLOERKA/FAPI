package ru.floerka.api.config.models;

import org.jetbrains.annotations.NotNull;

public class SerializableField<T> {

    private final FromTo<T> deserializer;
    private final ToFrom<T> serializer;

    private T cachedSerializedObject;

    public SerializableField(FromTo<T> deserializer, ToFrom<T> serializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.cachedSerializedObject = null;
    }

    public @NotNull T getObject() {
        if(cachedSerializedObject == null) {
            throw new RuntimeException("Serialized Object is Null");
        }
        return cachedSerializedObject;
    }
    public T setObject(String from) {
        return deserialize(from);
    }

    private T deserialize(String from) {
        this.cachedSerializedObject = deserializer.deserialize(from);
        return cachedSerializedObject;
    }

    private String serialize(T from) {
        return serializer.serialize(from);
    }


    public interface FromTo<T> {
        T deserialize(String from);
    }
    public interface ToFrom<T> {
        String serialize(T from);
    }
}
