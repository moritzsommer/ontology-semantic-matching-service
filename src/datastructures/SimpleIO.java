package datastructures;

public class SimpleIO {
    private final String inputPath;
    private final String outputPath;
    private final String owlFileName;
    private final String owlFileExtension;

    public SimpleIO(String inputPath, String outputPath, String owlFileName, String owlFileExtension) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.owlFileName = owlFileName;
        this.owlFileExtension = owlFileExtension;
    }

    public String getInputPath() {
        return inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getOwlFileName() {
        return owlFileName;
    }

    public String getOwlFileExtension() {
        return owlFileExtension;
    }
}
