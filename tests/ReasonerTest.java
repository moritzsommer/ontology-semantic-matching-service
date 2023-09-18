import utils.InferenceTypes;
import matching.Reasoner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.InferenceTypes.*;

public class ReasonerTest {
    private Reasoner reasoner;

    @BeforeEach
    public void setUp() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        InputStream input = new FileInputStream("testresources/people.owl");
        reasoner = new Reasoner(new InferenceTypes[]{CLASSASSERTION, PROPERTYASSERTION}, input);
    }

    @Test
    public void testValid() throws IOException {
        int size = reasoner.numberOfInputAxioms();
        assertEquals(135, size);

        size = reasoner.numberOfInferredAxioms();
        assertEquals(110, size);

        size = reasoner.numberOfOutputAxioms();
        assertEquals(212, size);

        assertTrue(compareStrings(readStringFromFile("testresources/people_axioms.txt"), reasoner.toString()));
    }

    private static String readStringFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path);

        StringBuilder content = new StringBuilder();
        for (String line : lines) {
            content.append(line).append("\n");
        }

        return content.toString();
    }

    // Order of Axioms in resulting Strings can vary, method checks if both strings contain same axioms
    private static boolean compareStrings(String expected, String actual) {
        String[] expectedLines = expected.split("\n");
        String[] actualLines = actual.split("\n");

        if (expectedLines.length != actualLines.length) {
            // Different number of lines
            return false;
        }

        Map<String, Integer> lineCountMap = new HashMap<>();

        for (String line : expectedLines) {
            lineCountMap.put(line, lineCountMap.getOrDefault(line, 0) + 1);
        }

        for (String line : actualLines) {
            if (!lineCountMap.containsKey(line)) {
                // Line not present in expected
                return false;
            }
            lineCountMap.put(line, lineCountMap.get(line) - 1);
        }

        return lineCountMap.values().stream().allMatch(count -> count == 0);
    }
}