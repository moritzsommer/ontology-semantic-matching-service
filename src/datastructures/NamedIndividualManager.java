package datastructures;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.HashSet;

public class NamedIndividualManager {
    private final OWLNamedIndividual individual;
    private final HashSet<OWLClassExpression> classes;
    private final HashSet<ObjectPropertyManager> objectProperties;
    private final HashSet<DataPropertyManager> dataProperties;

    public OWLNamedIndividual getIndividual() {
        return individual;
    }

    public HashSet<OWLClassExpression> getClasses() {
        return classes;
    }

    public HashSet<ObjectPropertyManager> getObjectProperties() {
        return objectProperties;
    }

    public HashSet<DataPropertyManager> getDataProperties() {
        return dataProperties;
    }

    public NamedIndividualManager(OWLNamedIndividual individual) {
        this.individual = individual;
        classes = new HashSet<>();
        objectProperties = new HashSet<>();
        dataProperties = new HashSet<>();
    }

    public void addClass(OWLClassExpression classExpression) {
        classes.add(classExpression);
    }

    public void addObjectProperty(ObjectPropertyManager value) {
        objectProperties.add(value);
    }

    public void addDataProperty(DataPropertyManager value) {
        dataProperties.add(value);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(new String(new char[150]).replace("\0", "-")).append("\n\n");
        output.append("NamedIndividual: ").append(individual.toStringID()).append("\n\n");

        output.append("Classes:\n\n");

        for (OWLClassExpression i : classes) {
            output.append(i).append("\n");
        }
        output.append("\n");
        output.append("Amount of Classes: ").append(classes.size()).append("\n\n");

        output.append("ObjectProperties:\n\n");

        for (ObjectPropertyManager i : objectProperties) {
            output.append(i).append("\n\n");
        }

        output.append("Amount of ObjectProperties: ").append(objectProperties.size()).append("\n\n");

        output.append("DataProperties:\n\n");

        for (DataPropertyManager i : dataProperties) {
            output.append(i).append("\n\n");
        }

        output.append("Amount of DataProperties: ").append(dataProperties.size()).append("\n\n");
        output.append(new String(new char[150]).replace("\0", "-"));

        return output.toString();
    }
}