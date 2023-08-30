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
import java.util.HashSet;

import static utils.InferenceTypes.*;
import static upload.InputTypes.TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CorrectNumberOfAxiomsTest {
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
        HashSet<OWLAxiom> input = new HashSet<>();
        HashSet<OWLAxiom> inferred = new HashSet<>();

        for (OWLOntology i : reasoner.getInputOntologies()) {
            input.addAll(i.getAxioms());
        }
        for (OWLOntology i : reasoner.getInferredOntologies()) {
            inferred.addAll(i.getAxioms());
        }

        HashSet<OWLAxiom> intersection = (HashSet<OWLAxiom>)input.clone();
        intersection.retainAll(inferred);
        Integer addedNumberOfAxioms = input.size() + inferred.size() - intersection.size();
        Integer actualNumberOfAxioms = reasoner.getOutputOntology().getAxioms().size();
        assertEquals(addedNumberOfAxioms, actualNumberOfAxioms, addedNumberOfAxioms + " unequals " + actualNumberOfAxioms);
    }
}
