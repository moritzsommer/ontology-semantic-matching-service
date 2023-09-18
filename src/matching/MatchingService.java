package matching;

import datastructures.*;
import utils.ConfigurationReader;
import utils.WrapperKey;
import utils.NoSuchIRIException;
import org.semanticweb.owlapi.model.*;

import java.util.*;

public class MatchingService {
    private final ConfigurationReader configReader;
    private final OWLOntology ontology;
    private final HashMap<String, NamedIndividualManager> individuals;
    private final HashMap<WrapperKey, MatchingScoreManager> matchingScores;

    public HashMap<String, NamedIndividualManager> getIndividuals() {
        return individuals;
    }

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

    private void storeNamedIndividuals() {
        for(OWLAxiom i : ontology.getAxioms()) {
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
    }

    private void storeClasses() throws NoSuchIRIException {
        for(OWLAxiom axiom : ontology.getAxioms()) {
            if(axiom.isOfType(AxiomType.CLASS_ASSERTION)) {
                OWLClassAssertionAxiom assertion = (OWLClassAssertionAxiom) axiom;
                if(assertion.getIndividual().isNamed()) {
                    OWLNamedIndividual individual = (OWLNamedIndividual) assertion.getIndividual();
                    String individualIRI = individual.toStringID();
                    if(individuals.containsKey(individualIRI)) {
                        OWLClassExpression classExpression = assertion.getClassExpression();
                        // Ignore OWLThing since it is the superclass of every class
                        if(!classExpression.isOWLThing()) {
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

    private void storeObjectProperties() throws NoSuchIRIException {
        for(OWLAxiom axiom : ontology.getAxioms()) {
            if (axiom.isOfType(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
                OWLObjectPropertyAssertionAxiom assertion = (OWLObjectPropertyAssertionAxiom) axiom;
                if (assertion.getSubject().isNamed()) {
                    if (assertion.getObject().isNamed()) {
                        OWLNamedIndividual individual = (OWLNamedIndividual) assertion.getSubject();
                        String individualIRI = individual.toStringID();
                        if(individuals.containsKey(individualIRI)) {
                            OWLObjectProperty property = assertion.getProperty().getNamedProperty();
                            OWLPropertyAssertionObject assertionObject = assertion.getObject();
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

    private void storeDataProperties() throws NoSuchIRIException {
        for(OWLAxiom axiom : ontology.getAxioms()) {
            if (axiom.isOfType(AxiomType.DATA_PROPERTY_ASSERTION)) {
                OWLDataPropertyAssertionAxiom assertion = (OWLDataPropertyAssertionAxiom) axiom;
                if (assertion.getSubject().isNamed()) {
                    OWLNamedIndividual individual = (OWLNamedIndividual) assertion.getSubject();
                    String individualIRI = individual.toStringID();
                    if(individuals.containsKey(individualIRI)) {
                        OWLDataProperty property = assertion.getProperty().asOWLDataProperty();
                        OWLPropertyAssertionObject assertionObject = assertion.getObject();
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

    private MatchingScoreManager computeMatchingScore(String iri1, String iri2) throws NoSuchIRIException {
        NamedIndividualManager manager1 = individuals.get(iri1);
        if(manager1 == null) throw new NoSuchIRIException(iri1);
        NamedIndividualManager manager2 = individuals.get(iri2);
        if(manager2 == null) throw new NoSuchIRIException(iri2);

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

        if(configReader.isClasses()) {
            // Compute class intersection
            HashSet<OWLClassExpression> classSet1 = manager1.getClasses();
            HashSet<OWLClassExpression> classSet2 = manager2.getClasses();
            // New Set required to protect original set, retainAll deletes elements
            classIntersection = new HashSet<>(classSet1);
            classIntersection.retainAll(classSet2);
            // Compute symmetric difference for classes, sorted by individual
            if(configReader.isDiffClasses()) {
                classDifference1 = new HashSet<>(classSet1);
                classDifference1.removeAll(classSet2);
                classDifference2 = new HashSet<>(classSet2);
                classDifference2.removeAll(classSet1);
            }
            // Compute part of the matching score
            numerator += 2d*classIntersection.size();
            denominator += classSet1.size() + classSet2.size();
        }

        if(configReader.isObjectProperties()) {
            // Compute object property intersection
            HashSet<ObjectPropertyManager> objectPropSet1 = manager1.getObjectProperties();
            HashSet<ObjectPropertyManager> objectPropSet2 = manager2.getObjectProperties();
            objectPropIntersection = new HashSet<>(objectPropSet1);
            objectPropIntersection.retainAll(objectPropSet2);
            // Compute symmetric difference for object properties, sorted by individual
            if(configReader.isDiffObjectProperties()) {
                objectPropDifference1 = new HashSet<>(objectPropSet1);
                objectPropDifference1.removeAll(objectPropSet2);
                objectPropDifference2 = new HashSet<>(objectPropSet2);
                objectPropDifference2.removeAll(objectPropSet1);
            }
            // Compute part of the matching score
            numerator += 2d*objectPropIntersection.size();
            denominator += objectPropSet1.size() + objectPropSet2.size();
        }

        if(configReader.isDataProperties()) {
            // Compute data property intersection
            HashSet<DataPropertyManager> dataPropSet1 = manager1.getDataProperties();
            HashSet<DataPropertyManager> dataPropSet2 = manager2.getDataProperties();
            dataPropIntersection = new HashSet<>(dataPropSet1);
            dataPropIntersection.retainAll(dataPropSet2);
            // Compute symmetric difference for object properties, sorted by individual
            if(configReader.isDiffDataProperties()) {
                dataPropDifference1 = new HashSet<>(dataPropSet1);
                dataPropDifference1.removeAll(dataPropSet2);
                dataPropDifference2 = new HashSet<>(dataPropSet2);
                dataPropDifference2.removeAll(dataPropSet1);
            }
            // Compute part of the matching score
            numerator += 2d*dataPropIntersection.size();
            denominator += dataPropSet1.size() + dataPropSet2.size();
        }

        // Compute overall matching score
        if(denominator > 0) score = numerator / denominator;

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

    public MatchingScoreManager matchingScore(String iri1, String iri2) throws NoSuchIRIException {
        WrapperKey key = new WrapperKey(iri1, iri2);
        MatchingScoreManager value = computeMatchingScore(iri1, iri2);
        matchingScores.put(key, value);
        return  value;
    }

    /**
     * Calculate matching scores for all possible combinations of individuals in the ontology.
     * This method iterates through all unique pairs of individuals and computes their matching scores.
     *
     * @throws NoSuchIRIException If an Individual does not have a valid IRI (Internationalized Resource Identifier).
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
        output.append("All matching scores: ").append("\n\n");

        for (HashMap.Entry<WrapperKey, MatchingScoreManager> entry : matchingScores.entrySet()) {
            if(entry.getValue().score() > configReader.getMaximumScoreToRemove()) {
                output.append(entry.getValue()).append("\n");
                counter++;
            }
        }
        if(counter != 0) output.append("\n");

        output.append("Amount of matchings with value > ").append(configReader.getMaximumScoreToRemove()).append(": ").append(counter).append("\n\n");
        output.append(new String(new char[200]).replace("\0", "-"));

        return output.toString();
    }
}
// ToDo IatUpload, Example for thesis