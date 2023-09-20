import matching.MatchingService;
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
        InputStream input = new FileInputStream("reasoner-input/empty.rdf");

        MatchingService m = new MatchingService(new Reasoner(new InferenceTypes[]{CLASSASSERTION, PROPERTYASSERTION}, input, input));
        m.matchingScore();
        System.out.println(m);

        MatchingService m2 = new MatchingService(new Reasoner(new InferenceTypes[]{CLASSASSERTION, PROPERTYASSERTION}, input, input));
        m.matchingScore();
        System.out.println(m2);
    }
}
