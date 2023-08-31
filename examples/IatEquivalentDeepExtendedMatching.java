import utils.InferenceTypes;
import utils.NoSuchIRIException;
import matching.MatchingService;
import matching.Reasoner;
import upload.SimpleInput;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.InferenceTypes.*;
import static upload.InputTypes.*;

public class IatEquivalentDeepExtendedMatching {
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
        SimpleInput input1 = new SimpleInput(
                EXAMPLE,
                "deep_iat",
                ".rdf"
        );
        SimpleInput input2 = new SimpleInput(
                EXAMPLE,
                "deep_iat_extend",
                ".rdf"
        );
        ArrayList<SimpleInput> input = new ArrayList<>(List.of(input1, input2));
        InferenceTypes[] inferenceTypes = {CLASSASSERTION, PROPERTYASSERTION};
        Reasoner r = new Reasoner(input, inferenceTypes);
        MatchingService m = new MatchingService(r);
        m.matchingScore();
        System.out.println(m);
    }
}
