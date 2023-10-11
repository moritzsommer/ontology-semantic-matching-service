package gateway;

import matching.MatchingAlgorithm;
import matching.Reasoner;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import utils.InferenceTypes;
import utils.NoSuchIRIException;
import py4j.GatewayServer;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

import static utils.InferenceTypes.*;

public class JavaGateway {
    private MatchingAlgorithm matchingAlgorithm;
    private final HashMap<HashSet<String>, MatchingAlgorithm> buffer;

    public JavaGateway() {
        buffer = new HashMap<>();
    }

    public void uploadOntology(HashSet<String> inputOntologies) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException, NoSuchIRIException {
        if (buffer.containsKey(inputOntologies)) {
            matchingAlgorithm = buffer.get(inputOntologies);
        } else {
            InferenceTypes[] inferenceTypes = {CLASSASSERTION, PROPERTYASSERTION};
            Reasoner reasoner = new Reasoner(inferenceTypes, inputOntologies.toArray(new String[0]));
            matchingAlgorithm = new MatchingAlgorithm(reasoner);
            buffer.put(inputOntologies, matchingAlgorithm);
        }
    }

    public double semanticMatchObjects(String iri1, String iri2) throws NoSuchIRIException {
        return matchingAlgorithm.matchingScore(iri1, iri2).score();
    }

    public String semanticMatchAllObjects() throws NoSuchIRIException {
        matchingAlgorithm.matchingScore();
        return matchingAlgorithm.toString();
    }

    public static void main(String[] args) {
        GatewayServer gatewayServer = new GatewayServer(new JavaGateway(), 55555);
        gatewayServer.start();
        System.out.println("Gateway server started");
    }
}