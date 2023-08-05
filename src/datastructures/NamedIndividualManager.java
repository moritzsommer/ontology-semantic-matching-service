package datastructures;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.HashSet;

public class NamedIndividualManager {
    private final OWLNamedIndividual individual;
    private final HashSet<OWLClassExpression> classes;

    public OWLNamedIndividual getIndividual() {
        return individual;
    }

    public HashSet<OWLClassExpression> getClasses() {
        return classes;
    }

    public NamedIndividualManager(OWLNamedIndividual individual) {
        this.individual = individual;
        classes = new HashSet<OWLClassExpression>();
    }

    public void addAssertion(OWLClassExpression classExpression) {
        classes.add(classExpression);
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
        output.append(new String(new char[150]).replace("\0", "-"));

        return output.toString();
    }
}
