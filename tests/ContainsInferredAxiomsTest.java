import utils.InferenceTypes;
import matching.Reasoner;
import upload.SimpleInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.IOException;

import static utils.InferenceTypes.*;
import static upload.InputTypes.TEST;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContainsInferredAxiomsTest {
    private Reasoner reasoner;

    @BeforeEach
    public void setUp() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        SimpleInput input = new SimpleInput(
                TEST,
                "deep_iat",
                ".rdf"
        );
        InferenceTypes[] inferenceTypes = {CLASSASSERTION, PROPERTYASSERTION};
        reasoner = new Reasoner(input, inferenceTypes);
    }

    @Test
    public void testValid() {
        for (OWLOntology i : reasoner.getInferredOntologies()) {
            for (OWLAxiom j : i.getAxioms()) {
                assertTrue(reasoner.getOutputOntology().getAxioms().contains(j));
            }
        }
    }
}