package datastructures;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import utils.ConfigurationReader;

import java.util.HashSet;

public record MatchingScoreManager(OWLNamedIndividual individual1, OWLNamedIndividual individual2, double score,
                                   HashSet<OWLClassExpression> classIntersection,
                                   HashSet<ObjectPropertyManager> objectPropIntersection,
                                   HashSet<DataPropertyManager> dataPropIntersection,
                                   HashSet<OWLClassExpression> classDifference1,
                                   HashSet<OWLClassExpression> classDifference2,
                                   HashSet<ObjectPropertyManager> objectPropDifference1,
                                   HashSet<ObjectPropertyManager> objectPropDifference2,
                                   HashSet<DataPropertyManager> dataPropDifference1,
                                   HashSet<DataPropertyManager> dataPropDifference2,
                                   ConfigurationReader configReader) {

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(new String(new char[150]).replace("\0", "-")).append("\n\n");
        output.append(individual1.toStringID()).append("\n");
        output.append(individual2.toStringID()).append("\n\n");

        output.append("\033[0;32m").append("Matching score of ").append("\033[1;32m").append(score).append("\033[0;32m").append(" due to ").append("\033[0m");

        if(configReader.isClasses()) {
            output.append("\033[0;32m").append("the following class intersection").append("\033[0m").append("\n\n");
            for (OWLClassExpression i : classIntersection) {
                output.append(i).append("\n");
            }
            if (classIntersection.isEmpty()) {
                output.append("no match found").append("\n");
            }
            output.append("\n");

            if(configReader.isObjectProperties() || configReader.isDataProperties()) {
                output.append("\033[0;32m").append("and ").append("\033[0m");
            }
        }

        if(configReader.isObjectProperties()) {
            output.append("\033[0;32m").append("the following object property assertion intersection").append("\033[0m").append("\n\n");

            for (ObjectPropertyManager i : objectPropIntersection) {
                output.append(i).append("\n\n");
            }
            if (objectPropIntersection.isEmpty()) {
                output.append("no match found").append("\n\n");
            }

            if(configReader.isDataProperties()) {
                output.append("\033[0;32m").append("and ").append("\033[0m");
            }
        }

        if(configReader.isDataProperties()) {
            output.append("\033[0;32m").append("the following data property assertion intersection").append("\033[0m").append("\n\n");

            for (DataPropertyManager i : dataPropIntersection) {
                output.append(i).append("\n\n");
            }
            if (dataPropIntersection.isEmpty()) {
                output.append("no match found").append("\n\n");
            }
        }

        if ((configReader.isClasses() && configReader.isDiffClasses()) ||
            (configReader.isObjectProperties() && configReader.isDiffObjectProperties()) ||
            (configReader.isDataProperties() && configReader.isDiffDataProperties())) {
            output.append("\033[0;31m").append("with ").append("\033[0m");
        }

        if(configReader.isClasses() && configReader.isDiffClasses()) {
            output.append("\033[0;31m").append("the following symmetric difference between classes").append("\033[0m").append("\n\n");

            if (!classDifference1.isEmpty()) {
                output.append(individual1).append(":\n");
                for (OWLClassExpression i : classDifference1) {
                    output.append(i).append("\n");
                }
                output.append("\n");
            }

            if (!classDifference2.isEmpty()) {
                output.append(individual2).append(":\n");
                for (OWLClassExpression i : classDifference2) {
                    output.append(i).append("\n");
                }
                output.append("\n");
            } else if (classDifference1.isEmpty()) {
                output.append("no difference found").append("\n\n");
            }

            if((configReader.isObjectProperties() && configReader.isDiffObjectProperties()) ||
               (configReader.isDataProperties() && configReader.isDiffDataProperties())){
                output.append("\033[0;31m").append("and ").append("\033[0m");
            }
        }

        if(configReader.isObjectProperties() && configReader.isDiffObjectProperties()) {
            output.append("\033[0;31m").append("the following symmetric difference between object property assertions").append("\033[0m").append("\n\n");

            if (!objectPropDifference1.isEmpty()) {
                output.append(individual1).append(":\n");
                for (ObjectPropertyManager i : objectPropDifference1) {
                    output.append(i).append("\n\n");
                }
            }

            if (!objectPropDifference2.isEmpty()) {
                output.append(individual2).append(":\n");
                for (ObjectPropertyManager i : objectPropDifference2) {
                    output.append(i).append("\n\n");
                }
            } else if (objectPropDifference1().isEmpty()) {
                output.append("no difference found").append("\n\n");
            }

            if(configReader.isDataProperties() && configReader.isDiffDataProperties()) {
                output.append("\033[0;31m").append("and ").append("\033[0m");
            }
        }

        if(configReader.isDataProperties() && configReader.isDiffDataProperties()) {
            output.append("\033[0;31m").append("the following symmetric difference between data property assertions").append("\033[0m").append("\n\n");

            if (!dataPropDifference1.isEmpty()) {
                output.append(individual1).append(":\n");
                for (DataPropertyManager i : dataPropDifference1) {
                    output.append(i).append("\n\n");
                }
            }

            if (!dataPropDifference2.isEmpty()) {
                output.append(individual2).append(":\n");
                for (DataPropertyManager i : dataPropDifference2) {
                    output.append(i).append("\n\n");
                }
            } else if (dataPropDifference1.isEmpty()) {
                output.append("no difference found").append("\n\n");
            }
        }
        output.append(new String(new char[150]).replace("\0", "-"));

        return output.toString();
    }
}