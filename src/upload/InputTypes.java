package upload;

public enum InputTypes {
    EXAMPLE("example-input/"),
    TEST("test-input/"),
    REGULAR("reasoner-input/");

    private final String path;

    public String getPath() {
        return path;
    }

    InputTypes(String path) {
        this.path = path;
    }
}
