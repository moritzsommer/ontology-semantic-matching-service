package matching;

import org.semanticweb.HermiT.monitor.CountingMonitor;
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
import java.util.*;


/**
 * The Reasoner class provides ontology reasoning by the HermiT OWL Reasoner.
 */
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

    /**
     * Initialises a Reasoner instance with the specified configuration reader, inference types and input ontologies.
     *
     * @param inferenceTypes An array of inference types to configure reasoning.
     * @param configReader A ConfigurationReader to configure matching.
     * @param input Any number of input streams representing ontologies to be processed.
     * @throws IOException If there is an issue with reading the input streams.
     * @throws OWLOntologyCreationException If there is an issue with creating the output ontology.
     * @throws OWLOntologyStorageException If there is an issue with storing an ontology internally.
     */
    public Reasoner(InferenceTypes[] inferenceTypes, ConfigurationReader configReader, InputStream... input)
            throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
        this.configReader = configReader;
        inputOntologies = new ArrayList<>();
        inferredOntologies = new ArrayList<>();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        // Remove duplicates from input vararg
        Set<InputStream> uniqueInput = new HashSet<>(Arrays.asList(input));
        for (InputStream i : uniqueInput) {
            // Load the input ontologies
            OWLOntology inputOntology = manager.loadOntologyFromOntologyDocument(i);
            InferredOntologyGenerator generator = setupReasoner(inputOntology, inferenceTypes);
            inputOntologies.add(inputOntology);
            inferredOntologies.add(computeInferences(manager, generator, inputOntology.getOntologyID().getOntologyIRI()));
        }
        if(!inputOntologies.isEmpty() && input.length > 0) {
            // Compute the output ontology with all inferences
            outputOntology = computeOutputOntology(manager, inputOntologies.get(0).getOntologyID().getOntologyIRI());
        } else {
            // Cover edge case no input ontologies
            outputOntology = computeOutputOntology(manager, IRI.create("empty"));
        }
    }

    /**
     * Initialises a Reasoner instance with the specified inference types and input ontologies.
     *
     * @param inferenceTypes An array of inference types to configure reasoning.
     * @param input Any number of input streams representing ontologies to be processed.
     * @throws IOException If there is an issue with reading the input streams.
     * @throws OWLOntologyCreationException If there is an issue with creating the output ontology.
     * @throws OWLOntologyStorageException If there is an issue with storing an ontology internally.
     */
    public Reasoner(InferenceTypes[] inferenceTypes, InputStream... input) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
        this(inferenceTypes, new ConfigurationReader(), input);
    }

    /**
     * Initialises a Reasoner instance with the specified inference types and input ontologies.
     *
     * @param inferenceTypes An array of inference types to configure reasoning.
     * @param input Any number of input Strings representing ontologies to be processed.
     * @throws IOException If there is an issue with reading the input streams.
     * @throws OWLOntologyCreationException If there is an issue with creating the output ontology.
     * @throws OWLOntologyStorageException If there is an issue with storing an ontology internally.
     */
    public Reasoner(InferenceTypes[] inferenceTypes, String... input) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException {
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

    /**
     * Sets up an ontology generator consisting of axiom generators for an input ontology.
     *
     * @param inputOntology The input ontology to reason over.
     * @param inferenceTypes An array of inference types to configure reasoning.
     * @return An InferredOntologyGenerator configured for reasoning.
     */
    private InferredOntologyGenerator setupReasoner(OWLOntology inputOntology, InferenceTypes[] inferenceTypes) {
        org.semanticweb.HermiT.Reasoner.ReasonerFactory factory = new org.semanticweb.HermiT.Reasoner.ReasonerFactory();
        Configuration c = new Configuration();
        c.reasonerProgressMonitor = new ConsoleProgressMonitor();
        c.tableauMonitorType = Configuration.TableauMonitorType.TIMING;
        c.monitor = new CountingMonitor();
        OWLReasoner basicReasoner = factory.createReasoner(inputOntology, c);
        List<InferredAxiomGenerator<? extends OWLAxiom>> generators = defineInferenceGenerators(inferenceTypes);
        return new InferredOntologyGenerator(basicReasoner, generators);
    }

    /**
     * Sets up axiom generators for the specified inference types.
     *
     * @param inferenceTypes An array of inference types to configure axiom generators.
     * @return A list of InferredAxiomGenerator instances for the specified inference types.
     */
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

    /**
     * Computes inferences for an input ontology, stores the inferred ontology in the OWLOntologyManager and returns it.
     *
     * @param manager An OWLOntologyManager to store ontologies.
     * @param reasoner An InferredOntologyGenerator for performing reasoning. It already contains the input ontology.
     * @param inputIri The IRI for the inferred ontology.
     * @return The inferred ontology.
     * @throws OWLOntologyCreationException If there is an issue with creating the inferred ontology.
     * @throws IOException If the inferred ontology cannot be stored in a file.
     * @throws OWLOntologyStorageException If there is an issue with storing the inferred ontology internally.
     */
    private OWLOntology computeInferences(
            OWLOntologyManager manager,
            InferredOntologyGenerator reasoner,
            IRI inputIri)
            throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        return computeOntology(manager, inputIri, reasoner);
    }

    /**
     * Combines all ontologies stored in the OWLOntologyManager, stores the output ontology in the OWLOntologyManager
     * and returns it.
     *
     * @param manager An OWLOntologyManager to store ontologies
     * @param inputIri The IRI for the output ontology.
     * @return The output ontology.
     * @throws OWLOntologyCreationException If there is an issue with creating the output ontology.
     * @throws IOException If the output ontology cannot be stored in a file.
     * @throws OWLOntologyStorageException If there is an issue with storing the inferred ontology internally.
     */
    private OWLOntology computeOutputOntology(
            OWLOntologyManager manager,
            IRI inputIri)
            throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        return computeOntology(manager, inputIri, null);
    }

    /**
     * Computes an inferred or an output ontology, stores it in the OWLOntologyManager, and returns it.
     *
     * @param manager An OWLOntologyManager to store ontologies.
     * @param inputIri The IRI for the ontology.
     * @param reasoner An InferredOntologyGenerator for performing reasoning. It can be null if not needed.
     * @return The computed ontology.
     * @throws OWLOntologyCreationException If there is an issue with creating the ontology.
     * @throws IOException If the ontology cannot be stored in a file.
     * @throws OWLOntologyStorageException If there is an issue with storing the ontology internally.
     */
    private OWLOntology computeOntology(
            OWLOntologyManager manager,
            IRI inputIri,
            InferredOntologyGenerator reasoner)
            throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        String ontologySuffix;
        if(reasoner != null) {
            ontologySuffix = "-inferred";
        } else {
            ontologySuffix = "-result";
        }

        String newIriBasis = Objects.requireNonNullElse(inputIri, "ontology") + ontologySuffix;
        String newIri = newIriBasis;

        String[] iriParts = newIriBasis.split("/");
        String outputPathBasis = iriParts[iriParts.length - 1];
        String outputPath = outputPathBasis + ".owl";

        // Compute the IRI of the ontology, avoid duplicates
        if (configReader.isGenerateOutputOntology()) {
            File checkFile = new File(outputPath);
            for (int i = 2; manager.contains(IRI.create(newIri)) || checkFile.exists(); i++) {
                newIri = newIriBasis + "-" + i;
                outputPath = outputPathBasis  + "-" + i  + ".owl";
                checkFile = new File(outputPath);
            }
        } else {
            for (int i = 2; manager.contains(IRI.create(newIri)); i++) {
                newIri = newIriBasis + "-" + i;
            }
        }

        // Create the ontology and store it in the OWLOntologyManager
        OWLOntology ontology;
        if (reasoner != null) {
            ontology = manager.createOntology(IRI.create(newIri));
            reasoner.fillOntology(manager, ontology);
        } else {
            ontology = manager.createOntology(IRI.create(newIri), manager.getOntologies());
        }

        if (configReader.isGenerateOutputOntology()) {
            OWLOntologyFormat format = new RDFXMLOntologyFormat();
            if (inputIri != null) {
                format = manager.getOntologyFormat(manager.getOntology(inputIri));
            }
            manager.saveOntology(ontology, format, initialiseStream(outputPath));
        }

        return ontology;
    }

    /**
     * Initialises and returns a FileOutputStream for a specified file path.
     *
     * @param path The file path where the output stream will be created.
     * @return A FileOutputStream for the specified file path.
     * @throws IOException If there is an issue with creating the FileOutputStream.
     */
    private FileOutputStream initialiseStream(String path) throws IOException {
        File resultingOntologyFile = new File(path);
        if (!resultingOntologyFile.exists()) {
            resultingOntologyFile.createNewFile();
        }
        resultingOntologyFile = resultingOntologyFile.getAbsoluteFile();
        return new FileOutputStream(resultingOntologyFile);
    }

    /**
     * Returns the total number of axioms in all input ontologies.
     *
     * @return The total number of axioms in the input ontologies.
     */
    public int numberOfInputAxioms() {
        int res = 0;
        for (OWLOntology i : inputOntologies) {
            res += i.getAxiomCount();
        }
        return res;
    }

    /**
     * Returns the total number of axioms in all inferred ontologies.
     *
     * @return The total number of axioms in the inferred ontologies.
     */
    public int numberOfInferredAxioms() {
        int res = 0;
        for (OWLOntology i : inferredOntologies) {
            res += i.getAxiomCount();
        }
        return res;
    }

    /**
     * Returns the total number of axioms in the output ontology.
     *
     * @return The total number of axioms in the output ontology.
     */
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