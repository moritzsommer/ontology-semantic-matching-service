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

public class ObjectPropertyAssertionTest {
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
        int size = matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#John_Doe").getObjectProperties().size();
        assertEquals(18, size);
        assertEquals("""
                        [ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Social_Relation_With>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Tom_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Brother>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Tom_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Social_Relation_With>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Jay_Gatsby>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Child>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Wife>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Beth_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Friend>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#John_Smith>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Daughter>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Sister>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Sarah_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Sibling>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Tom_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Spouse>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Beth_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Social_Relation_With>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Social_Relation_With>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Beth_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Social_Relation_With>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Daisy_Buchanan>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Friend>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Daisy_Buchanan>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Sibling>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Sarah_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Social_Relation_With>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#John_Smith>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Social_Relation_With>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Sarah_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Friend>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Jay_Gatsby>]""",
                matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#John_Doe").getObjectProperties().toString()
        );

        size = matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe").getObjectProperties().size();
        assertEquals(10, size);
        assertEquals("""
                        [ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Gender>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Female>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Social_Relation_With>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Tom_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Sister>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Susan_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Parent>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#John_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Social_Relation_With>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#John_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Aunt>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Sarah_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Sibling>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Susan_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Social_Relation_With>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Susan_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Uncle>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Tom_Doe>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Social_Relation_With>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Sarah_Doe>]""",
                matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Mary_Doe").getObjectProperties().toString()
        );

        size = matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Nick_Carraway").getObjectProperties().size();
        assertEquals(2, size);
        assertEquals("""
                        [ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Social_Relation_With>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Jay_Gatsby>, ObjectProperty: <http://www.semanticweb.org/mdebe/ontologies/example#has_Friend>
                        AssertionObject: <http://www.semanticweb.org/mdebe/ontologies/example#Jay_Gatsby>]""",
                matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Nick_Carraway").getObjectProperties().toString()
        );

        size = matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Male").getObjectProperties().size();
        assertEquals(0, size);
        assertEquals("[]",
                matchingService.getIndividuals().get("http://www.semanticweb.org/mdebe/ontologies/example#Male").getObjectProperties().toString()
        );
    }
}
