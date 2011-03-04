/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter;

import java.io.File;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.writer.OpiWriter;

/**
 * Main class for running edm2xml converter from command line.
 * @author Matevz
 */
public class EdmConverter {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.EdmConverter");
	
	private static boolean displayHelp = false;
	private static boolean robustParsing = false;
	private static boolean colorDefOutput = false;
	private static String edlFileName = null;
	private static String opiFileName = null;
	
	private static final String edlExtension = ".edl";
	private static final String opiExtension = ".opi";

	private static final String defaultColorsFileName = "colors.list";
	private static final String defaultColorsOutputName = "color.def";
	
	public static void main(String[] args) throws EdmException {

		// Default options
		displayHelp = false;
		robustParsing = true;
		colorDefOutput = false;
		edlFileName = null;
		opiFileName = null;
		
		parseArguments(args);
		
		if (displayHelp) {
			printHelp();
			System.exit(0);
		}
		
		validateEdlFileName();
		validateOpiFileName();
		
		validateEdmFiles();
		validateColorsFile();
		validateColorsOutput();
		validateRobustParsing();
		
		OpiWriter writer = OpiWriter.getInstance();
		
		writer.writeDisplayFile(edlFileName, opiFileName);
		
		if (colorDefOutput) {
			writer.writeColorDef(System.getProperty("edm2xml.colorsOutput"));
		}
	}

	public static void parseArguments(String[] args) throws EdmException {
		for (int argIndex = 0; argIndex < args.length; argIndex++) {
			
			String argument = args[argIndex].trim();
			
			if (argument.startsWith("-")) {
				String option = argument.substring(1);
				parseOption(option);
			} else if (edlFileName == null) {
				edlFileName = argument;
			} else if (opiFileName == null) {
				opiFileName = argument;
			} else {
				String errorMessage = "Too many arguments: '" + argument + "'.";
				log.error(errorMessage);
				System.err.println("Error: " + errorMessage);
				System.out.println("Type " + EdmConverter.class.getSimpleName() + " -help for command arguments.");

				System.exit(1);
			}
		}
	}
	
	public static void parseOption(String option) throws EdmException {
		if (option.equals("help")) {
			displayHelp = true;
		} else if (option.equals("r")) {
			robustParsing = false;
		} else if (option.equals("c")) {
			colorDefOutput = true;
		} else {
			String errorMessage = "Unknown option -" + option + ".";
			log.error(errorMessage);

			System.err.println("Error: " + errorMessage);
			System.out.println("Type " + EdmConverter.class.getSimpleName()
					+ " -help for command arguments.");
					
			System.exit(1);
		}
	}

	private static void validateEdlFileName() {
		if (edlFileName == null) {
			String errorMessage = "EDL input file not specified.";
			log.error(errorMessage);
			System.err.println("Error: " + errorMessage);
			System.exit(1);
		}

		File edlFile = new File(edlFileName);
		if (!edlFile.exists()) {
			String errorMessage = "File " + edlFileName + " does not exist.";
			log.error(errorMessage);
			System.err.println("Error: " + errorMessage);
			System.exit(1);
		}
	}

	private static void validateOpiFileName() {
		if (opiFileName == null) {
			opiFileName = edlFileName;
			if (opiFileName.endsWith(edlExtension)) {
				opiFileName = opiFileName.substring(0, opiFileName.length() - edlExtension.length());
			}
			opiFileName += opiExtension;
		}
	}

	private static void validateEdmFiles() {
		String edmFilesName = System.getProperty("edm2xml.edmFiles");
		if (edmFilesName == null) {
			edmFilesName = System.getenv("EDMFILES");
		}
		if (edmFilesName != null) {
			System.setProperty("edm2xml.edmFiles", edmFilesName);
		}
	}
	
	private static void validateColorsFile() {

		String colorsFileName = System.getProperty("edm2xml.colorsFile");

		// Use default name if the name is not specified.
		if (colorsFileName == null) {
			colorsFileName = defaultColorsFileName; 

			// Append directory if specified.
			String edmFilesName = System.getProperty("edm2xml.edmFiles");
			if (edmFilesName != null) {
				colorsFileName = edmFilesName + File.separator + colorsFileName;
			}
		}
		
		File colorsFile = new File(colorsFileName);
		
		if (colorsFile.isDirectory()) {
			colorsFileName = colorsFileName + File.separator + defaultColorsFileName;
			colorsFile = new File(colorsFileName);
		}
		
		if (!colorsFile.exists()) {
			String errorMessage = "File " + colorsFileName + " does not exist.";
			log.error(errorMessage);
			System.err.println("Error: " + errorMessage);
			System.exit(1);
		}
		System.setProperty("edm2xml.colorsFile", colorsFileName);
	}

	private static void validateColorsOutput() {
		
		String colorsOutputName = System.getProperty("edm2xml.colorsOutput");

		/* By default the color output definition is placed at the same place as output
		 * OPI files.
		 */
		if (colorsOutputName == null) {
			colorsOutputName = defaultColorsOutputName;

			String opiDirectoryName = (new File(opiFileName)).getParent();
			if (opiDirectoryName != null) {
				colorsOutputName = opiDirectoryName + File.separator + colorsOutputName;   
			}
		}
		
		File colorsOutputFile = new File(colorsOutputName);
		
		if (colorsOutputFile.isDirectory()) {
			colorsOutputName = colorsOutputName + File.separator + defaultColorsOutputName;
			colorsOutputFile = new File(colorsOutputName);
		}
		
		System.setProperty("edm2xml.colorsOutput", colorsOutputName);
	}
	
	private static void validateRobustParsing() {
		System.setProperty("edm2xml.robustParsing", String.valueOf(robustParsing));
	}
	
	private static void printHelp() {
		System.out.println("Usage: EdmConverter [-r] [-help] EDL_FILE [OPI_FILE]");
		System.out.println();
		System.out.println("    -help       Displays this help.");
		System.out.println("    -r          Disables robust EDL file parsing. Parsing will stop at any");
		System.out.println("                kind of exception.");
		System.out.println("    -c          Outputs color definition file.");
		System.out.println("    EDL_FILE    EDL file to convert.");
		System.out.println("    OPI_FILE    OPI file to write the output to. When ommited, EDL_FILE");
		System.out.println("                base with '.opi' extension is used.");
		System.out.println();
		System.out.println("To load the needed color definitions, system property EDMFILES must");
		System.out.println("be set, java property edm2xml.colorsFile must point to colors.list file, or");
		System.out.println("colors.list file must exist in current directory.");
		System.out.println();
		System.out.println("When using -c option, color definitions are stored in color.def file placed");
		System.out.println("in the same directory as OPI_FILE. To specify alternative path and ");
		System.out.println("filename, set edm2xml.colorsOutput java property.");
	}
}
