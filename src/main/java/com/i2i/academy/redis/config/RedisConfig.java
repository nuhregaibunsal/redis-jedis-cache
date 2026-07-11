package com.i2i.academy.redis.config;

/**
 * Connection settings for the local Redis container.
 * Values fall back to sensible defaults but can be overridden with
 * environment variables, which keeps the code free of hardcoded hosts.
 */
public final class RedisConfig {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 6379;

    private final String host;
    private final int port;

    private RedisConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static RedisConfig fromEnvironment() {
        String host = envOrDefault("REDIS_HOST", DEFAULT_HOST);
        int port = Integer.parseInt(envOrDefault("REDIS_PORT", String.valueOf(DEFAULT_PORT)));
        return new RedisConfig(host, port);
    }

    private static String envOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
