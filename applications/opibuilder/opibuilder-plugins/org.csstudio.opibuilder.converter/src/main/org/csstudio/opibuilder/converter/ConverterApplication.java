/*******************************************************************************
 * Copyright (c) 2018 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.csstudio.opibuilder.converter.writer.OpiWriter;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** RCP command line application for ADL file converter
 *
 *  <p>To use, start product like this:
 *
 *  <code>
 *  css -nosplash
 *      -application org.csstudio.opibuilder.converter.edl
 *      /path/to/file1.edl
 *      /path/to/file2.edl
 *  </code>
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ConverterApplication implements IApplication
{
    private List<File> inputFiles = new ArrayList<>();
    private Optional<File> outputDirectory = Optional.empty();
    private boolean force = false;

    private static String renameEdlToOpi(String edlFileName)
    {
          return edlFileName.endsWith(".edl")
              ? edlFileName.substring(0, edlFileName.length()-4) + ".opi"
              : edlFileName + ".opi";
    }

    private void usage() {
        System.out.println("EDM Converter: convert all .edl files to .opi files at the same location.");
        System.out.println("Usage: <converter-cmd> [-h] [-f] [-o <output-dir>] edl-file ...");
        System.out.println("       -h: print this help and exit");
        System.out.println("       -o <output-dir>: place all converted opi files in output-dir");
        System.out.println("       -f: overwrite existing files");
    }

    private void parseArguments(String[] args) {
        for (int i=0; i<args.length; ++i)
        {
            if (args[i].equals("-h"))
            {
                usage();
                System.exit(0);
            }
            else if (args[i].equals("-f"))
            {
                force = true;
            }
            else if (args[i].equals("-o"))
            {
                outputDirectory = Optional.of(new File(args[i+1]));
                ++i;
            }
            else if (args[i].startsWith("-"))
            {
                if (i+1 < args.length)
                {
                    System.out.println("Ignoring argument " + args[i] + " " + args[i+1]);
                    ++i;
                }
                else
                    System.out.println("Ignoring argument " + args[i]);
            }
            else
                inputFiles.add(new File(args[i]));
        }
    }

    @Override
    public Object start(final IApplicationContext context) throws Exception
    {
        System.out.println("************************");
        System.out.println("** EDL File Converter **");
        System.out.println("************************");

        // Prevent long error message when returning non-zero error code.
        System.setProperty(IApplicationContext.EXIT_DATA_PROPERTY, "");

        final String args[] =
                (String []) context.getArguments().get("application.args");
        parseArguments(args);
        if (outputDirectory.isPresent() && ! outputDirectory.get().exists())
        {
            System.out.println("ERROR: Output directory " + outputDirectory.get() + " does not exist.");
            return -1;
        }
        if (inputFiles.isEmpty())
        {
            System.out.println("ERROR: No input files specified.");
            return -1;
        }
        try
        {
            if (! checkThenConvert())
                return -1;
        }
        catch (Exception ex)
        {
            System.out.println("Unexpected error while converting.");
            ex.printStackTrace();
            return -1;
        }

        return IApplication.EXIT_OK;
    }

    private boolean checkThenConvert() throws Exception
    {
        final List<File> inputs = new ArrayList<>();
        final List<File> outputs = new ArrayList<>();
        for (File input : inputFiles)
        {
            File output = null;
            if (outputDirectory.isPresent())
            {
                output = new File(outputDirectory.get(), renameEdlToOpi(input.getName()));
            }
            else
                output = new File(renameEdlToOpi(input.getAbsolutePath()));

            if (! input.canRead())
            {
                System.out.println("ERROR: Cannot read input file " + input);
                return false;
            }
            if (output.exists() && ! force)
            {
                System.out.println("ERROR: Output file already exists: " + output);
                return false;
            }
            // This can happen if the -o option is selected so all output files
            // would be put in the same directory.
            if (outputs.contains(output)) {
                System.out.println("ERROR: Multiple output files with the path " + output + " would be created.");
                return false;
            }
            inputs.add(input);
            outputs.add(output);
        }

        for (int i=0; i<inputs.size(); ++i)
            convert(inputs.get(i), outputs.get(i));
        return true;
    }

    private void convert(final File input, final File output) throws Exception
    {
        System.out.println("\n** Converting " + input + " into " + output);
        OpiWriter writer = OpiWriter.getInstance();
        writer.writeDisplayFile(input.getAbsolutePath(),
                                output.getAbsolutePath());
    }

    @Override
    public void stop()
    {
    }
}
