package matching;

import utils.ConfigurationReader;
import utils.WrapperKey;
import utils.NoSuchIRIException;
import datastructures.DataPropertyManager;
import datastructures.MatchingScoreManager;
import datastructures.NamedIndividualManager;
import datastructures.ObjectPropertyManager;
import org.semanticweb.owlapi.model.*;

import java.io.IOException;
import java.util.*;

public class MatchingService {
    private final OWLOntology ontology;
    private final HashMap<String, NamedIndividualManager> individuals;
    private final HashMap<WrapperKey, MatchingScoreManager> matchingScores;
    private final ConfigurationReader configReader;

    public OWLOntology getOntology() {
        return ontology;
    }

    public HashMap<String, NamedIndividualManager> getIndividuals() {
        return individuals;
    }

    public HashMap<WrapperKey, MatchingScoreManager> getMatchingScores() {
        return matchingScores;
    }

    public MatchingService(Reasoner reasoner) throws NoSuchIRIException, IOException {
        ontology = reasoner.getOutputOntology();
        individuals = new HashMap<String, NamedIndividualManager>();
        matchingScores = new HashMap<WrapperKey, MatchingScoreManager>();
        configReader = new ConfigurationReader();

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
        // ToDo adapt score itself, take size of intersections into account, at least inform about it
        NamedIndividualManager manager1 = individuals.get(iri1);
        if(manager1 == null) throw new NoSuchIRIException(iri1);
        NamedIndividualManager manager2 = individuals.get(iri2);
        if(manager2 == null) throw new NoSuchIRIException(iri2);

        double numerator = 0d;
        double denominator = 0d;
        double score = 0d;
        HashSet<OWLClassExpression> classIntersection = new HashSet<OWLClassExpression>();
        HashSet<ObjectPropertyManager> objectPropIntersection = new HashSet<ObjectPropertyManager>();
        HashSet<DataPropertyManager> dataPropIntersection = new HashSet<DataPropertyManager>();

        if(configReader.isMatchingClasses()) {
            // Class intersection
            HashSet<OWLClassExpression> classSet1 = manager1.getClasses();
            HashSet<OWLClassExpression> classSet2 = manager2.getClasses();
            // Clone() used to protect classes in manager1 due to retainAll
            classIntersection = (HashSet<OWLClassExpression>) classSet1.clone();
            classIntersection.retainAll(classSet2);

            numerator += 2d*classIntersection.size();
            denominator += classSet1.size() + classSet2.size();
        }

        if(configReader.isMatchingObjectProperties()) {
            // Object property intersection
            HashSet<ObjectPropertyManager> objectPropSet1 = manager1.getObjectProperties();
            HashSet<ObjectPropertyManager> objectPropSet2 = manager2.getObjectProperties();
            // .clone() vergessen, 2 Stunden Fehlersuche, programmieren ist pain. :)
            objectPropIntersection = (HashSet<ObjectPropertyManager>) objectPropSet1.clone();
            objectPropIntersection.retainAll(objectPropSet2);

            numerator += 2d*objectPropIntersection.size();
            denominator += objectPropSet1.size() + objectPropSet2.size();
        }

        if(configReader.isMatchingDataProperties()) {
            // Data property intersection
            HashSet<DataPropertyManager> dataPropSet1 = manager1.getDataProperties();
            HashSet<DataPropertyManager> dataPropSet2 = manager2.getDataProperties();
            dataPropIntersection = (HashSet<DataPropertyManager>) dataPropSet1.clone();
            dataPropIntersection.retainAll(dataPropSet2);

            numerator += 2d*dataPropIntersection.size();
            denominator += dataPropSet1.size() + dataPropSet2.size();
        }

        if(denominator > 0) score = numerator / denominator;
        return new MatchingScoreManager(manager1.getIndividual(), manager2.getIndividual(), score, classIntersection, objectPropIntersection, dataPropIntersection);
    }

    public MatchingScoreManager matchingScore(String iri1, String iri2) throws NoSuchIRIException {
        WrapperKey key = new WrapperKey(iri1, iri2);
        MatchingScoreManager value = computeMatchingScore(iri1, iri2);
        matchingScores.put(key, value);
        return  value;
    }

    public void matchingScore() throws NoSuchIRIException {
        String[][] combinations = getAllCombinations(individuals.keySet());
        for (String[] pair : combinations) {
            matchingScore(pair[0], pair[1]);
        }
    }

    private static String[][] getAllCombinations(Set<String> inputSet) {
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

    @Override
    public String toString() {
        int counter = 0;
        StringBuilder output = new StringBuilder(new String(new char[200]).replace("\0", "-")).append("\n\n");
        output.append("All matching scores: ").append("\n\n");

        for (HashMap.Entry<WrapperKey, MatchingScoreManager> entry : matchingScores.entrySet()) {
            //if(entry.getValue().getScore() > 0.8d && entry.getValue().getScore() < 1) {
            if(entry.getValue().score() > configReader.getFilterMinimumScore()) {
                output.append(entry.getValue()).append("\n");
                counter++;
            }
        }
        output.append("\n");

        output.append("Amount of matchings with value > ").append(configReader.getFilterMinimumScore()).append(": ").append(counter).append("\n\n");
        output.append(new String(new char[200]).replace("\0", "-"));

        return output.toString();
    }
}
// ToDo IatUpload, Example for thesis
// ToDo Test for files that dont have file extension in their name
// ToDo gradle build
// ToDo test upload