package ru.otus.java.basic.june.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

public final class Settings {
    private static final Logger logger = LogManager.getLogger(Settings.class);
    private static final String CONFIG_FILE = "preferences.txt";
    private static Map<String, String> defaultSettings = getDefaultSettings();
    private static Map<String, String> userSettings = loadUserSettings();
    private static Map<String, String> finalSettings = new HashMap<>(defaultSettings);
    static {
        finalSettings.putAll(userSettings);
        logger.debug("Настройки сервера: " + finalSettings);
    }

    private Settings(String configFile) {}

    public static int getIntSettings(String key) {
        try {
            String v = finalSettings.get(key);
            return Integer.parseInt(v);
        } catch (Exception e) {
            logger.error(e);
        }
        return 0;
    }

    public static String getStringSettings(String key) {
        try {
            return finalSettings.get(key);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    private static Map<String, String> getDefaultSettings() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("port", "8189");
        defaults.put("maxRequestSize", "1024");
        defaults.put("threadPullSize", "1048576");
        return defaults;
    }

    private static Map<String, String> loadUserSettings() {
        Map<String, String> settings = new HashMap<>();
        File configFile = new File(CONFIG_FILE);

        if (!configFile.exists()) {
            logger.info("Файл настроек не найден. Используются значения по умолчанию.");
            return settings;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                line = line.split("#")[0];
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    settings.put(key, value);
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }

        return settings;
    }
}