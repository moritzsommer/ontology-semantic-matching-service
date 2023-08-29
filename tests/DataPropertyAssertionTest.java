import additional.InferenceTypes;
import additional.NoSuchIRIException;
import core.MatchingService;
import core.Reasoner;
import datastructures.SimpleIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.IOException;

import static additional.InferenceTypes.CLASSASSERTION;
import static additional.InferenceTypes.PROPERTYASSERTION;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataPropertyAssertionTest {
    MatchingService matchingService;

    @BeforeEach
    public void setup() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
        SimpleIO io = new SimpleIO(
                "exampleresources/",
                "reasoner-output/",
                "people",
                ".owl"
        );
        InferenceTypes[] inferenceTypes = {CLASSASSERTION, PROPERTYASSERTION};
        Reasoner r = new Reasoner(io, inferenceTypes);
        matchingService = new MatchingService(r);
    }

    @Test
    public void testValid() {
        int size = matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#John_Doe").getDataProperties().size();
        assertEquals(1, size);
        assertEquals("""
                        [DataProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Age>
                        AssertionObject: "34"^^xsd:integer]""",
                matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#John_Doe").getDataProperties().toString()
        );

        size = matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe").getDataProperties().size();
        assertEquals(0, size);
        assertEquals("[]",
                matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe").getDataProperties().toString()
        );

        size = matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Nick_Carraway").getDataProperties().size();
        assertEquals(1, size);
        assertEquals("""
                        [DataProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Age>
                        AssertionObject: "28"^^xsd:integer]""",
                matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Nick_Carraway").getDataProperties().toString()
        );

        size = matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Male").getDataProperties().size();
        assertEquals(0, size);
        assertEquals("[]",
                matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe").getDataProperties().toString()
        );
    }
}
