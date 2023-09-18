package gateway;

import matching.Reasoner;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import utils.InferenceTypes;
import utils.NoSuchIRIException;
import matching.MatchingService;
import py4j.GatewayServer;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

import static utils.InferenceTypes.*;

public class JavaGateway {
    private MatchingService matchingService;
    private final HashMap<HashSet<String>, MatchingService> buffer;

    public JavaGateway() {
        buffer = new HashMap<>();
    }

    public void uploadOntology(HashSet<String> inputOntologies) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
        if (buffer.containsKey(inputOntologies)) {
            matchingService = buffer.get(inputOntologies);
        } else {
            InferenceTypes[] inferenceTypes = {CLASSASSERTION, PROPERTYASSERTION};
            Reasoner reasoner = new Reasoner(inferenceTypes, inputOntologies.toArray(new String[0]));
            matchingService = new MatchingService(reasoner);
            buffer.put(inputOntologies, matchingService);
        }
    }

    public double semanticMatchObjects(String iri1, String iri2) throws NoSuchIRIException {
        return matchingService.matchingScore(iri1, iri2).score();
    }

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