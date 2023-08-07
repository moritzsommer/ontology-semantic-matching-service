package datastructures;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.HashSet;

public record MatchingScoreManager(OWLNamedIndividual individual1, OWLNamedIndividual individual2,
                            HashSet<OWLClassExpression> intersection, double score) {

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(new String(new char[150]).replace("\0", "-")).append("\n\n");
        output.append(individual1.toStringID()).append("\n");
        output.append(individual2.toStringID()).append("\n\n");

        output.append("Matching score of ").append(score).append(" due to following class intersection").append("\n\n");

        for (OWLClassExpression i : intersection) {
            output.append(i);
            output.append("\n");
        }
        if (score > 0d) {
            output.append("\n");
        }
        output.append(new String(new char[150]).replace("\0", "-"));

        return output.toString();
    }
}
