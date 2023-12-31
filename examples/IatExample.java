import matching.MatchingAlgorithm;
import matching.Reasoner;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import utils.InferenceTypes;
import utils.NoSuchIRIException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static utils.InferenceTypes.CLASSASSERTION;
import static utils.InferenceTypes.PROPERTYASSERTION;

public class IatExample {
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
        InputStream input = new FileInputStream("example-resources/pump_station.rdf");
        InputStream input2 = new FileInputStream("example-resources/control_system.rdf");

        MatchingAlgorithm m = new MatchingAlgorithm(new Reasoner(new InferenceTypes[]{CLASSASSERTION, PROPERTYASSERTION}, input, input2));
        m.matchingScore();
        System.out.println(m);
    }
}