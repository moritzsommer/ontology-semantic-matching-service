import matching.MatchingService;
import matching.Reasoner;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import utils.InferenceTypes;
import utils.NoSuchIRIException;

import java.io.File;
import java.io.IOException;

import static utils.InferenceTypes.CLASSASSERTION;
import static utils.InferenceTypes.PROPERTYASSERTION;

public class IatExample {
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
        File input1 = new File("reasoner-input/people.owl");
        File input2 = new File("reasoner-input/deep_iat.rdf");

        MatchingService m = new MatchingService(new Reasoner(new InferenceTypes[]{CLASSASSERTION, PROPERTYASSERTION}, input1, input2));
        m.matchingScore();
        System.out.println(m);
    }
}
