package core;

import additional.InferenceTypes;
import datastructures.SimpleIO;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Reasoner {
    private final OWLOntology inputOntology;
    private final OWLOntology inferredOntology;
    private final OWLOntology outputOntology;
    private final OWLOntologyManager manager;
    private final InferredOntologyGenerator generator;

    public OWLOntology getInputOntology() {
        return inputOntology;
    }

    public OWLOntology getInferredOntology() {
        return inferredOntology;
    }

    public OWLOntology getOutputOntology() {
        return outputOntology;
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public InferredOntologyGenerator getGenerator() {
        return generator;
    }

    public Reasoner(SimpleIO io, InferenceTypes[] inferenceTypes) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        manager = OWLManager.createOWLOntologyManager();
        inputOntology = loadData(manager, io);
        generator = setupReasoner(inputOntology, inferenceTypes);
        inferredOntology = computeInferences(manager, generator, inputOntology.getOntologyID().getOntologyIRI(), io);
        outputOntology = computeOutputOntology(manager, inputOntology.getOntologyID().getOntologyIRI(), io);
    }

    private OWLOntology loadData(OWLOntologyManager manager, SimpleIO io) throws OWLOntologyCreationException {
        String inputPath = io.inputPath() + io.owlFileName() + io.owlFileExtension();
        File inputOntologyFile = new File(inputPath);
        return manager.loadOntologyFromOntologyDocument(inputOntologyFile);
    }

    private InferredOntologyGenerator setupReasoner(OWLOntology inputOntology, InferenceTypes[] inferenceTypes) {
        org.semanticweb.HermiT.Reasoner.ReasonerFactory factory = new org.semanticweb.HermiT.Reasoner.ReasonerFactory();
        Configuration c = new Configuration();
        c.reasonerProgressMonitor = new ConsoleProgressMonitor();
        OWLReasoner basicReasoner = factory.createReasoner(inputOntology, c);
        List<InferredAxiomGenerator<? extends OWLAxiom>> generators = defineInferenceGenerators(inferenceTypes);
        return new InferredOntologyGenerator(basicReasoner, generators);
    }

    private List<InferredAxiomGenerator<? extends OWLAxiom>> defineInferenceGenerators(InferenceTypes[] inferenceTypes) {
        List<InferredAxiomGenerator<? extends OWLAxiom>> generators = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
        for (InferenceTypes i : inferenceTypes) {
            switch (i) {
                case SUBCLASS -> generators.add(new InferredSubClassAxiomGenerator());
                case CLASSASSERTION -> generators.add(new InferredClassAssertionAxiomGenerator());
                case EQUIVALENTCLASS -> generators.add(new InferredEquivalentClassAxiomGenerator());
                case EQUIVALENTDATAPROPERTY -> generators.add(new InferredEquivalentDataPropertiesAxiomGenerator());
                case EQUIVALENTOBJECTPROPERTY -> generators.add(new InferredEquivalentObjectPropertyAxiomGenerator());
            }
        }
        return generators;
    }

    private OWLOntology computeInferences(OWLOntologyManager manager, InferredOntologyGenerator reasoner, IRI inputIri, SimpleIO io) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        IRI newIri = IRI.create(inputIri.toString() + "-inferred");
        OWLOntology inferredOntology = manager.createOntology(newIri);
        reasoner.fillOntology(manager, inferredOntology);
        String outputPath = io.outputPath() + io.owlFileName() + "-inferred" + io.owlFileExtension();
        OutputStream outputStream = initialiseStream(outputPath);
        // Only one element in manager, input ontology
        manager.saveOntology(inferredOntology, manager.getOntologyFormat(manager.getOntology(inputIri)), outputStream);
        return inferredOntology;
    }

    private OWLOntology computeOutputOntology(OWLOntologyManager manager, IRI inputIri, SimpleIO io) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        IRI newIri = IRI.create(inputIri.toString() + "-result");
        OWLOntology outputOntology = manager.createOntology(newIri, manager.getOntologies());
        String outputPath = io.outputPath() + io.owlFileName() + "-result" + io.owlFileExtension();
        OutputStream outputStream = initialiseStream(outputPath);
        // Two elements in manager, input and inferred ontologies
        manager.saveOntology(outputOntology, manager.getOntologyFormat(manager.getOntology(inputIri)), outputStream);
        return outputOntology;
    }

    private FileOutputStream initialiseStream(String path) throws IOException {
        File resultingOntologyFile = new File(path);
        if (!resultingOntologyFile.exists())
            resultingOntologyFile.createNewFile();
        resultingOntologyFile = resultingOntologyFile.getAbsoluteFile();

        return new FileOutputStream(resultingOntologyFile);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(new String(new char[200]).replace("\0", "-")).append("\n\n");
        output.append("Summary: \n\n");
        output.append(new String(new char[200]).replace("\0", "-")).append("\n\n");

        output.append("Axioms of input Ontology:\n\n");
        for (OWLAxiom i : inputOntology.getAxioms()) {
            output.append(i).append("\n");
        }
        output.append("\n");
        output.append("Amount of axioms: ").append(inputOntology.getAxioms().size()).append("\n\n");
        output.append(new String(new char[200]).replace("\0", "-")).append("\n\n");

        output.append("Axioms of inferred Ontology:\n\n");
        for (OWLAxiom i : inferredOntology.getAxioms()) {
            output.append(i).append("\n");
        }
        output.append("\n");
        output.append("Amount of axioms: ").append(inferredOntology.getAxioms().size()).append("\n\n");
        output.append(new String(new char[200]).replace("\0", "-")).append("\n\n");

        output.append("Axioms of original and inferred Ontology, duplicates:\n\n");
        Set<OWLAxiom> intersection = inputOntology.getAxioms();
        intersection.retainAll(inferredOntology.getAxioms());
        for (OWLAxiom i : intersection) {
            output.append(i).append("\n");
        }
        output.append("\n");
        output.append("Amount of axioms: ").append(intersection.size()).append("\n\n");
        output.append(new String(new char[200]).replace("\0", "-")).append("\n\n");

        output.append("Axioms of output Ontology:\n\n");
        for (OWLAxiom i : outputOntology.getAxioms()) {
            output.append(i).append("\n");
        }
        output.append("\n");
        output.append("Amount of axioms: ").append(outputOntology.getAxioms().size()).append("\n\n");
        output.append(new String(new char[200]).replace("\0", "-"));

        return output.toString();
    }
}
// Zu Ende, Datenstrukturen mit Subklasse, Oberklasse definieren, Vergleich zusammensetzen, zweite Ontologie hinzufügen
