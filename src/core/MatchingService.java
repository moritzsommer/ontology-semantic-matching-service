package core;

import additional.NoSuchIRIException;
import additional.WrapperKey;
import datastructures.MatchingScore;
import datastructures.NamedIndividualManager;
import org.semanticweb.owlapi.model.*;

import java.util.*;

public class MatchingService {
    private final Reasoner reasoner;
    // HashMap key as Strings because IRIs can be found directly, without a wrapper
    private final HashMap<String, NamedIndividualManager> individuals;
    private final HashMap<WrapperKey, MatchingScore> matchingScores;

    public Reasoner getReasoner() {
        return reasoner;
    }

    public HashMap<String, NamedIndividualManager> getIndividuals() {
        return individuals;
    }

    public HashMap<WrapperKey, MatchingScore> getMatchingScores() {
        return matchingScores;
    }

    public MatchingService(Reasoner reasoner) {
        this.reasoner = reasoner;
        individuals = new HashMap<String, NamedIndividualManager>();
        matchingScores = new HashMap<WrapperKey, MatchingScore>();
        storeIndividuals();
    }

    public static String[][] getAllCombinations(Set<String> inputSet) {
        List<String[]> combinations = new ArrayList<>();
        String[] inputArray = inputSet.toArray(new String[0]);
        int n = inputArray.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                String[] pair = new String[2];
                pair[0] = inputArray[i];
                pair[1] = inputArray[j];
                combinations.add(pair);
            }
        }
        return combinations.toArray(new String[0][0]);
    }

    private void storeIndividuals() {
        OWLOntology output = reasoner.getOutputOntology();
        // Store named individual
        for(OWLAxiom i : output.getAxioms()) {
            if(i.isOfType(AxiomType.DECLARATION)) {
                OWLDeclarationAxiom assertion = (OWLDeclarationAxiom) i;
                if(assertion.getEntity().isType(EntityType.NAMED_INDIVIDUAL)) {
                    OWLNamedIndividual individual = (OWLNamedIndividual) assertion.getEntity();
                    String individualIRI = individual.toStringID();
                    NamedIndividualManager manager = new NamedIndividualManager(individual);
                    individuals.put(individualIRI, manager);
                }
            }
        }
        // Store class assertions of named individuals
        for(OWLAxiom axiom : output.getAxioms()) {
            if(axiom.isOfType(AxiomType.CLASS_ASSERTION)) {
                OWLClassAssertionAxiom assertion = (OWLClassAssertionAxiom) axiom;
                if(assertion.getIndividual().isNamed()) {
                    OWLNamedIndividual individual = (OWLNamedIndividual) assertion.getIndividual();
                    String individualIRI = individual.toStringID();
                    OWLClassExpression classExpression = assertion.getClassExpression();
                    if(!classExpression.isOWLThing() && individuals.get(individualIRI) != null) {
                        // Ignore OWLThing since it is the superclass of every class
                        individuals.get(individualIRI).addAssertion(classExpression);
                    }
                }
            }
        }
    }

    private MatchingScore computeMatchingScore(String iri1, String iri2) throws NoSuchIRIException {
        if (!individuals.containsKey(iri1)) {
            throw new NoSuchIRIException(iri1);
        } else if (!individuals.containsKey(iri2)) {
            throw new NoSuchIRIException(iri2);
        }

        NamedIndividualManager manager1 = individuals.get(iri1);
        NamedIndividualManager manager2 = individuals.get(iri2);
        HashSet<OWLClassExpression> classSet1 = manager1.getClasses();
        HashSet<OWLClassExpression> classSet2 = manager2.getClasses();
        HashSet<OWLClassExpression> intersection = (HashSet<OWLClassExpression>) classSet1.clone();
        intersection.retainAll(classSet2);

        double score = 0d;
        if(classSet1.size() + classSet2.size() > 0) {
            score =  (2d*intersection.size()) / (classSet1.size() + classSet2.size());
        }
        return new MatchingScore(manager1.getIndividual(), manager2.getIndividual(), intersection, score);
    }

    public MatchingScore matchingScore(String iri1, String iri2) throws NoSuchIRIException {
        WrapperKey key = new WrapperKey(iri1, iri2);
        MatchingScore value = matchingScores.get(key);
        if(value == null) {
            value = computeMatchingScore(iri1, iri2);
            matchingScores.put(key, value);
        }
        return value;
    }

    public HashMap<WrapperKey, MatchingScore> matchingScore() throws NoSuchIRIException {
        String[][] combinations = getAllCombinations(individuals.keySet());
        for (String[] pair : combinations) {
            matchingScore(pair[0], pair[1]);
        }
        return matchingScores;
    }

    @Override
    public String toString() {
        int counter = 0;
        StringBuilder output = new StringBuilder(new String(new char[200]).replace("\0", "-")).append("\n\n");
        output.append("All matching scores: ").append("\n\n");

        for (HashMap.Entry<WrapperKey, MatchingScore> entry : matchingScores.entrySet()) {
            //if(entry.getValue().getScore() > 0.8d && entry.getValue().getScore() < 1) {
            if(entry.getValue().getScore() > 0d) {
                output.append(entry.getValue()).append("\n");
                counter++;
            }
        }
        output.append("\n");

        output.append("Amount of matchings with value > 0: ").append(counter).append("\n\n");
        output.append(new String(new char[200]).replace("\0", "-"));

        return output.toString();
    }
}
