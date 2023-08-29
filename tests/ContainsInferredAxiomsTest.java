import additional.InferenceTypes;
import core.Reasoner;
import datastructures.SimpleIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.IOException;

import static additional.InferenceTypes.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContainsInferredAxiomsTest {
    private Reasoner reasoner;

    @BeforeEach
    public void setUp() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        SimpleIO fileInputOutput = new SimpleIO(
                "testresources/",
                "reasoner-output/",
                "deep_iat",
                ".rdf"
        );
        InferenceTypes[] inferenceTypes = {CLASSASSERTION, PROPERTYASSERTION};
        reasoner = new Reasoner(fileInputOutput, inferenceTypes);
    }

    @Test
    public void testValid() {
        for (OWLAxiom i : reasoner.getInferredOntology().getAxioms()) {
            assertTrue(reasoner.getOutputOntology().getAxioms().contains(i));
        }
    }
}