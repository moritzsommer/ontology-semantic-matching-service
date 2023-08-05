import additional.InferenceTypes;
import core.Reasoner;
import datastructures.SimpleIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.IOException;
import java.util.Set;

import static additional.InferenceTypes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContainsInputAxiomsTest {
    private Reasoner reasoner;

    @BeforeEach
    public void setUp() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        SimpleIO fileInputOutput = new SimpleIO(
                "testresources/",
                "reasoner-output/",
                "deep_iat",
                ".rdf"
        );

        InferenceTypes[] inferenceTypes = { SUBCLASS, CLASSASSERTION, EQUIVALENTCLASS, EQUIVALENTDATAPROPERTY, EQUIVALENTOBJECTPROPERTY };

        reasoner = new Reasoner(fileInputOutput, inferenceTypes);
    }

    @Test
    public void testValid() {
        Set<OWLAxiom> intersection = reasoner.getInputOntology().getAxioms();
        intersection.retainAll(reasoner.getInferredOntology().getAxioms());
        Integer addedNumberOfAxioms = reasoner.getInputOntology().getAxioms().size() + reasoner.getInferredOntology().getAxioms().size() - intersection.size();
        Integer actualNumberOfAxioms = reasoner.getOutputOntology().getAxioms().size();
        assertEquals(addedNumberOfAxioms, actualNumberOfAxioms, addedNumberOfAxioms + " unequals " + actualNumberOfAxioms);
    }
}
