package net.javaguides.springboot.controller;

/**
 * Holds canonical URI fragments so every controller can share the same API versioning scheme.
 */
public final class ApiPaths {

    private ApiPaths() {}

    public static final String API_PREFIX = "/api";

    public static final String API_V1_BASE = API_PREFIX + "/v1";
    public static final String API_V2_BASE = API_PREFIX + "/v2";

    public static final String V1_ACCOUNTS = API_V1_BASE + "/accounts";
    public static final String V2_ACCOUNTS = API_V2_BASE + "/accounts";
}