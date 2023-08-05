package additional;

public class NoSuchIRIException extends Exception {
    public NoSuchIRIException(String IRI) {
        super("The IRI " + IRI + " does not exist");
    }
}
