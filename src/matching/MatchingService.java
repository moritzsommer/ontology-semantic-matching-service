package matching;

import datastructures.*;
import utils.ConfigurationReader;
import utils.WrapperKey;
import utils.NoSuchIRIException;
import org.semanticweb.owlapi.model.*;

import java.util.*;

/**
 * The MatchingService class provides a semantic matching service for ontology individuals.
 * It allows users to calculate matching scores between individuals based on their classes,
 * object properties and data properties within the ontology.
 * This service is designed to work with ontologies processed by the HermiT OWL Reasoner, and it stores
 * information about individuals and their properties for matching purposes.
 *
 * @author Moritz Sommer
 * @version 1.0
 */
public class MatchingService {
    private final ConfigurationReader configReader;
    private final OWLOntology ontology;
    private final HashMap<String, NamedIndividualManager> individuals;
    private final HashMap<WrapperKey, MatchingScoreManager> matchingScores;

    public HashMap<String, NamedIndividualManager> getIndividuals() {
        return individuals;
    }

    /**
     * Initialise a MatchingService using a provided reasoner.
     *
     * @param reasoner The reasoner instance containing the inferred result ontology and configuration information.
     * @throws NoSuchIRIException If there are missing IRIs for certain individuals in the ontology.
     */
    public MatchingService(Reasoner reasoner) throws NoSuchIRIException {
        this.configReader = reasoner.getConfigReader();
        ontology = reasoner.getOutputOntology();
        individuals = new HashMap<>();
        matchingScores = new HashMap<>();

        storeNamedIndividuals();
        storeClasses();
        storeObjectProperties();
        storeDataProperties();
    }

    /**
     * Extract and store named individuals from the ontology.
     */
    private void storeNamedIndividuals() {
        // Iterate through all axioms in the ontology and pick named individuals
        for (OWLAxiom i : ontology.getAxioms()) {
            if (i.isOfType(AxiomType.DECLARATION)) {
                OWLDeclarationAxiom assertion = (OWLDeclarationAxiom) i;
                if (assertion.getEntity().isType(EntityType.NAMED_INDIVIDUAL)) {
                    // Get the named individual
                    OWLNamedIndividual individual = (OWLNamedIndividual) assertion.getEntity();
                    String individualIRI = individual.toStringID();
                    NamedIndividualManager manager = new NamedIndividualManager(individual);
                    // Store named individual
                    individuals.put(individualIRI, manager);
                }
            }
        }
    }

    /**
     * Extract, store, and associate classes with accompanying individuals in the ontology.
     *
     * @throws NoSuchIRIException If an individual referenced in a class assertion does not exist in the ontology.
     */
    private void storeClasses() throws NoSuchIRIException {
        // Iterate through all axioms in ontology and pick class assertions
        for (OWLAxiom axiom : ontology.getAxioms()) {
            if (axiom.isOfType(AxiomType.CLASS_ASSERTION)) {
                OWLClassAssertionAxiom assertion = (OWLClassAssertionAxiom) axiom;
                if (assertion.getIndividual().isNamed()) {
                    // Get accompanying individual and its IRI
                    OWLNamedIndividual individual = (OWLNamedIndividual) assertion.getIndividual();
                    String individualIRI = individual.toStringID();
                    if (individuals.containsKey(individualIRI)) {
                        // Get the class
                        OWLClassExpression classExpression = assertion.getClassExpression();
                        // Ignore OWLThing since it is the superclass of every class
                        if (!classExpression.isOWLThing()) {
                            // Store class and the association with its individual
                            individuals.get(individualIRI).addClass(classExpression);
                        }
                    } else {
                        // Can't happen in well-defined ontology
                        throw new NoSuchIRIException(individualIRI);
                    }
                }
            }
        }
    }

    /**
     * Extract, store, and associate object properties with accompanying individuals in the ontology.
     *
     * @throws NoSuchIRIException If an individual referenced in an object property assertion does not exist in the ontology.
     */
    private void storeObjectProperties() throws NoSuchIRIException {
        // Iterate through all axioms in ontology and pick object property assertions
        for (OWLAxiom axiom : ontology.getAxioms()) {
            if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
                OWLObjectPropertyAssertionAxiom assertion = (OWLObjectPropertyAssertionAxiom) axiom;
                if (assertion.getSubject().isNamed()) {
                    if (assertion.getObject().isNamed()) {
                        // Get accompanying individual and its IRI
                        OWLNamedIndividual individual = (OWLNamedIndividual) assertion.getSubject();
                        String individualIRI = individual.toStringID();
                        if (individuals.containsKey(individualIRI)) {
                            // Get object property and its value
                            OWLObjectProperty property = assertion.getProperty().getNamedProperty();
                            OWLPropertyAssertionObject assertionObject = assertion.getObject();
                            // Store object property and the association with its individual
                            ObjectPropertyManager manager = new ObjectPropertyManager(property, assertionObject);
                            individuals.get(individualIRI).addObjectProperty(manager);
                        } else {
                            // Can't happen in well-defined ontology
                            throw new NoSuchIRIException(individualIRI);
                        }
                    }
                }
            }
        }
    }

    /**
     * Extract, store and associate data properties with accompanying individuals in the ontology.
     *
     * @throws NoSuchIRIException If an individual referenced in a data property assertion does not exist in the ontology.
     */
    private void storeDataProperties() throws NoSuchIRIException {
        // Iterate through all axioms in ontology and pick data property assertions
        for (OWLAxiom axiom : ontology.getAxioms()) {
            if (axiom.isOfType(AxiomType.DATA_PROPERTY_ASSERTION)) {
                OWLDataPropertyAssertionAxiom assertion = (OWLDataPropertyAssertionAxiom) axiom;
                if (assertion.getSubject().isNamed()) {
                    // Get accompanying individual and its IRI
                    OWLNamedIndividual individual = (OWLNamedIndividual) assertion.getSubject();
                    String individualIRI = individual.toStringID();
                    if (individuals.containsKey(individualIRI)) {
                        // Get data property and its value
                        OWLDataProperty property = assertion.getProperty().asOWLDataProperty();
                        OWLPropertyAssertionObject assertionObject = assertion.getObject();
                        // Store data property and the association with its individual
                        DataPropertyManager manager = new DataPropertyManager(property, assertionObject);
                        individuals.get(individualIRI).addDataProperty(manager);
                    } else {
                        // Can't happen in well-defined ontology
                        throw new NoSuchIRIException(individualIRI);
                    }
                }
            }
        }
    }

    /**
     * Calculate the matching score between two individuals in the ontology.
     *
     * @param iri1 The IRI (Internationalized Resource Identifier) of the first individual.
     * @param iri2 The IRI of the second individual.
     * @return A MatchingScoreManager object containing the calculated matching score and related details.
     * @throws NoSuchIRIException If either of the provided IRIs is invalid or does not exist in the ontology.
     */
    private MatchingScoreManager computeMatchingScore(String iri1, String iri2) throws NoSuchIRIException {
        NamedIndividualManager manager1 = individuals.get(iri1);
        if (manager1 == null) throw new NoSuchIRIException(iri1);
        NamedIndividualManager manager2 = individuals.get(iri2);
        if (manager2 == null) throw new NoSuchIRIException(iri2);

        double numerator = 0d;
        double denominator = 0d;
        double score = 0d;

        HashSet<OWLClassExpression> classIntersection = new HashSet<>();
        HashSet<ObjectPropertyManager> objectPropIntersection = new HashSet<>();
        HashSet<DataPropertyManager> dataPropIntersection = new HashSet<>();

        HashSet<OWLClassExpression> classDifference1 = new HashSet<>();
        HashSet<OWLClassExpression> classDifference2 = new HashSet<>();
        HashSet<ObjectPropertyManager> objectPropDifference1 = new HashSet<>();
        HashSet<ObjectPropertyManager> objectPropDifference2 = new HashSet<>();
        HashSet<DataPropertyManager> dataPropDifference1 = new HashSet<>();
        HashSet<DataPropertyManager> dataPropDifference2 = new HashSet<>();

        if (configReader.isClasses()) {
            // Compute class intersection
            HashSet<OWLClassExpression> classSet1 = manager1.getClasses();
            HashSet<OWLClassExpression> classSet2 = manager2.getClasses();
            // New Set required to protect original set, retainAll deletes elements
            classIntersection = new HashSet<>(classSet1);
            classIntersection.retainAll(classSet2);
            // Compute symmetric difference for classes, sorted by individual
            if (configReader.isDiffClasses()) {
                // Those classes only associated with individual 1
                classDifference1 = new HashSet<>(classSet1);
                classDifference1.removeAll(classSet2);
                // Those classes only associated with individual 2
                classDifference2 = new HashSet<>(classSet2);
                classDifference2.removeAll(classSet1);
            }
            // Compute part of the matching score for classes
            numerator += 2d * classIntersection.size();
            denominator += classSet1.size() + classSet2.size();
        }

        // Object property intersection and data property intersection computed analogously
        if (configReader.isObjectProperties()) {
            HashSet<ObjectPropertyManager> objectPropSet1 = manager1.getObjectProperties();
            HashSet<ObjectPropertyManager> objectPropSet2 = manager2.getObjectProperties();
            objectPropIntersection = new HashSet<>(objectPropSet1);
            objectPropIntersection.retainAll(objectPropSet2);
            if (configReader.isDiffObjectProperties()) {
                objectPropDifference1 = new HashSet<>(objectPropSet1);
                objectPropDifference1.removeAll(objectPropSet2);
                objectPropDifference2 = new HashSet<>(objectPropSet2);
                objectPropDifference2.removeAll(objectPropSet1);
            }
            numerator += 2d * objectPropIntersection.size();
            denominator += objectPropSet1.size() + objectPropSet2.size();
        }

        if (configReader.isDataProperties()) {
            HashSet<DataPropertyManager> dataPropSet1 = manager1.getDataProperties();
            HashSet<DataPropertyManager> dataPropSet2 = manager2.getDataProperties();
            dataPropIntersection = new HashSet<>(dataPropSet1);
            dataPropIntersection.retainAll(dataPropSet2);
            if (configReader.isDiffDataProperties()) {
                dataPropDifference1 = new HashSet<>(dataPropSet1);
                dataPropDifference1.removeAll(dataPropSet2);
                dataPropDifference2 = new HashSet<>(dataPropSet2);
                dataPropDifference2.removeAll(dataPropSet1);
            }
            numerator += 2d * dataPropIntersection.size();
            denominator += dataPropSet1.size() + dataPropSet2.size();
        }

        // Compute overall matching score
        if (denominator > 0) score = numerator / denominator;

        return new MatchingScoreManager(
                manager1.getIndividual(),
                manager2.getIndividual(),
                score,
                classIntersection,
                objectPropIntersection,
                dataPropIntersection,
                classDifference1,
                classDifference2,
                objectPropDifference1,
                objectPropDifference2,
                dataPropDifference1,
                dataPropDifference2,
                configReader
        );
    }

    /**
     * Calculate and store the matching score between two individuals in the ontology.
     *
     * @param iri1 The IRI (Internationalized Resource Identifier) of the first individual.
     * @param iri2 The IRI of the second individual.
     * @return The MatchingScoreManager object containing the calculated matching score.
     * @throws NoSuchIRIException If either of the provided IRIs is invalid or does not exist in the ontology.
     */
    public MatchingScoreManager matchingScore(String iri1, String iri2) throws NoSuchIRIException {
        WrapperKey key = new WrapperKey(iri1, iri2);
        MatchingScoreManager value = computeMatchingScore(iri1, iri2);
        matchingScores.put(key, value);
        return value;
    }

    /**
     * Calculate and store the matching scores for all possible combinations of individuals in the ontology.
     *
     * @throws NoSuchIRIException If an Individual in the ontology does not have a valid IRI.
     */
    public void matchingScore() throws NoSuchIRIException {
        String[][] combinations = getAllCombinations(individuals.keySet());
        for (String[] pair : combinations) {
            matchingScore(pair[0], pair[1]);
        }
    }

    /**
     * Generates all possible combinations of pairs from a set of strings.
     *
     * @param inputSet A set of strings from which to create pairs.
     * @return A 2D array containing all unique combinations of pairs.
     */
    private static String[][] getAllCombinations(Set<String> inputSet) {
        List<String[]> combinations = new ArrayList<>();
        String[] inputArray = inputSet.toArray(new String[0]);
        int n = inputArray.length;
        // Iterate through the array to create pairs
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                // Create a pair and add it to the combinations list
                String[] pair = new String[2];
                pair[0] = inputArray[i];
                pair[1] = inputArray[j];
                combinations.add(pair);
            }
        }
        return combinations.toArray(new String[0][0]);
    }

    @Override
    public String toString() {
        int counter = 0;
        StringBuilder output = new StringBuilder(new String(new char[200]).replace("\0", "-")).append("\n\n");
        output.append("All matching scores:").append("\n\n");

        for (HashMap.Entry<WrapperKey, MatchingScoreManager> entry : matchingScores.entrySet()) {
            if (entry.getValue().score() > configReader.getMaximumScoreToRemove()) {
                output.append(entry.getValue()).append("\n");
                counter++;
            }
        }
        if (counter != 0) output.append("\n");

        output.append("Amount of matchings with value > ").append(configReader.getMaximumScoreToRemove()).append(": ").append(counter).append("\n\n");
        output.append(new String(new char[200]).replace("\0", "-"));

        return output.toString();
    }
}
// ToDo IatUpload, Example for thesis
// ToDo write comments
// ToDo split some long methods if time, better readable code