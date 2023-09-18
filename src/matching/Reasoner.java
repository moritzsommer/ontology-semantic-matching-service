package matching;

import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import utils.ConfigurationReader;
import utils.InferenceTypes;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Reasoner {
    private final ConfigurationReader configReader;
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

    public ConfigurationReader getConfigReader() {
        return configReader;
    }

    public Reasoner(InferenceTypes[] inferenceTypes, InputStream... input) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
        configReader = new ConfigurationReader();
        inputOntologies = new ArrayList<>();
        inferredOntologies = new ArrayList<>();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        // Identical axioms from input Ontologies removed in output Ontology, but each input needs unique IRI in rdf:about
        for (InputStream i : input) {
            OWLOntology inputOntology = manager.loadOntologyFromOntologyDocument(i);;
            InferredOntologyGenerator generator = setupReasoner(inputOntology, inferenceTypes);
            inputOntologies.add(inputOntology);
            inferredOntologies.add(computeInferences(manager, generator, inputOntology.getOntologyID().getOntologyIRI()));
        }
        if(!inputOntologies.isEmpty() && input.length > 0) {
            outputOntology = computeOutputOntology(manager, inputOntologies.get(0).getOntologyID().getOntologyIRI());
        } else {
            // Cover edge case no input ontologies
            outputOntology = computeOutputOntology(manager, IRI.create("empty"));
        }
    }

    public Reasoner(InferenceTypes[] inferenceTypes, String... input) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        this(inferenceTypes, convertStringToInputStream(input));
    }

    /**
     * Converts an array of strings into an array of InputStreams.
     *
     * @param input An array of strings to be converted into InputStreams.
     * @return An array of InputStreams containing the content of the input strings.
     */
    private static InputStream[] convertStringToInputStream(String[] input) {
        HashSet<InputStream> res = new HashSet<>();
        for (String i : input) {
            res.add(new ByteArrayInputStream(i.getBytes()));
        }
        return res.toArray(new InputStream[0]);
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
        List<InferredAxiomGenerator<? extends OWLAxiom>> generators = new ArrayList<>();
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

    private OWLOntology computeInferences(OWLOntologyManager manager, InferredOntologyGenerator reasoner, IRI inputIri) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        IRI newIri = IRI.create(inputIri.toString() + "-inferred");
        OWLOntology inferredOntology = manager.createOntology(newIri);
        reasoner.fillOntology(manager, inferredOntology);

        if(configReader.isGenerateInferredOntology()) {
            String outputPath = "reasoner-output/inferred";
            OutputStream outputStream = initialiseStream(outputPath);
            if(!manager.getOntologies().isEmpty()) {
                manager.saveOntology(inferredOntology, manager.getOntologyFormat(manager.getOntology(inputIri)), outputStream);
            } else {
                manager.saveOntology(inferredOntology, new RDFXMLOntologyFormat(), outputStream);
            }
        }

        return inferredOntology;
    }

    private OWLOntology computeOutputOntology(OWLOntologyManager manager, IRI inputIri) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        IRI newIri = IRI.create(inputIri.toString() + "-result");
        OWLOntology outputOntology = manager.createOntology(newIri, manager.getOntologies());

        if(configReader.isGenerateOutputOntology()) {
            String outputPath = "reasoner-output/result";
            OutputStream outputStream = initialiseStream(outputPath);
            if(!manager.getOntologies().isEmpty()) {
                manager.saveOntology(outputOntology, manager.getOntologyFormat(manager.getOntology(inputIri)), outputStream);
            } else {
                manager.saveOntology(outputOntology, new RDFXMLOntologyFormat(), outputStream);
            }
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
        HashSet<OWLAxiom> intersection = new HashSet<>();
        HashSet<OWLAxiom> inferred = new HashSet<>();
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
