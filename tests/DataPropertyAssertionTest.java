import utils.ConfigurationReader;
import utils.InferenceTypes;
import utils.NoSuchIRIException;
import matching.MatchingAlgorithm;
import matching.Reasoner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static utils.InferenceTypes.CLASSASSERTION;
import static utils.InferenceTypes.PROPERTYASSERTION;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataPropertyAssertionTest {
    MatchingAlgorithm matchingAlgorithm;

    @BeforeEach
    public void setup() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
        InputStream input = new FileInputStream("test-resources/people.owl");
        Reasoner r = new Reasoner(new InferenceTypes[]{CLASSASSERTION, PROPERTYASSERTION}, new ConfigurationReader("test-config.properties"), input);
        matchingAlgorithm = new MatchingAlgorithm(r);
    }

    @Test
    public void testValid() {
        int size = matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#John_Doe").getDataProperties().size();
        assertEquals(1, size);
        assertEquals("""
                        [DataProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Age>
                        AssertionObject: "34"^^xsd:integer]""",
                matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#John_Doe").getDataProperties().toString()
        );

        size = matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe").getDataProperties().size();
        assertEquals(0, size);
        assertEquals("[]",
                matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe").getDataProperties().toString()
        );

        size = matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Nick_Carraway").getDataProperties().size();
        assertEquals(1, size);
        assertEquals("""
                        [DataProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Age>
                        AssertionObject: "28"^^xsd:integer]""",
                matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Nick_Carraway").getDataProperties().toString()
        );

        size = matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Male").getDataProperties().size();
        assertEquals(0, size);
        assertEquals("[]",
                matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe").getDataProperties().toString()
        );
    }
}