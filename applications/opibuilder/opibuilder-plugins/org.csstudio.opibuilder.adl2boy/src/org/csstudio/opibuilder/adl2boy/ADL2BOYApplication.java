/*******************************************************************************
 * Copyright (c) 2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.adl2boy;

import java.util.Arrays;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/** RCP command line application for ADL file converter
 *
 *  <p>To use, start product like this:
 *
 *  <code>
 *  css -application org.csstudio.opibuilder.adl2boy.application
 *      /path/to/file1.adl
 *      /path/to/file2.adl
 *
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
        System.out.println("ADL File Converter");

        final String args[] =
                (String []) context.getArguments().get("application.args");

        System.out.println(Arrays.toString(args));

        return IApplication.EXIT_OK;
    }

    @Override
    public void stop()
    {
    }
}
