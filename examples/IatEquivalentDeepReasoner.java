import upload.SimpleInput;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import utils.InferenceTypes;
import matching.Reasoner;

import java.io.IOException;

import static utils.InferenceTypes.*;
import static upload.InputTypes.EXAMPLE;

public class IatEquivalentDeepReasoner {
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        SimpleInput input = new SimpleInput(
                EXAMPLE,
                "people",
                ".owl"
        );
        InferenceTypes[] inferenceTypes = {CLASSASSERTION, PROPERTYASSERTION};
        Reasoner r = new Reasoner(input, inferenceTypes);
        System.out.println(r);
    }
}
