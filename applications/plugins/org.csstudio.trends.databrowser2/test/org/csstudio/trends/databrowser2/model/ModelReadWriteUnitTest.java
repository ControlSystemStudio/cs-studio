/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.swt.graphics.RGB;
import org.junit.Test;

/** JUnit test of Model's XML config read/write
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelReadWriteUnitTest
{
    @Test
    public void testModelReadWrite() throws Exception
    {
        // Pipe: Dummy Model XML output -> Model XML Parser
        final Model original_model = createModel();
        final PipedOutputStream original_xml = new PipedOutputStream();
        final InputStream xml_reader = new PipedInputStream(original_xml);
        new Thread(new Runnable()
        {
              @Override
            public void run()
              {
                  try
                  {
                      original_model.write(original_xml);
                  }
                  catch (final Exception ex)
                  {
                      ex.printStackTrace();
                  }
                  System.out.println("** Done writing model");
              }
        }).start();

        final Model readback_model = new Model();
        readback_model.read(xml_reader);
        System.out.println("** Done reading model back");

        // Trivial equality check
        assertEquals(original_model.getAxisCount(), readback_model.getAxisCount());
        assertEquals(original_model.getItemCount(), readback_model.getItemCount());

        // Pipe: Model that was read from XML -> Console
        final PipedOutputStream dumper = new PipedOutputStream();
        final InputStream console = new PipedInputStream(dumper);
        new Thread(new Runnable()
        {
              @Override
            public void run()
              {
                  try
                  {
                      readback_model.write(dumper);
                  }
                  catch (final Exception ex)
                  {
                      ex.printStackTrace();
                  }
                  System.out.println("** Done dumping to console");
              }
        }).start();

        dumpStreamToConsole(console);
        System.out.println("** Done copying to console");
    }

    /** @return Dummy model
     *  @throws Exception on error
     */
    private Model createModel() throws Exception
    {
        final Model model = new Model();
        model.enableScrolling(true);
        model.setTimerange("-130 sec", "now");

        model.addAxis(new AxisConfig(true, "Value", new RGB(0, 0, 255), 0, 10, false, false));

        final PVItem pv = new PVItem("fred", 1.0);
        pv.addArchiveDataSource(new ArchiveDataSource("abcd://some/url", 1, "Archive"));
        model.addItem(pv);
        model.addItem(new FormulaItem("demo", "x*2", new FormulaInput[]
        {
            new FormulaInput(pv, "x")
        }));
        return model;
    }

    /** @param in Stream that's dumped to console
     *  @throws Exception on error
     */
    private void dumpStreamToConsole(final InputStream in) throws Exception
    {
        final InputStreamReader reader = new InputStreamReader(in);
        final BufferedReader buf = new BufferedReader(reader);
        String line = buf.readLine();
        while (line != null)
        {
            System.out.println(line);
            line = buf.readLine();
        }
        in.close();
    }
}
