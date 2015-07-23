/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.csstudio.opibuilder.validation.core.SchemaVerifier;
import org.csstudio.opibuilder.validation.core.ValidationFailure;
import org.csstudio.opibuilder.validation.core.Validator;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * <code>Application</code> verifies the opi files provided by the parameters against the provided
 * schema and validation rules. The results of validation are either printed to the console or into
 * a file specified by one of the parameters.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Application implements IApplication {

    private static final char SEPARATOR = ';';
    private static final String HEADER = "PATH;WIDGET_NAME;WIDGET_TYPE;LINE_NUMBER;PROPERTY;VALUE;EXPECTED_VALUE";
//    private static final Integer EXIT_ERROR = -1;

    private static final String VALIDATION_RULES = "-rules";
    private static final String SCHEMA = "-schema";
    private static final String OPI_LOCATION = "-opilocation";
    private static final String RESULTS = "-results";
    private static final String HELP = "-help";
    private static final String VERSION = "-version";
    private static final String PRINT_RESULTS = "-print";

    private boolean deleteWhenFinished = false;

    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    @Override
    public Object start(IApplicationContext context) throws Exception {
        String rules = null;
        String location = null;
        String schema = null;
        String results = null;
        boolean printResults = false;
        final String args[] = (String[]) context.getArguments().get("application.args");
        for (int i = 0; i < args.length; i++) {
            if (HELP.equals(args[i])) {
                printHelp();
                return EXIT_OK;
            } else if (VERSION.equals(args[i])) {
                String version = (String) context.getBrandingBundle().getHeaders().get("Bundle-Version");
                System.out.println(version);
                return EXIT_OK;
            } else if (PRINT_RESULTS.equals(args[i])) {
                printResults = true;
            } else if (VALIDATION_RULES.equals(args[i])) {
                rules = args[++i];
            } else if (OPI_LOCATION.equals(args[i])) {
                location = args[++i];
            } else if (SCHEMA.equals(args[i])) {
                schema = args[++i];
            } else if (RESULTS.equals(args[i])) {
                results = args[++i];
            }
        }

        if (rules == null) {
            System.err.println("Validation Rules file is not defined!");
            printHelp();
            return EXIT_OK;
        } else if (schema == null) {
            System.err.println("OPI Schema is not defined!");
            printHelp();
            return EXIT_OK;
        } else if (location == null) {
            location = new File(".").getAbsoluteFile().getParentFile().getAbsolutePath();
        }

        Path schemaPath = new Path(new File(schema).getAbsolutePath());
        Path rulesPath = new Path(new File(rules).getAbsolutePath());
        SchemaVerifier verifier = Validator.createVerifier(schemaPath, rulesPath, null);
        File file = new File(location);
        if (!file.exists()) {
            System.err.println("The path '" + location + "' does not exist.");
            return EXIT_OK;
        }
        file = setUpLocation(file);
        check(verifier, file);
        ValidationFailure[] failures = verifier.getValidationFailures();
        if (deleteWhenFinished) {
            FileUtils.deleteDirectory(file);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Validated files: ").append(verifier.getNumberOfAnalyzedFiles()).append('\n');
        sb.append("Files with failures: ").append(verifier.getNumberOfFilesFailures()).append('\n');
        sb.append("Validated widgets: ").append(verifier.getNumberOfAnalyzedWidgets()).append('\n');
        sb.append("Widgets with failures: ").append(verifier.getNumberOfWidgetsFailures()).append('\n');
        sb.append("Validated RO properties: ").append(verifier.getNumberOfROProperties()).append('\n');
        sb.append("Critical RO failures: ").append(verifier.getNumberOfCriticalROFailures()).append('\n');
        sb.append("Major RO failures: ").append(verifier.getNumberOfMajorROFailures()).append('\n');
        sb.append("Validated WRITE properties: ").append(verifier.getNumberOfWRITEProperties()).append('\n');
        sb.append("WRITE failures: ").append(verifier.getNumberOfWRITEFailures()).append('\n');
        sb.append("Validated RW properties: ").append(verifier.getNumberOfRWProperties()).append('\n');
        sb.append("RW failures: ").append(verifier.getNumberOfRWFailures()).append('\n');
        sb.append("Deprecated properties used: ").append(verifier.getNumberOfDeprecatedFailures());

        if (printResults) {
            System.out.println(HEADER);
            for (ValidationFailure f : failures) {
                System.out.println(toMessage(f));
            }
            System.out.println(sb.toString());
        }

        if (results != null) {
            System.out.println("Results printed to file '" + new File(results).getAbsolutePath() + "'.");
            PrintWriter pw = new PrintWriter(new File(results));
            pw.println(HEADER);
            for (ValidationFailure f : failures) {
                pw.println(toMessage(f));
            }
            pw.close();

            int idx = results.lastIndexOf('.');
            String summaryFile = null;
            if (idx < 1) {
                summaryFile = results + "_summary";
            } else {
                summaryFile = results.substring(0,idx) + "_summary" + results.substring(idx);
            }
            System.out.println("Results summary printed to file '" + new File(summaryFile).getAbsolutePath() + "'.");
            pw = new PrintWriter(new File(summaryFile));
            pw.println(sb.toString());
            pw.close();
        }

        return EXIT_OK;
    }

    /**
     * Convert the validation failure into a csv format: file path; widget name; line number; property; message
     *
     * @param f the validation failure
     * @return the full message describing the failure
     */
    private String toMessage(ValidationFailure f) {
        StringBuilder sb = new StringBuilder(300);
        sb.append(f.getPath()).append(SEPARATOR).append(f.getWidgetName()).append(SEPARATOR)
            .append(f.getWidgetType()).append(SEPARATOR).append(f.getLineNumber()).append(SEPARATOR)
            .append(f.getProperty()).append(SEPARATOR).append('"').append(f.getActual()).append('"').append(SEPARATOR)
            .append('"').append(f.getExpected()).append('"');
        return sb.toString();
    }

    /**
     * Checks the given file. If the file is a directory all its children are checked. A file is only checked
     * if it is an opi file.
     *
     * @param verifier the verifier to use for checking
     * @param file the file to check
     * @throws IllegalStateException
     * @throws IOException
     */
    private void check(SchemaVerifier verifier, File file)
            throws IllegalStateException, IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    check(verifier, f);
                }
            }
        } else if (file.getAbsolutePath().toLowerCase().endsWith(".opi")) {
            System.out.println("Validating file: " + file.getAbsolutePath());
            verifier.validate(new Path(file.getAbsolutePath()));
        }
    }

    private void printHelp() {
        System.out.println("Options:");
        System.out.println(String.format("  %-30s: %s", HELP, "Print this help."));
        System.out.println(String.format("  %-30s: %s", VERSION, "Print the version number of this tool."));
        System.out.println(String.format("  %-30s: %s", VALIDATION_RULES + " <FILE>",
                "Path to the file with validation rules."));
        System.out.println(String.format("  %-30s: %s", SCHEMA + " <FILE>", "Path to the OPI schema file."));
        System.out.println(String.format("  %-30s: %s", OPI_LOCATION + " <PATH>",
                "Path to the OPI file or folder to validate. If null current directory is used."));
        System.out.println(String.format("  %-30s: %s", RESULTS + " <FILE> ",
                "Path to the file into which the results will be printed"));
        System.out.println(String.format("  %-30s: %s", PRINT_RESULTS,"Print validation results to console."));

        try {
            //if the application terminates too quickly, the framework is terminated before it was fully started
            //and some annoying stack traces are printed
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    @Override
    public void stop() {
    }

    ////////////////////////////////////////////////////////////

    private static final String PROJECT_FILE = ".project";
    private static final String XML_LINKED_RESOURCES = "linkedResources";
    private static final String XML_LINK = "link";
    private static final String XML_LOCATION = "location";
    private static final String XML_NAME = "name";
    private static final String XML_TYPE = "type";

    private static class Resource {
        final String location;
        final String destination;
        final int type;
        Resource(String location, String destination, int type) {
            this.location = location;
            this.destination = destination;
            this.type = type;
        }
    }

    private File setUpLocation(File file) throws IOException {
        if (file.isFile()) {
            return file;
        }
        //if file is a directory, search for .project files.
        //gather all .project files that contains a linked resource
        Map<File, Resource[]> links = gatherLinkedResources(new HashMap<>(),file);
        if (links.isEmpty()) {
            return file;
        }
        //if .project exists and at least one contains a linked resource tag, copy everything to a temp folder and validate there
        String tempFolder = System.getProperty("java.io.tmpdir");
        File temp = new File(tempFolder, "opivalidation");
        temp = new File(temp, file.getName());
        if (temp.exists()) {
            FileUtils.deleteDirectory(temp);
        }
        FileUtils.copyDirectory(file, temp);
        deleteWhenFinished = true;

        String absoluteLocation = file.getAbsolutePath();
        for (Map.Entry<File,Resource[]> e : links.entrySet()) {
            File project = e.getKey().getParentFile();
            String absoluteProject = project.getAbsolutePath();
            if (!absoluteProject.startsWith(absoluteLocation)) continue;
            String path = absoluteProject.substring(absoluteLocation.length());
            File destination = new File(temp, path);
            for (Resource r : e.getValue()) {
                File dest = new File(destination,r.destination);
                if (r.type == 1) {
                    FileUtils.copyFile(new File(r.location), dest);
                } else {
                    FileUtils.copyDirectory(new File(r.location), dest);
                }
            }
        }
        return temp;
    }

    private static Map<File,Resource[]> gatherLinkedResources(Map<File,Resource[]> links, File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    gatherLinkedResources(links, f);
                }
                if (PROJECT_FILE.equalsIgnoreCase(f.getName())) {
                    try (FileInputStream stream = new FileInputStream(f)) {
                        ArrayList<Resource> linkList = new ArrayList<>();
                        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
                        NodeList linkedResources = d.getElementsByTagName(XML_LINKED_RESOURCES);
                        for (int i = 0; i < linkedResources.getLength(); i++) {
                            NodeList linkItems = linkedResources.item(i).getChildNodes();
                            for (int j = 0; j < linkItems.getLength(); j++) {
                                Node node = linkItems.item(j);
                                if (XML_LINK.equals(node.getNodeName())) {
                                    NodeList linkChildren = node.getChildNodes();
                                    String location = null;
                                    String destination = null;
                                    int type = 0;
                                    for (int k = 0; k < linkChildren.getLength(); k++) {
                                        node = linkChildren.item(k);
                                        if (XML_LOCATION.equals(node.getNodeName())) {
                                            location = node.getTextContent();
                                        } else if (XML_NAME.equals(node.getNodeName())) {
                                            destination = node.getTextContent();
                                        } else if (XML_TYPE.equals(node.getNodeName())) {
                                            type = Integer.parseInt(node.getTextContent());
                                        }
                                    }
                                    if (location != null && destination != null && type != 0) {
                                        linkList.add(new Resource(location,destination,type));
                                    }
                                }
                            }
                        }
                        if (!linkList.isEmpty()) {
                            links.put(f, linkList.toArray(new Resource[linkList.size()]));
                        }
                    } catch (IOException | SAXException | ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return links;
    }
}
