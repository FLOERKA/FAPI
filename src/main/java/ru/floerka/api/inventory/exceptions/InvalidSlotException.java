package ru.floerka.api.inventory.exceptions;

public class InvalidSlotException extends RuntimeException {
    public InvalidSlotException(int slot) {
        super("Slot " + slot + " is incorrect to inventory size");
    }
}
