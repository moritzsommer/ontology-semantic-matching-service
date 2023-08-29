package datastructures;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;

public record DataPropertyManager(OWLDataProperty property, OWLPropertyAssertionObject assertionObject) {

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("DataProperty: ").append(property).append("\n");
        output.append("AssertionObject: ").append(assertionObject);

        return output.toString();
    }
}
