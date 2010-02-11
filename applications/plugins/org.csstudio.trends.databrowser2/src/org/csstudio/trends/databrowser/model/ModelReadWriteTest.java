package org.csstudio.trends.databrowser.model;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.swt.graphics.RGB;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of Model's XML config read/write
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ModelReadWriteTest
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
              public void run()
              {
                  try
                  {
                      original_model.write(original_xml);
                  }
                  catch (Exception ex)
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
              public void run()
              {
                  try
                  {
                      readback_model.write(dumper);
                  }
                  catch (Exception ex)
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
        model.setTimespan(60*60 + 10);
        model.enableScrolling(true);
        
        model.addAxis(new AxisConfig("Value", new RGB(0, 0, 255), 0, 10, false, false));
        
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
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader buf = new BufferedReader(reader);
        String line = buf.readLine();
        while (line != null)
        {
            System.out.println(line);
            line = buf.readLine();
        }
        in.close();
    }
}
