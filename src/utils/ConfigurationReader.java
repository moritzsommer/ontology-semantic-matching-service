package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationReader {
    private final boolean classes;
    private final boolean objectProperties;
    private final boolean dataProperties;
    private final double maximumScoreToRemove;
    private final boolean generateInferredOntology;
    private final boolean generateOutputOntology;

    public boolean isClasses() {
        return classes;
    }

    public boolean isObjectProperties() {
        return objectProperties;
    }

    public boolean isDataProperties() {
        return dataProperties;
    }

    public double getMaximumScoreToRemove() {
        return maximumScoreToRemove;
    }

    public boolean isGenerateInferredOntology() {
        return generateInferredOntology;
    }

    public boolean isGenerateOutputOntology() {
        return generateOutputOntology;
    }

    public ConfigurationReader() throws IOException {
        // ToDo add mode for showing symmetric difference of individuals
        // ToDo adjust String if certain values dont influence the matching Score
        Properties properties = new Properties();
        InputStream configFile = getClass().getClassLoader().getResourceAsStream("config.properties");
        properties.load(configFile);

       classes = Boolean.parseBoolean(properties.getProperty("matching.classes"));
       objectProperties = Boolean.parseBoolean(properties.getProperty("matching.objectProperties"));
       dataProperties = Boolean.parseBoolean(properties.getProperty("matching.dataProperties"));
       maximumScoreToRemove = Double.parseDouble(properties.getProperty("filter.maximumScoreToRemove", "0"));
       generateInferredOntology = Boolean.parseBoolean(properties.getProperty("output.generateInferredOntology"));
       generateOutputOntology = Boolean.parseBoolean(properties.getProperty("output.generateOutputOntology"));

       configFile.close();
    }
}