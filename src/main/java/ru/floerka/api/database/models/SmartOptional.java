package ru.floerka.api.database.models;


public class SmartOptional<T> {

    private T object;

    public SmartOptional(T object) {
        this.object = object;
    }
    public static <T> SmartOptional<T> empty() {
        return new SmartOptional<>(null);
    }
    public static <T> SmartOptional<T> of(T object) {
        return new SmartOptional<>(object);
    }
    public boolean ifPresent() {
        return object != null;
    }
    public T get() {
        return object;
    }
    public void set(T object) {
        this.object = object;
    }

}
