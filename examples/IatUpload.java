import matching.MatchingService;
import matching.Reasoner;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import upload.UploadFiles;
import utils.InferenceTypes;
import utils.NoSuchIRIException;

import java.io.File;
import java.io.IOException;

import static utils.InferenceTypes.CLASSASSERTION;
import static utils.InferenceTypes.PROPERTYASSERTION;

public class IatUpload {
    public static void main(String[] args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
        UploadFiles.uploadFile(new File("example-input/people.owl"));
        UploadFiles.uploadFile(new File("example-input/deep_iat.rdf"));

        MatchingService m = new MatchingService(new Reasoner(new InferenceTypes[]{CLASSASSERTION, PROPERTYASSERTION}));
        m.matchingScore();
        System.out.println(m);
    }
}
