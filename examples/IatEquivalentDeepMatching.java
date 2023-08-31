import utils.InferenceTypes;
import utils.NoSuchIRIException;
import matching.MatchingService;
import matching.Reasoner;
import upload.SimpleInput;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import java.io.IOException;

import static utils.InferenceTypes.*;
import static upload.InputTypes.*;

public class IatEquivalentDeepMatching {
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
        SimpleInput input = new SimpleInput(
                EXAMPLE,
                "people",
                ".owl"
        );
        InferenceTypes[] inferenceTypes = {CLASSASSERTION, PROPERTYASSERTION};
        Reasoner r = new Reasoner(input, inferenceTypes);
        MatchingService m = new MatchingService(r);
        m.matchingScore();
        System.out.println(m);
    }
}
