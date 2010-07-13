/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.configtool;

import java.io.PrintWriter;

import org.junit.Test;

/** JUnit plug-in test of ALHConverter
 *  @author Kay Kasemir
 */
public class ALHConverterTest
{
    @Test
    public void testConverter() throws Exception
    {
        final ALHConverter converter = new ALHConverter("alh/Demo2.alhConfig"); //$NON-NLS-1$
        final PrintWriter out = new PrintWriter(System.out);
        converter.getAlarmTree().writeXML(out);
        out.flush();
    }
}
