package datastructures;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.HashSet;

public record MatchingScoreManager(OWLNamedIndividual individual1, OWLNamedIndividual individual2, double score,
                                   HashSet<OWLClassExpression> classIntersection,
                                   HashSet<ObjectPropertyManager> objectPropIntersection,
                                   HashSet<DataPropertyManager> dataPropIntersection) {

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(new String(new char[150]).replace("\0", "-")).append("\n\n");
        output.append(individual1.toStringID()).append("\n");
        output.append(individual2.toStringID()).append("\n\n");

        output.append("Matching score of ").append(score).append(" due to following class intersection").append("\n\n");

        for (OWLClassExpression i : classIntersection) {
            output.append(i);
            output.append("\n");
        }
        if (!classIntersection.isEmpty()) {
            output.append("\n");
        }

        output.append("and following object property assertion intersection").append("\n\n");

        for (ObjectPropertyManager i : objectPropIntersection) {
            output.append(i).append("\n\n");
        }

        output.append("and following data property assertion intersection").append("\n\n");

        for (DataPropertyManager i : dataPropIntersection) {
            output.append(i).append("\n\n");
        }
        output.append(new String(new char[150]).replace("\0", "-"));

        return output.toString();
    }
}