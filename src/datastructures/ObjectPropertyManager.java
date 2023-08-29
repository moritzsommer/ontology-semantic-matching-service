package datastructures;

import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;

public record ObjectPropertyManager(OWLObjectProperty property, OWLPropertyAssertionObject assertionObject) {

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("ObjectProperty: ").append(property).append("\n");
        output.append("AssertionObject: ").append(assertionObject);

        return output.toString();
    }
}