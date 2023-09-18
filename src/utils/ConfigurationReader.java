package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationReader {
    private final boolean classes;
    private final boolean objectProperties;
    private final boolean dataProperties;
    private final double maximumScoreToRemove;
    private final boolean diffClasses;
    private final boolean diffObjectProperties;
    private final boolean diffDataProperties;
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

    public boolean isDiffClasses() {
        return diffClasses;
    }

    public boolean isDiffObjectProperties() {
        return diffObjectProperties;
    }

    public boolean isDiffDataProperties() {
        return diffDataProperties;
    }

    public boolean isGenerateInferredOntology() {
        return generateInferredOntology;
    }

    public boolean isGenerateOutputOntology() {
        return generateOutputOntology;
    }

    public ConfigurationReader() throws IOException {
        Properties properties = new Properties();
        InputStream configFile = getClass().getClassLoader().getResourceAsStream("config.properties");
        properties.load(configFile);

       classes = Boolean.parseBoolean(properties.getProperty("matching.classes"));
       objectProperties = Boolean.parseBoolean(properties.getProperty("matching.objectProperties"));
       dataProperties = Boolean.parseBoolean(properties.getProperty("matching.dataProperties"));
       maximumScoreToRemove = Double.parseDouble(properties.getProperty("filter.maximumScoreToRemove", "0"));
       diffClasses = Boolean.parseBoolean(properties.getProperty("symmetricDifference.diffClasses"));
       diffObjectProperties = Boolean.parseBoolean(properties.getProperty("symmetricDifference.diffObjectProperties"));
       diffDataProperties = Boolean.parseBoolean(properties.getProperty("symmetricDifference.diffDataProperties"));
       generateInferredOntology = Boolean.parseBoolean(properties.getProperty("output.generateInferredOntology"));
       generateOutputOntology = Boolean.parseBoolean(properties.getProperty("output.generateOutputOntology"));

       configFile.close();
    }
}