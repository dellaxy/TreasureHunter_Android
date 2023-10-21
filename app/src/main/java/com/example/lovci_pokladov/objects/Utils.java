package com.example.lovci_pokladov.objects;

import java.util.Collection;

public class Utils {
    public static boolean isNull(Object obj){
        return obj == null;
    }
    public static boolean isNotNull(String value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static boolean isNotEmpty(Object[] array) {
        return array != null && array.length > 0;
    }
}
