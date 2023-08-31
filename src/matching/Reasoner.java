package matching;

import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import upload.UploadFiles;
import utils.InferenceTypes;
import upload.SimpleInput;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static upload.InputTypes.REGULAR;

public class Reasoner {
    private final ArrayList<OWLOntology> inputOntologies;
    private final ArrayList<OWLOntology> inferredOntologies;
    private final OWLOntology outputOntology;

    public ArrayList<OWLOntology> getInputOntologies() {
        return inputOntologies;
    }

    public ArrayList<OWLOntology> getInferredOntologies() {
        return inferredOntologies;
    }

    public OWLOntology getOutputOntology() {
        return outputOntology;
    }

    public Reasoner(ArrayList<SimpleInput> input, InferenceTypes[] inferenceTypes) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        inputOntologies = new ArrayList<OWLOntology>();
        inferredOntologies = new ArrayList<OWLOntology>();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        // Identical axioms from input Ontologies removed in output Ontology, but each input needs unique IRI in rdf:about
        for (SimpleInput i : input) {
            OWLOntology inputOntology = loadData(manager, i);
            InferredOntologyGenerator generator = setupReasoner(inputOntology, inferenceTypes);
            inputOntologies.add(inputOntology);
            inferredOntologies.add(computeInferences(manager, generator, inputOntology.getOntologyID().getOntologyIRI(), i));
        }
        if(!inputOntologies.isEmpty() && !input.isEmpty()) {
            outputOntology = computeOutputOntology(manager, inputOntologies.get(0).getOntologyID().getOntologyIRI(), input.get(0));
        } else {
            // Cover edge case no input ontologies
            outputOntology = computeOutputOntology(manager, IRI.create("empty"), new SimpleInput(REGULAR, "empty", ".empty"));
        }
    }

    public Reasoner(SimpleInput input, InferenceTypes[] inferenceTypes) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        this(new ArrayList<SimpleInput>(Collections.singletonList(input)), inferenceTypes);
    }

    public Reasoner(InferenceTypes[] inferenceTypes) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        this(UploadFiles.getAllInputFiles(), inferenceTypes);
    }

    private OWLOntology loadData(OWLOntologyManager manager, SimpleInput input) throws OWLOntologyCreationException {
        String inputPath = input.inputType().getPath() + input.owlFileName() + input.owlFileExtension();
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
                case CLASSASSERTION -> generators.add(new InferredClassAssertionAxiomGenerator());
                case PROPERTYASSERTION -> generators.add(new InferredPropertyAssertionGenerator());
                case SUBCLASS -> generators.add(new InferredSubClassAxiomGenerator());
                case EQUIVALENTCLASS -> generators.add(new InferredEquivalentClassAxiomGenerator());
                case EQUIVALENTDATAPROPERTY -> generators.add(new InferredEquivalentDataPropertiesAxiomGenerator());
                case EQUIVALENTOBJECTPROPERTY -> generators.add(new InferredEquivalentObjectPropertyAxiomGenerator());
            }
        }
        return generators;
    }

    private OWLOntology computeInferences(OWLOntologyManager manager, InferredOntologyGenerator reasoner, IRI inputIri, SimpleInput input) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        IRI newIri = IRI.create(inputIri.toString() + "-inferred");
        OWLOntology inferredOntology = manager.createOntology(newIri);
        reasoner.fillOntology(manager, inferredOntology);
        String outputPath = "reasoner-output/" + input.owlFileName() + "-inferred" + input.owlFileExtension();
        OutputStream outputStream = initialiseStream(outputPath);
        // Only one element in manager, input ontology
        manager.saveOntology(inferredOntology, manager.getOntologyFormat(manager.getOntology(inputIri)), outputStream);
        return inferredOntology;
    }

    private OWLOntology computeOutputOntology(OWLOntologyManager manager, IRI inputIri, SimpleInput input) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        IRI newIri = IRI.create(inputIri.toString() + "-result");
        OWLOntology outputOntology = manager.createOntology(newIri, manager.getOntologies());
        String outputPath = "reasoner-output/" + input.owlFileName() + "-result" + input.owlFileExtension();
        OutputStream outputStream = initialiseStream(outputPath);
        if(!inputOntologies.isEmpty()) {
            // Two elements in manager, input and inferred ontologies
            manager.saveOntology(outputOntology, manager.getOntologyFormat(manager.getOntology(inputIri)), outputStream);
        } else {
            // Cover edge case no input ontologies
            manager.saveOntology(outputOntology, new RDFXMLOntologyFormat(), outputStream);
        }
        return outputOntology;
    }

    private FileOutputStream initialiseStream(String path) throws IOException {
        File resultingOntologyFile = new File(path);
        if (!resultingOntologyFile.exists())
            resultingOntologyFile.createNewFile();
        resultingOntologyFile = resultingOntologyFile.getAbsoluteFile();

        return new FileOutputStream(resultingOntologyFile);
    }

    public int numberOfInputAxioms() {
        int res = 0;
        for (OWLOntology i : inputOntologies) {
            res += i.getAxiomCount();
        }
        return res;
    }

    public int numberOfInferredAxioms() {
        int res = 0;
        for (OWLOntology i : inferredOntologies) {
            res += i.getAxiomCount();
        }
        return res;
    }

    public int numberOfOutputAxioms() {
        return outputOntology.getAxiomCount();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(new String(new char[200]).replace("\0", "-")).append("\n\n");
        output.append("Summary:\n\n");
        output.append(new String(new char[200]).replace("\0", "-")).append("\n\n");

        output.append("Axioms of input Ontologies:\n\n");
        int inputSize = 0;
        for(OWLOntology i : inputOntologies) {
            inputSize += i.getAxiomCount();
            for (OWLAxiom j : i.getAxioms()) {
                output.append(j).append("\n");
            }
        }
        output.append("\n");
        output.append("Amount of axioms: ").append(inputSize).append("\n\n");
        output.append(new String(new char[200]).replace("\0", "-")).append("\n\n");

        output.append("Axioms of inferred Ontology:\n\n");
        int inferredSize = 0;
        for(OWLOntology i : inferredOntologies) {
            inferredSize += i.getAxiomCount();
            for (OWLAxiom j : i.getAxioms()) {
                output.append(j).append("\n");
            }
        }
        output.append("\n");
        output.append("Amount of axioms: ").append(inferredSize).append("\n\n");
        output.append(new String(new char[200]).replace("\0", "-")).append("\n\n");

        output.append("Axioms of original and inferred Ontology, duplicates:\n\n");
        HashSet<OWLAxiom> intersection = new HashSet<OWLAxiom>();
        HashSet<OWLAxiom> inferred = new HashSet<OWLAxiom>();
        for(OWLOntology i : inputOntologies) {
            intersection.addAll(i.getAxioms());
        }
        for(OWLOntology i : inferredOntologies) {
            inferred.addAll(i.getAxioms());
        }
        intersection.retainAll(inferred);
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
        output.append("Amount of axioms: ").append(outputOntology.getAxiomCount()).append("\n\n");
        output.append(new String(new char[200]).replace("\0", "-"));

        return output.toString();
    }
}
// Zu Ende, Datenstrukturen mit Subklasse, Oberklasse definieren, Vergleich zusammensetzen, zweite Ontologie hinzufügen
