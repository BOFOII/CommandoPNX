package com.bofoiii.commando.store;

import com.bofoiii.commando.exception.CommandoException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoftEnumStore {
    private static final Map<String, String[]> enums = new ConcurrentHashMap<>();

    public static String[] getEnumByName(String name) {
        return enums.get(name);
    }

    public static Map<String, String[]> getEnums() {
        return enums;
    }

    public static void addEnum(String name, String[] values) {
        enums.put(name, values);
    }

    public static void updateEnum(String enumName, String[] values) {
        if (!enums.containsKey(enumName)) {
            throw new CommandoException("Unknown enum named " + enumName);
        }
        enums.put(enumName, values);
    }

    public static void removeEnum(String enumName) {
        if (enums.remove(enumName) == null) {
            throw new CommandoException("Unknown enum named " + enumName);
        }
    }
}
