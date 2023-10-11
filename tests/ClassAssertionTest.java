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

import static utils.InferenceTypes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassAssertionTest {
    MatchingAlgorithm matchingAlgorithm;

    @BeforeEach
    public void setup() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
        InputStream input = new FileInputStream("test-resources/people.owl");
        Reasoner r = new Reasoner(new InferenceTypes[]{CLASSASSERTION, PROPERTYASSERTION}, new ConfigurationReader("test-config.properties"), input);
        matchingAlgorithm = new MatchingAlgorithm(r);
    }

    @Test
    public void testValid() {
        int size = matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#John_Doe").getClasses().size();
        assertEquals(4, size);
        assertEquals("[<http://www.semanticweb.org/mdebe/ontologies/example#Person>, " +
                        "<http://www.semanticweb.org/mdebe/ontologies/example#Parent>, " +
                        "<http://www.semanticweb.org/mdebe/ontologies/example#Adult>, " +
                        "<http://www.semanticweb.org/mdebe/ontologies/example#Social_Person>]",
                matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#John_Doe").getClasses().toString()
        );

        size = matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe").getClasses().size();
        assertEquals(1, size);
        assertEquals("[<http://www.semanticweb.org/mdebe/ontologies/example#Person>]",
                matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe").getClasses().toString()
        );

        size = matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Nick_Carraway").getClasses().size();
        assertEquals(2, size);
        assertEquals("[<http://www.semanticweb.org/mdebe/ontologies/example#Person>, " +
                        "<http://www.semanticweb.org/mdebe/ontologies/example#Adult>]",
                matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Nick_Carraway").getClasses().toString()
        );

        size = matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Male").getClasses().size();
        assertEquals(1, size);
        assertEquals("[<http://www.semanticweb.org/mdebe/ontologies/example#Gender>]",
                matchingAlgorithm.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Male").getClasses().toString()
        );
    }
}
