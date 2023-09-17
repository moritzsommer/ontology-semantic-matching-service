import utils.InferenceTypes;
import matching.Reasoner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.File;
import java.io.IOException;

import static utils.InferenceTypes.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContainsInputAxiomsTest {
    private Reasoner reasoner;

    @BeforeEach
    public void setUp() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        File input = new File("testresources/deep_iat.rdf");
        reasoner = new Reasoner(new InferenceTypes[]{CLASSASSERTION, PROPERTYASSERTION}, input);
    }

    @Test
    public void testValid() {
        for (OWLOntology i : reasoner.getInputOntologies()) {
            for (OWLAxiom j : i.getAxioms()) {
                assertTrue(reasoner.getOutputOntology().getAxioms().contains(j), reasoner.getOutputOntology().getOntologyID() + " does not contain " + j);
            }
        }
    }
}
