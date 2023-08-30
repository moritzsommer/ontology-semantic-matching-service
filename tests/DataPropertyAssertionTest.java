import utils.InferenceTypes;
import utils.NoSuchIRIException;
import matching.MatchingService;
import matching.Reasoner;
import upload.SimpleInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.IOException;

import static utils.InferenceTypes.CLASSASSERTION;
import static utils.InferenceTypes.PROPERTYASSERTION;
import static upload.InputTypes.TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataPropertyAssertionTest {
    MatchingService matchingService;

    @BeforeEach
    public void setup() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
        SimpleInput input = new SimpleInput(
                TEST,
                "people",
                ".owl"
        );
        InferenceTypes[] inferenceTypes = {CLASSASSERTION, PROPERTYASSERTION};
        Reasoner r = new Reasoner(input, inferenceTypes);
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
