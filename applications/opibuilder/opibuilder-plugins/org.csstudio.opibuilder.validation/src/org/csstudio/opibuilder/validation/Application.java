/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.apache.commons.io.FileUtils;
import org.csstudio.opibuilder.validation.ProjectDescription.Link;
import org.csstudio.opibuilder.validation.ProjectDescription.Variable;
import org.csstudio.opibuilder.validation.core.SchemaVerifier;
import org.csstudio.opibuilder.validation.core.ValidationFailure;
import org.csstudio.opibuilder.validation.core.Validator;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 *
 * <code>Application</code> verifies the opi files provided by the parameters against the provided schema and validation
 * rules. The results of validation are either printed to the console or into a file specified by one of the parameters.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Application implements IApplication {

    private static final char SEPARATOR = ';';
    private static final String HEADER = "PATH;WIDGET_NAME;WIDGET_TYPE;LINE_NUMBER;PROPERTY;VALUE;EXPECTED_VALUE";

    private static final String VALIDATION_RULES = "-rules";
    private static final String SCHEMA = "-schema";
    private static final String OPI_LOCATION = "-opilocation";
    private static final String RESULTS = "-results";
    private static final String HELP = "-help";
    private static final String VERSION = "-version";
    private static final String PRINT_RESULTS = "-print";
    private static final String INCLUDE_TARGET_FOLDER = "-includeTarget";

    private File targetToDelete = null;
    private boolean deleteWhenFinished = false;
    private boolean skipTargetFolder = true;

    /*
     * (non-Javadoc)
     *
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
            } else if (INCLUDE_TARGET_FOLDER.equals(args[i])) {
                skipTargetFolder = false;
            }
        }

        if (rules == null) {
            System.err.println("Validation Rules file is not defined!");
            Thread.sleep(300);
            printHelp();
            Thread.sleep(1000);
            return EXIT_OK;
        } else if (schema == null) {
            System.err.println("OPI Schema is not defined!");
            Thread.sleep(300);
            printHelp();
            Thread.sleep(1000);
            return EXIT_OK;
        } else if (location == null) {
            location = new File(".").getAbsoluteFile().getParentFile().getAbsolutePath();
        }

        Path schemaPath = new Path(new File(schema).getAbsolutePath());
        Path rulesPath = new Path(new File(rules).getAbsolutePath());
        File file = new File(location);
        if (!file.exists()) {
            System.err.println("The path '" + location + "' does not exist.");
            Thread.sleep(1000);
            return EXIT_OK;
        }

        SchemaVerifier verifier = Validator.createVerifier(schemaPath, rulesPath, null);
        System.out.println("Checking for linked resources...");
        file = setUpLocation(file);
        System.out.println("Prepared the validation target: " + file.getAbsolutePath());
        System.out.println("Validation started...");
        check(verifier, file);
        ValidationFailure[] failures = verifier.getValidationFailures();
        if (deleteWhenFinished) {
            try {
                FileUtils.deleteDirectory(targetToDelete);
            } catch (IOException e) {
                System.err.println("WARNING: " + e.getMessage());
            }
        }
        System.out.println("Validation finished.");
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
        sb.append("Deprecated properties used: ").append(verifier.getNumberOfDeprecatedFailures()).append('\n');
        sb.append("Scripts & Rules usage: \n");
        sb.append("    Jython standalone: ").append(verifier.getNumberOfPythonStandalone()).append(' ').append('(')
            .append(verifier.getNumberOfWidgetsWithPythonStandalone())
            .append(verifier.getNumberOfWidgetsWithPythonStandalone() == 1 ? " widget)\n" : " widgets)\n");
        sb.append("    Jython embedded: ").append(verifier.getNumberOfPythonEmbedded()).append(' ').append('(')
            .append(verifier.getNumberOfWidgetsWithPythonEmbedded())
            .append(verifier.getNumberOfWidgetsWithPythonEmbedded() == 1 ? " widget)\n" : " widgets)\n");
        sb.append("    Javascript standalone: ").append(verifier.getNumberOfJavascriptStandalone()).append(' ')
            .append('(').append(verifier.getNumberOfWidgetsWithJavascriptStandalone())
            .append(verifier.getNumberOfWidgetsWithJavascriptStandalone() == 1 ? " widget)\n" : " widgets)\n");
        sb.append("    Javascript embedded: ").append(verifier.getNumberOfJavascriptEmbedded()).append(' ').append('(')
            .append(verifier.getNumberOfWidgetsWithJavascriptEmbedded())
            .append(verifier.getNumberOfWidgetsWithJavascriptEmbedded() == 1 ? " widget)\n" : " widgets)\n");
        sb.append("    Rules: ").append(verifier.getNumberOfAllRules()).append(' ').append('(')
            .append(verifier.getNumberOfWidgetsWithRules())
            .append(verifier.getNumberOfWidgetsWithRules() == 1 ? " widget)\n" : " widgets)\n");

        if (printResults) {
            System.out.println(HEADER);
            for (ValidationFailure f : failures) {
                System.out.println(toMessage(f));
            }
            System.out.println(sb.toString());
        }

        if (results != null) {
            System.out.println("Results printed to file '" + new File(results).getAbsolutePath() + "'.");
            try (PrintWriter pw = new PrintWriter(new File(results))) {
                pw.println(HEADER);
                for (ValidationFailure f : failures) {
                    pw.println(toMessage(f));
                }
            }

            int idx = results.lastIndexOf('.');
            String summaryFile = null;
            if (idx < 1) {
                summaryFile = results + "_summary";
            } else {
                summaryFile = results.substring(0, idx) + "_summary" + results.substring(idx);
            }
            System.out.println("Results summary printed to file '" + new File(summaryFile).getAbsolutePath() + "'.");
            try (PrintWriter pw = new PrintWriter(new File(summaryFile))) {
                pw.println(sb.toString());
            }
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
        sb.append(f.getPath()).append(SEPARATOR).append(f.getWidgetName()).append(SEPARATOR).append(f.getWidgetType())
            .append(SEPARATOR).append(f.getLineNumber()).append(SEPARATOR).append(f.getProperty()).append(SEPARATOR)
            .append('"').append(f.getActual()).append('"').append(SEPARATOR).append('"').append(f.getExpected())
            .append('"');
        return sb.toString();
    }

    /**
     * Checks the given file. If the file is a directory all its children are checked. A file is only checked if it is
     * an opi file.
     *
     * @param verifier the verifier to use for checking
     * @param file the file to check
     * @throws IllegalStateException
     * @throws IOException
     */
    private void check(SchemaVerifier verifier, File file) throws IllegalStateException, IOException {
        if (file.isDirectory()) {
            if (skipTargetFolder && isTargetFolder(file))
                return;
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

    private static boolean isTargetFolder(File file) {
        if ("target".equalsIgnoreCase(file.getName())) {
            String[] siblings = file.getParentFile().list();
            for (String s : siblings) {
                if ("src".equalsIgnoreCase(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void printHelp() {
        System.out.println("Options:");
        System.out.println(String.format("  %-30s: %s", HELP, "Print this help."));
        System.out.println(String.format("  %-30s: %s", VERSION, "Print the version number of this tool."));
        System.out.println(
            String.format("  %-30s: %s", VALIDATION_RULES + " <FILE>", "Path to the file with validation rules."));
        System.out.println(String.format("  %-30s: %s", SCHEMA + " <FILE>", "Path to the OPI schema file."));
        System.out.println(String.format("  %-30s: %s", OPI_LOCATION + " <PATH>",
            "Path to the OPI file or folder to validate. If null current directory is used."));
        System.out.println(String.format("  %-30s: %s", RESULTS + " <FILE> ",
            "Path to the file into which the results will be printed"));
        System.out.println(String.format("  %-30s: %s", PRINT_RESULTS, "Print validation results to console."));

        try {
            // if the application terminates too quickly, the framework is terminated before it was fully started
            // and some annoying stack traces are printed
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    @Override
    public void stop() {
    }

    ////////////////////////////////////////////////////////////

    private static final String PROJECT_FILE = ".project";

    /**
     * Checks the file if it belongs to a project and if that project has any linked resources. If it does have linked
     * resources, the entire structure is copied to a temp folder and the location of the file within the temp folder is
     * returned. If there are no linked resources, the file itself is returned.
     *
     * @param file the file to check if it belongs to a project and if there are any linked resources
     * @return the location that should be checked instead of the given file (could be the same)
     * @throws IOException
     * @throws URISyntaxException
     */
    private File setUpLocation(File file) throws IOException, URISyntaxException {
        File projectFolder = traverseToProjectFolder(file.getAbsoluteFile());
        Map<File, Link[]> links = null;
        File rootToCopy = file;
        if (projectFolder == null) {
            // this is a standalone file/folder, which certainly doesn't have any linked resources
            if (file.isFile()) {
                return file;
            }

            // if file is a directory, search for .project files.
            // gather all .project files that contains a linked resource
            links = gatherLinkedResources(new HashMap<>(), file);
            if (links.isEmpty()) {
                return file;
            }
        } else {
            links = gatherLinkedResources(new HashMap<>(), projectFolder);
            if (links.isEmpty()) {
                return file;
            }
            rootToCopy = projectFolder;
        }

        // if .project exists and at least one contains a linked resource tag, copy everything to a temp folder and
        // validate there
        String tempFolder = System.getProperty("java.io.tmpdir");
        File temp = new File(tempFolder, "opivalidation");
        temp = new File(temp, rootToCopy.getName());
        if (temp.exists()) {
            FileUtils.deleteDirectory(temp);
        }
        FileUtils.copyDirectory(rootToCopy, temp);
        deleteWhenFinished = true;

        String absoluteLocation = rootToCopy.getAbsolutePath();
        for (Map.Entry<File, Link[]> e : links.entrySet()) {
            File project = e.getKey().getParentFile();
            String absoluteProject = project.getAbsolutePath();
            if (!absoluteProject.startsWith(absoluteLocation))
                continue;
            String path = absoluteProject.substring(absoluteLocation.length());
            File destination = new File(temp, path);
            for (Link r : e.getValue()) {
                File dest = new File(destination, r.getName());
                try {
                    if (r.getType() == 1) {
                        FileUtils.copyFile(r.getFile(), dest);
                    } else {
                        FileUtils.copyDirectory(r.getFile(), dest);
                    }
                } catch (FileNotFoundException ex) {
                    System.err.println("ERROR: " + ex.getMessage());
                }
            }
        }
        targetToDelete = temp;
        if (projectFolder != null) {
            String relative = file.getAbsolutePath().substring(projectFolder.getAbsolutePath().length());
            temp = new File(temp, relative);
        }
        return temp;
    }

    /**
     * Find if this file belongs to a project structure. If yes, the project folder (folder that contains the .project
     * file) is returned, otherwise null is returned.
     *
     * @param file the file to check if it belongs to a project
     * @return the project folder if it exists
     */
    private static File traverseToProjectFolder(File file) {
        if (file == null) {
            return null;
        } else if (file.isDirectory()) {
            String[] children = file.list();
            for (String s : children) {
                if (PROJECT_FILE.equals(s)) {
                    return file;
                }
            }
        }
        return traverseToProjectFolder(file.getParentFile());
    }

    /**
     * Checks the file and all its children for .project files and linked resources defined in those files.
     *
     * @param links the map to receive the links and should also be returned
     * @param file the base file to start the check with
     * @return the map containing all linked resources; key is the .project file, value is the array of links defined in
     *         that .project file
     */
    private static Map<File, Link[]> gatherLinkedResources(Map<File, Link[]> links, File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    gatherLinkedResources(links, f);
                } else if (PROJECT_FILE.equalsIgnoreCase(f.getName())) {
                    ArrayList<Link> linkList = new ArrayList<>();
                    ProjectDescription pd = JAXB.unmarshal(f, ProjectDescription.class);
                    Variable[] variables = pd.getVariables().toArray(new Variable[0]);
                    for (Link l : pd.getLinks()) {
                        String locURI = l.getLocationURI();
                        String loc = l.getLocation();
                        for (Variable v : variables) {
                            if (locURI != null && locURI.contains(v.getName())) {
                                locURI = locURI.replace(v.getName(), v.getValue());
                            }
                            if (loc != null && loc.contains(v.getName())) {
                                loc = loc.replace(v.getName(), v.getValue());
                            }
                        }
                        l.setLocationURI(locURI);
                        l.setLocation(loc);
                        linkList.add(l);
                    }
                    if (!linkList.isEmpty()) {
                        links.put(f, linkList.toArray(new Link[linkList.size()]));
                    }
                }
            }
        }
        return links;
    }
}
