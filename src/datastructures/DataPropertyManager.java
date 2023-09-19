package datastructures;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;

public record DataPropertyManager(OWLDataProperty property, OWLPropertyAssertionObject assertionObject) {

    @Override
    public String toString() {
        return "DataProperty: " + property + "\n" + "AssertionObject: " + assertionObject;
    }
}
