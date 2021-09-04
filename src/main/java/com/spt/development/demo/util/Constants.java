package com.spt.development.demo.util;

public interface Constants {
    interface Auditing {
        interface Type {
            String BOOK = "Book";
            String SECURITY = "Security";
        }

        interface SubType {
            String CREATED = "CREATED";
            String UPDATED = "UPDATED";
            String DELETED = "DELETED";
        }
    }
}
