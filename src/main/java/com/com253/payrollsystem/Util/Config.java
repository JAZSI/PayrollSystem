package com.com253.payrollsystem.Util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.LoaderOptions;

/**
 * Utility for loading and reading values from a YAML config file.
 */
public class Config {

    private final Map<String, Object> root;

    /**
     * Loads YAML from the given file path.
     *
     * @param yamlPath path to the YAML config file
     * @throws IOException if file cannot be read
     * @throws IllegalArgumentException if YAML content is not a map at root level
     */
    @SuppressWarnings("unchecked")
    public Config(Path yamlPath) throws IOException {
        LoaderOptions options = new LoaderOptions();
        Yaml yaml = new Yaml(new SafeConstructor(options));

        try (InputStream inputStream = Files.newInputStream(yamlPath)) {
            Object loaded = yaml.load(inputStream);
            if (loaded == null) {
                this.root = Collections.emptyMap();
            } else if (loaded instanceof Map<?, ?> map) {
                this.root = (Map<String, Object>) map;
            } else {
                throw new IllegalArgumentException("YAML root must be a map/object.");
            }
        }
    }

    /**
     * Convenience factory for the default config.yaml in the project root.
     *
     * @return loaded YAML config reader
     * @throws IOException if file cannot be read
     */
    public static Config fromDefaultConfig() throws IOException {
        return new Config(Path.of("config.yaml"));
    }

    public String getString(String keyPath, String defaultValue) {
        Object value = getValue(keyPath);
        return value != null ? String.valueOf(value) : defaultValue;
    }

    public double getDouble(String keyPath, double defaultValue) {
        Object value = getValue(keyPath);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Double.parseDouble(stringValue);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public int getInt(String keyPath, int defaultValue) {
        Object value = getValue(keyPath);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public boolean getBoolean(String keyPath, boolean defaultValue) {
        Object value = getValue(keyPath);
        if (value instanceof Boolean boolValue) {
            return boolValue;
        }
        if (value instanceof String stringValue) {
            return Boolean.parseBoolean(stringValue);
        }
        return defaultValue;
    }

    /**
     * Reads nested YAML fields using dot notation. Example: payroll.overtime.regular_day_multiplier
     */
    @SuppressWarnings("unchecked")
    private Object getValue(String keyPath) {
        if (keyPath == null || keyPath.isBlank()) {
            return null;
        }

        String[] keys = keyPath.split("\\.");
        Object current = root;

        for (String key : keys) {
            if (!(current instanceof Map<?, ?> currentMap)) {
                return null;
            }

            current = ((Map<String, Object>) currentMap).get(key);
            if (current == null) {
                return null;
            }
        }

        return current;
    }
}
