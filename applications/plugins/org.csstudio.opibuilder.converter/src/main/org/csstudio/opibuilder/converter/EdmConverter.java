package org.csstudio.opibuilder.converter;

import java.io.File;

import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.writer.OpiWriter;

/**
 * Main class for running edm2xml converter from command line.
 * @author Matevz
 *
 */
public class EdmConverter {

	public static void main(String[] args) throws EdmException {
		
		boolean robust = true;
		String edlFileName = "";
		String opiFileName = "";
		
		if (args.length > 3 || args.length < 1) {
			System.out.println("Error: Invalid number of command line arguments. Must be between 1 and 3.");
			System.out.println();
			printHelp();
			return;
		}
		
		boolean edl = false;	// second argument is edl file
		if (args[0].equals("-help")) {
			printHelp();
			return;
		}
		else if (args[0].equals("-r")) {
			robust = false;
			edl = true;
		}
		else {
			edlFileName = args[0];
		}
		
		if (args.length > 1) {
			if (edl) {
				edlFileName = args[1];
				
				if (args.length == 3)
					opiFileName = args[2];
			}
			else {
				opiFileName = args[1];
			}
		}
		
		checkFileExistency(edlFileName);
		
		
		System.setProperty("edm2xml.robustParsing", String.valueOf(robust));
		
		if (System.getProperty("edm2xml.edmFiles") == null) {
			String edmfiles = System.getenv("EDMFILES");
			if (edmfiles == null)
				System.setProperty("edm2xml.edmFiles", ".");
			else 
				System.setProperty("edm2xml.edmFiles", edmfiles);
		}
		
		String edm2xml_files = System.getProperty("edm2xml.edmFiles");
		
		if (System.getProperty("edm2xml.colorsFile") == null)
			System.setProperty("edm2xml.colorsFile", edm2xml_files + "/colors.list");
		
		OpiWriter opiWriter = OpiWriter.getInstance();
		
		if (opiFileName == "")
			opiWriter.writeDisplayFile(edlFileName);
		else
			opiWriter.writeDisplayFile(edlFileName, opiFileName);
		
		// TODO: add options to output the color.def or not, and to define where.
		String colorDefPath = ".";
		String edlDirectory = (new File(edlFileName)).getParent();
		if (edlDirectory != null) {
			colorDefPath = edlDirectory;   
		}

		String colorDefName = colorDefPath + File.separator  + "color.def";
		opiWriter.writeColorDef(colorDefName);
	}

	private static void checkFileExistency(String fileName) {
		File e = new File(fileName);
		if (!e.exists()) {
			System.out.println("Error: File " + fileName + " does not exist.");
			System.exit(1);
		}
	}

	private static void printHelp() {
		System.out.println("Usage: EdmConverter [-r] [-help] EDL_FILE [OPI_FILE]");
		System.out.println();
		System.out.println("    -r          Disables robust EDL file parsing. Parsing will stop at any");
		System.out.println("                kind of exception.");
		System.out.println("    -help       Displays this help.");
		System.out.println("    EDL_FILE    EDL file to convert.");
		System.out.println("    OPI File    OPI file to write the output to. When ommited, EDL_FILE");
		System.out.println("                base with '.opi' extension is used.");
		System.out.println();
		System.out.println("Note: to load the needed color definitions, system property EDMFILES must");
		System.out.println("be set, java property edm2xml.colorsFile must point to colors.list file, or");
		System.out.println("colors.list file must exist in current directory.");
	}
}
