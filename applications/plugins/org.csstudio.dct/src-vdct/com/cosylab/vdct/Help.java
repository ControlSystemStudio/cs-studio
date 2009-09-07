/*
 * Copyright (c) 2004 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */
package com.cosylab.vdct;

/**
 * <code>Help</code> ...  DOCUMENT ME!
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 * 
 * @since VERSION
 */
public class Help {
    
    public static final String RUN_COMMAND_CLASS = "java -cp VisualDCT.jar com.cosylab.vdct.VisualDCT";
    public static final String RUN_COMMAND_JAR = "java -jar VisualDCT.jar";
    public static final String DBD_LOAD = "<DBD>* or <DB>*";
    public static final String DIR = "-DVDCT_DIR=";
    public static final String EPICS_DB_INCLUDE_PATH = "-DEPICS_DB_INCLUDE_PATH=";
    public static final String CONFIG_DIR = "-DVDCT_CONFIG_DIR=";
    public static final String HELP = "-h";
    public static final String HELP2 = "--help";
    public static final String PATH = "<path>";
    public static final String SPACE = "     ";
    public static final String INDENT = "  ";
      
    public static final String GENERATE_FLAT_DATABASE = "java -cp VisualDCT.jar com.cosylab.vdct.GenerateFlatDatabase";
    public static final String OPTIONS = "[OPTIONS]";
    public static final String INPUT_VDB = "input.vdb";
    public static final String OUTPUT_DB = "output.db";
    public static final String FLATDB = "flatdb";
    public static final String DBD_FILE = "--dbd-file";
    public static final String ENABLE_GLOBAL_MACRO = "--enable-global-macros";
    public static final String DISABLE_GLOBAL_MACRO = "--disable-global-macros";
    public static final String ENABLE_CAPFAST = "--enable-capfast";
    public static final String DISABLE_CAPFAST ="--disable-capfast";
    
    public static void printHelpAndExit() {
        System.out.println("VisualDCT help");
        System.out.println();
        System.out.println("General help");
        System.out.println("Usage: " + RUN_COMMAND_CLASS + " " + OPTIONS);
        System.out.println("or     " + RUN_COMMAND_JAR + " " + OPTIONS);
        System.out.println();
        System.out.println("Options:");
        System.out.println(INDENT + HELP + ", " + HELP2 + "                    " + SPACE +
                "print this message");
        System.out.println(INDENT + DBD_LOAD + "               " + SPACE +
                "paths to database definition files and record instance database files (both optional)");
        System.out.println(INDENT + DIR + PATH + "             " + SPACE +
                "VDCT_DIR environment variable is used to define the default working directory");
        System.out.println(INDENT + CONFIG_DIR + PATH + "      " + SPACE + 
                "VDCT_CONFIG_DIR is an environment variable used to define the default plug-ins configuration directory");
        System.out.println(INDENT + EPICS_DB_INCLUDE_PATH + PATH + SPACE + 
                "definition of EPICS_DB_INCLUDE_PATH environment variable");
        System.out.println();
        System.out.println(INDENT + "Example: " + RUN_COMMAND_CLASS + " " + CONFIG_DIR + "/users/devl/config/vdct test.dbd test.db" );
        
        System.out.println();
        System.out.println("Generating flat database help");
        System.out.println("Usage: " + GENERATE_FLAT_DATABASE + " " + OPTIONS + " ..." + " " + INPUT_VDB + " " + OUTPUT_DB);
        System.out.println();
        System.out.println("Options:");
        System.out.println(INDENT + DBD_FILE + "                    " + SPACE + 
                "if this command is followed by the name of a .dbd file, the .dbd file is loaded before database is generated");
        System.out.println(INDENT + ENABLE_GLOBAL_MACRO + "        " + SPACE + 
                "enables global macro evaluation");
        System.out.println(INDENT + DISABLE_GLOBAL_MACRO + "       " + SPACE +
                "disable global macro evaluation");
        System.out.println(INDENT + ENABLE_CAPFAST + "              " + SPACE +
                "enable production of hierarhical names like CapFast");
        System.out.println(INDENT + DISABLE_CAPFAST + "             " + SPACE +
                "disable production of hierarhical names like CapFast");
        System.out.println();
        System.out.println("We also provided a script for running flattening database tool, named flatdb, which can be found next to the distribution of VisualDCT.");
        System.out.println(INDENT + "Example: " + FLATDB + " " + DBD_FILE + " app.dbd " + INPUT_VDB + " " + OUTPUT_DB);
        
        
        
        System.exit(0);
    }

}
