package com.globmed.patterns.flyweight;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Hansana
 */
public class PermissionFlyweightFactory {

    private static final Map<String, PermissionFlyweight> flyweights = new HashMap<>();

    public static PermissionFlyweight getPermission(String name, String description) {
        return flyweights.computeIfAbsent(name, k -> new PermissionFlyweight(name, description));
    }
}
