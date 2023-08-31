import upload.SimpleInput;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import utils.InferenceTypes;
import matching.Reasoner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.InferenceTypes.*;
import static upload.InputTypes.EXAMPLE;

public class IatEquivalentDeepExtendedReasoner {
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        SimpleInput input1 = new SimpleInput(
                EXAMPLE,
                "deep_iat",
                ".rdf"
        );
        SimpleInput input2 = new SimpleInput(
                EXAMPLE,
                "deep_iat_duplicate",
                ".rdf"
        );
        ArrayList<SimpleInput> input = new ArrayList<SimpleInput>(List.of(input1, input2));
        InferenceTypes[] inferenceTypes = {CLASSASSERTION, PROPERTYASSERTION};
        Reasoner r = new Reasoner(input, inferenceTypes);
        System.out.println(r);
    }
}
