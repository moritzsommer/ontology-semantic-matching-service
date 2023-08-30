package gateway;

import utils.NoSuchIRIException;
import matching.MatchingService;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import py4j.GatewayServer;

import java.io.IOException;

public class JavaGateway {
    private MatchingService matchingService;

    public JavaGateway() {
    }

    public void uploadOntology(String inputPath, String owlFileName, String owlFileExtension) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
//        SimpleIO io = new SimpleIO(
//                inputPath,
//                "reasoner-output/",
//                owlFileName,
//                owlFileExtension
//        );
//        InferenceTypes[] inferenceTypes = { SUBCLASS, CLASSASSERTION };
//        Reasoner reasoner = new Reasoner(io, inferenceTypes);
//        matchingService = new MatchingService(reasoner);
    }

    public float semanticMatchObjects(String iri1, String iri2) throws NoSuchIRIException {
        return (float) matchingService.matchingScore(iri1, iri2).score();
    }

//    public HashMap<WrapperKey, MatchingScore> semanticMatchAllObjects() throws NoSuchIRIException {
//        return matchingService.matchingScore();
//    }

    public String semanticMatchAllObjects() throws NoSuchIRIException {
        matchingService.matchingScore();
        return matchingService.toString();
    }

    public static void main(String[] args) {
        GatewayServer gatewayServer = new GatewayServer(new JavaGateway(), 55555);
        gatewayServer.start();
        System.out.println("Gateway server started");
    }
}