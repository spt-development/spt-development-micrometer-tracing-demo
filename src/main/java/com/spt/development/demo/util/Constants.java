package com.spt.development.demo.util;

public final class Constants {
    private Constants() {}

    public static final class Auditing {
        private Auditing() {}

        public static class Type {
            public static final String BOOK = "Book";
            public static final String SECURITY = "Security";

            private Type() {}
        }

        public static final class SubType {
            public static final String CREATED = "CREATED";
            public static final String UPDATED = "UPDATED";
            public static final String DELETED = "DELETED";

            private SubType() {}
        }
    }
}
