package datastructures;

import additional.WrapperKey;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.HashMap;
import java.util.HashSet;

public class NamedIndividualManager {
    private final OWLNamedIndividual individual;
    private final HashSet<OWLClassExpression> classes;
    private final HashMap<WrapperKey, ObjectPropertyManager> objectProperties;

    public OWLNamedIndividual getIndividual() {
        return individual;
    }

    public HashSet<OWLClassExpression> getClasses() {
        return classes;
    }

    public HashMap<WrapperKey, ObjectPropertyManager> getObjectProperties() {
        return objectProperties;
    }

    public NamedIndividualManager(OWLNamedIndividual individual) {
        this.individual = individual;
        classes = new HashSet<OWLClassExpression>();
        objectProperties = new HashMap<WrapperKey, ObjectPropertyManager>();
    }

    public void addClass(OWLClassExpression classExpression) {
        classes.add(classExpression);
    }

    public boolean objectPropertiesContain(WrapperKey key) {
        return objectProperties.containsKey(key);
    }

    public void addObjectProperty(WrapperKey key, ObjectPropertyManager value) {
        objectProperties.put(key, value);
    }

    public void addObjectPropertyIndividual(WrapperKey key, OWLNamedIndividual individual) {
        objectProperties.get(key).addIndividual(individual);
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
        output.append("Amount of classes: ").append(classes.size()).append("\n\n");

        output.append("ObjectProperties:\n\n");

        for (HashMap.Entry<WrapperKey, ObjectPropertyManager> entry : objectProperties.entrySet()) {
            output.append(entry.getValue()).append("\n");
        }
        output.append("\n");

        output.append("Amount of ObjectProperties: ").append(classes.size()).append("\n\n");
        output.append(new String(new char[150]).replace("\0", "-"));

        return output.toString();
    }
}