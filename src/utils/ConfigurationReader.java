package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationReader {
    private final boolean matchingClasses;
    private final boolean matchingObjectProperties;
    private final boolean matchingDataProperties;
    private final double filterMinimumScore;

    public boolean isMatchingClasses() {
        return matchingClasses;
    }

    public boolean isMatchingObjectProperties() {
        return matchingObjectProperties;
    }

    public boolean isMatchingDataProperties() {
        return matchingDataProperties;
    }

    public double getFilterMinimumScore() {
        return filterMinimumScore;
    }

    public ConfigurationReader() throws IOException {
        // ToDo add mode for showing symmetric difference of individuals
        // ToDo adjust String if certain values dont influence the matching Score
        Properties properties = new Properties();
        FileInputStream configFile = new FileInputStream("config.properties");
        properties.load(configFile);

       matchingClasses = Boolean.parseBoolean(properties.getProperty("matching.classes"));
       matchingObjectProperties = Boolean.parseBoolean(properties.getProperty("matching.objectProperties"));
       matchingDataProperties = Boolean.parseBoolean(properties.getProperty("matching.dataProperties"));
       filterMinimumScore = Double.parseDouble(properties.getProperty("filter.minimumScore", "0"));

       configFile.close();
    }
}