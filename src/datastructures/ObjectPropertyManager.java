package datastructures;

import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;

public record ObjectPropertyManager(OWLObjectProperty property, OWLPropertyAssertionObject assertionObject) {

    @Override
    public String toString() {
        return "ObjectProperty: " + property + "\n" + "AssertionObject: " + assertionObject;
    }
}