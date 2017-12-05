/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.adl2boy;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.adl2boy.translator.TranslatorUtils;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** RCP command line application for ADL file converter
 *
 *  <p>To use, start product like this:
 *
 *  <code>
 *  css -nosplash
 *      -application org.csstudio.opibuilder.adl2boy.application
 *      /path/to/file1.adl
 *      /path/to/file2.adl
 *  </code>
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ADL2BOYApplication implements IApplication
{
    @Override
    public Object start(final IApplicationContext context)
    {
        System.out.println("************************");
        System.out.println("** ADL File Converter **");
        System.out.println("************************");

        final String args[] =
                (String []) context.getArguments().get("application.args");

        final List<String> files = new ArrayList<>();
        for (int i=0; i<args.length; ++i)
        {
            if (args[i].startsWith("-"))
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
                files.add(args[i]);
        }

        try
        {
            if (! checkThenConvert(files))
                System.exit(-1);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        return IApplication.EXIT_OK;
    }

    private boolean checkThenConvert(final List<String> filenames) throws Exception
    {
        final List<File> inputs = new ArrayList<>();
        final List<File> outputs = new ArrayList<>();
        for (String name : filenames)
        {
            final File input = new File(name);
            final File output = name.endsWith(".adl")
                              ? new File(name.substring(0, name.length()-4) + ".opi")
                              : new File(name + ".opi");
            if (! input.canRead())
            {
                System.out.println("ERROR: Cannot read " + input);
                return false;
            }
            if (output.exists())
            {
                System.out.println("ERROR: Output file already exists: " + output);
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
        System.out.println("\n** Reading " + input);
        final DisplayModel model = TranslatorUtils.convertAdlToModel(input.toString());
        final String xml = XMLUtil.widgetToXMLString(model, true);

        System.out.println("\n** Writing " + output);
        try
        (
            final FileOutputStream out = new FileOutputStream(output);
        )
        {
            out.write(xml.getBytes());
        }
    }

    @Override
    public void stop()
    {
    }
}
