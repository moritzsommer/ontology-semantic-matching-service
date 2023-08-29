import additional.InferenceTypes;
import additional.NoSuchIRIException;
import core.MatchingService;
import core.Reasoner;
import datastructures.SimpleIO;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import java.io.IOException;

import static additional.InferenceTypes.*;

public class IatEquivalentDeepMatching {

    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
//        SimpleIO io = new SimpleIO(
//                "exampleresources/",
//                "reasoner-output/",
//                "deep_iat",
//                ".rdf"
//        );
        SimpleIO io = new SimpleIO(
                "exampleresources/",
                "reasoner-output/",
                "people",
                ".owl"
        );
        InferenceTypes[] inferenceTypes = {CLASSASSERTION, PROPERTYASSERTION};
        Reasoner r = new Reasoner(io, inferenceTypes);
        MatchingService m = new MatchingService(r);
        m.matchingScore();
        System.out.println(m);
    }
}
