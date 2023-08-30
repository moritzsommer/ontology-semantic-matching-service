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

import static utils.InferenceTypes.*;
import static upload.InputTypes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassAssertionTest {
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
        int size = matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#John_Doe").getClasses().size();
        assertEquals(4, size);
        assertEquals("[<http://www.semanticweb.org/mdebe/ontologies/example#Person>, " +
                        "<http://www.semanticweb.org/mdebe/ontologies/example#Parent>, " +
                        "<http://www.semanticweb.org/mdebe/ontologies/example#Adult>, " +
                        "<http://www.semanticweb.org/mdebe/ontologies/example#Social_Person>]",
                matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#John_Doe").getClasses().toString()
        );

        size = matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe").getClasses().size();
        assertEquals(1, size);
        assertEquals("[<http://www.semanticweb.org/mdebe/ontologies/example#Person>]",
                matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe").getClasses().toString()
        );

        size = matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Nick_Carraway").getClasses().size();
        assertEquals(2, size);
        assertEquals("[<http://www.semanticweb.org/mdebe/ontologies/example#Person>, " +
                        "<http://www.semanticweb.org/mdebe/ontologies/example#Adult>]",
                matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Nick_Carraway").getClasses().toString()
        );

        size = matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Male").getClasses().size();
        assertEquals(1, size);
        assertEquals("[<http://www.semanticweb.org/mdebe/ontologies/example#Gender>]",
                matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Male").getClasses().toString()
        );
    }
}
