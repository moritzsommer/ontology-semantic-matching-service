import datastructures.SimpleIO;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import additional.InferenceTypes;
import core.Reasoner;

import java.io.IOException;

import static additional.InferenceTypes.*;

public class IatEquivalentDeepReasoner {
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        SimpleIO io = new SimpleIO(
                "exampleresources/",
                "reasoner-output/",
                "people",
                ".owl"
        );
        InferenceTypes[] inferenceTypes = {CLASSASSERTION, PROPERTYASSERTION};
        Reasoner r = new Reasoner(io, inferenceTypes);
        System.out.println(r);
    }
}
