package datastructures;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import java.util.HashSet;

public class ObjectPropertyManager {
    private final OWLNamedIndividual individual;
    private final OWLObjectProperty property;
    private final HashSet<OWLNamedIndividual> individuals;

    public ObjectPropertyManager(OWLNamedIndividual individual, OWLObjectProperty property, OWLNamedIndividual assertedIndividual) {
        this.individual = individual;
        this.property = property;
        individuals = new HashSet<>();
        individuals.add(assertedIndividual);
    }

    public void addIndividual(OWLNamedIndividual individual) {
        individuals.add(individual);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(new String(new char[150]).replace("\0", "-")).append("\n\n");
        output.append("NamedIndividual: ").append(individual.toStringID()).append("\n");
        output.append("ObjectProperty: ").append(property).append("\n\n");

        output.append("Individuals:\n\n");

        for (OWLNamedIndividual i : individuals) {
            output.append(i).append("\n");
        }
        output.append("\n");

        output.append("Amount of individuals: ").append(individuals.size()).append("\n\n");
        output.append(new String(new char[150]).replace("\0", "-"));

        return output.toString();
    }
}
