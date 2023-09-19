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
        InputStream input1 = new FileInputStream("reasoner-input/people.owl");
        InputStream input2 = new FileInputStream("reasoner-input/iat.rdf");

        MatchingService m = new MatchingService(new Reasoner(new InferenceTypes[]{CLASSASSERTION, PROPERTYASSERTION}, input1, input2 ));
        m.matchingScore();
        System.out.println(m);
    }
}
