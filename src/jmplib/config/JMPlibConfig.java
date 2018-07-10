package jmplib.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;
import java.util.Properties;

public class JMPlibConfig {

    private static JMPlibConfig _INSTANCE;

    private static final String PROPERTY_FILE_NAME = "config.properties";

    private static final String DEFAULT_ORIGINAL_CLASS_PATH = "bin/";
    private static final String DEFAULT_MODIFIED_CLASS_PATH = "generated_bin/";
    private static final String DEFAULT_ORIGINAL_SRC_PATH = "src/";
    private static final String DEFAULT_MODIFIED_SRC_PATH = "generated_src/";
    private static final String DEFAULT_POLYGLOT_PATH = "polyglot_src/";

    private static final String ORIGINAL_CLASS_PATH_KEY = "original.classpath";
    private static final String MODIFIED_CLASS_PATH_KEY = "modified.classpath";
    private static final String ORIGINAL_SRC_PATH_KEY = "original.srcpath";
    private static final String MODIFIED_SRC_PATH_KEY = "modified.srcpath";
    private static final String POLYGLOT_PATH_KEY = "polyglot.path";
    private static final String JAVA_HOME_KEY = "java.home";

    private boolean configFileExist;

    private final String originalClassPath;
    private final String modifiedClassPath;
    private final String originalSrcPath;
    private final String modifiedSrcPath;
    private final String polyglotPath;
    private final String javaHome;

    public static final String THREAD_SAFE_OPTION = "thread_safety";
    private boolean configureAsThreadSafe = false;

    public boolean isAgentLoaded() {
        return agentLoaded;
    }

    public void setAgentLoaded(boolean agentLoaded) {
        this.agentLoaded = agentLoaded;
    }

    private boolean agentLoaded = false;

    private JMPlibConfig() {
        configFileExist = new File(PROPERTY_FILE_NAME).exists();
        originalClassPath = load(ORIGINAL_CLASS_PATH_KEY).orElse(DEFAULT_ORIGINAL_CLASS_PATH);
        modifiedClassPath = load(MODIFIED_CLASS_PATH_KEY).orElse(DEFAULT_MODIFIED_CLASS_PATH);
        originalSrcPath = load(ORIGINAL_SRC_PATH_KEY).orElse(DEFAULT_ORIGINAL_SRC_PATH);
        modifiedSrcPath = load(MODIFIED_SRC_PATH_KEY).orElse(DEFAULT_MODIFIED_SRC_PATH);
        polyglotPath = load(POLYGLOT_PATH_KEY).orElse(DEFAULT_POLYGLOT_PATH);
        javaHome = load(JAVA_HOME_KEY).orElse(null);
    }

    private Optional<String> load(String key) {
        if (!configFileExist) {
            return Optional.empty();
        }
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(PROPERTY_FILE_NAME));
            return Optional.ofNullable(properties.getProperty(key));
        } catch (Exception e) {
            throw new RuntimeException("The properties file cannot be obtained", e);
        }
    }

    public static JMPlibConfig getInstance() {
        if (_INSTANCE == null) {
            _INSTANCE = new JMPlibConfig();
        }
        return _INSTANCE;
    }

    public String getOriginalClassPath() {
        return originalClassPath;
    }

    public String getModifiedClassPath() {
        return modifiedClassPath;
    }

    public String getOriginalSrcPath() {
        return originalSrcPath;
    }

    public String getModifiedSrcPath() {
        return modifiedSrcPath;
    }

    public String getPolyglotPath() {
        return polyglotPath;
    }

    public Optional<String> getJavaHome() {
        return Optional.ofNullable(javaHome);
    }

    public String getPathSeparator() {
        return System.getProperties().getProperty("path.separator");
    }

    public boolean getConfigureAsThreadSafe() {
        return configureAsThreadSafe;
    }

    public void setConfigureAsThreadSafe(boolean newValue) {
        configureAsThreadSafe = newValue;
    }
}
