package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private Properties properties;
    private String environment;

    private static final String DEFAULT_ENV = "dev";
    private static final String CONFIG_FILE_PATTERN = "config.%s.properties";

    /**
     * Private constructor to prevent direct instantiation
     */
    private ConfigManager() {
        this.environment = System.getProperty("env", DEFAULT_ENV);
        this.properties = new Properties();
        logger.info("Initializing ConfigManager for environment: " + environment);
        loadProperties();
    }

    /**
     * Get singleton instance of ConfigManager
     *
     * @return ConfigManager instance
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            logger.debug("Creating new ConfigManager instance");
            instance = new ConfigManager();
        }
        return instance;
    }

  
    private void loadProperties() {
        String configFileName = String.format(CONFIG_FILE_PATTERN, environment);
        logger.debug("Loading configuration from file: " + configFileName);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (inputStream == null) {
                logger.error("Config file not found: " + configFileName);
                throw new RuntimeException("Config file not found: " + configFileName);
            }
            properties.load(inputStream);
            logger.info("Configuration loaded successfully from: " + configFileName);
            logger.debug("Total properties loaded: " + properties.size());
        } catch (IOException e) {
            logger.error("Failed to load configuration from: " + configFileName, e);
            throw new RuntimeException("Failed to load configuration from: " + configFileName, e);
        }
    }


    public String getProperty(String key) {
        String value = properties.getProperty(key);
        logger.debug("Getting property [" + key + "] = " + (value != null ? value : "null"));
        return value;
    }

  
    public String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key, defaultValue);
        logger.debug("Getting property [" + key + "] with default [" + defaultValue + "] = " + value);
        return value;
    }


    public int getIntProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.error("Property not found: " + key);
            throw new RuntimeException("Property not found: " + key);
        }
        try {
            int intValue = Integer.parseInt(value);
            logger.debug("Getting int property [" + key + "] = " + intValue);
            return intValue;
        } catch (NumberFormatException e) {
            logger.error("Invalid integer value for property: " + key + " = " + value, e);
            throw new RuntimeException("Invalid integer value for property: " + key + " = " + value, e);
        }
    }


    public long getLongProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.error("Property not found: " + key);
            throw new RuntimeException("Property not found: " + key);
        }
        try {
            long longValue = Long.parseLong(value);
            logger.debug("Getting long property [" + key + "] = " + longValue);
            return longValue;
        } catch (NumberFormatException e) {
            logger.error("Invalid long value for property: " + key + " = " + value, e);
            throw new RuntimeException("Invalid long value for property: " + key + " = " + value, e);
        }
    }

    // ============ Selenium Grid Configuration ============

    /**
     * Get Selenium Grid URL
     *
     * @return Grid URL
     */
    public String getGridUrl() {
        return getProperty("grid.url");
    }

    /**
     * Get Grid platform (MAC, WINDOWS, LINUX)
     *
     * @return Platform name
     */
    public String getGridPlatform() {
        return getProperty("grid.platform", "MAC");
    }

    // ============ Timeout Configuration ============

    /**
     * Get default timeout in seconds
     *
     * @return Timeout value in seconds
     */
    public int getTimeoutSeconds() {
        return getIntProperty("timeout.seconds");
    }

    // ============ Google Search Configuration ============

    /**
     * Get Google Search URL
     *
     * @return Google URL
     */
    public String getGoogleUrl() {
        return getProperty("google.url");
    }

    /**
     * Get search query for Google
     *
     * @return Search query
     */
    public String getGoogleSearchQuery() {
        return getProperty("google.search.query");
    }

    /**
     * Get YouTube link text to search for on Google results
     *
     * @return Link text
     */
    public String getGoogleYouTubeLinkText() {
        return getProperty("google.youtube.link.text");
    }

    // ============ Login Test Data Configuration ============

    /**
     * Get login email for test
     *
     * @return Email address
     */
    public String getLoginEmail() {
        return getProperty("login.email");
    }

    /**
     * Get login password for test
     *
     * @return Password
     */
    public String getLoginPassword() {
        return getProperty("login.password");
    }

    // ============ YouTube Test Video Configuration ============

    /**
     * Get test video ID
     *
     * @return Video ID
     */
    public String getTestVideoId() {
        return getProperty("test.video.id");
    }

    /**
     * Get test video URL
     *
     * @return Video URL
     */
    public String getTestVideoUrl() {
        return getProperty("test.video.url");
    }

    /**
     * Get test video title
     *
     * @return Video title
     */
    public String getTestVideoTitle() {
        return getProperty("test.video.title");
    }

    /**
     * Get minimum view count threshold for test video
     *
     * @return View count
     */
    public long getTestVideoMinViews() {
        return getLongProperty("test.video.min.views");
    }

    // ============ Utility Methods ============

    /**
     * Get current environment
     *
     * @return Environment name
     */
    public String getEnvironment() {
        return environment;
    }



}

