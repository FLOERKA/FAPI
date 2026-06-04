package ru.floerka.api.utils;

public class IntUtils {

    public static boolean isInt(String check) {
        try {
            Integer.parseInt(check);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static IntegerChecker isIntOpt(String check) {
        try {
            int i = Integer.parseInt(check);
            return new IntegerChecker(true, i);
        } catch (NumberFormatException e) {
            return new IntegerChecker(false, -1);
        }
    }




    public static class IntegerChecker {
        private final boolean success;
        private final int integer;

        public IntegerChecker(boolean success, int integer) {
            this.success = success;
            this.integer = integer;
        }

        public IntegerChecker ifPresent(SuccessSupplier successSupplier) {
            if(success) {
                successSupplier.ifPresent(integer);
            }
            return this;
        }

        public void orElse(PostSupplier postSupplier) {
            postSupplier.post();
        }

        public interface SuccessSupplier {
            void ifPresent(int integer);
        }
        public interface PostSupplier {
            void post();
        }
    }
    public static class PostChecker {

    }
}
