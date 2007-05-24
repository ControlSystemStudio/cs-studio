package org.csstudio.trends.databrowser.plotpart;

import java.io.InputStream;

import org.csstudio.swt.chart.Chart;
import org.csstudio.trends.databrowser.model.Model;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;

/** Base for a ViewPart or EditorPart that displays a DataBrowser plot.
 *  @author Kay Kasemir
 */
public class PlotPart
{
    private Model model = new Model();

    private Controller controller;
    private BrowserUI gui;

    public void init(IFile file) throws PartInitException
    {
        if (file == null)
            return;
        // Load model content from file
        try
        {
            InputStream stream = file.getContents();
            model.load(stream);
            stream.close();
        }
        catch (Exception e)
        {
            throw new PartInitException("Load error", e); //$NON-NLS-1$
        }
    }
    
    /** @return Returns the model. */
    public Model getModel()
    {  
        return model;
    }
    
    public Chart getChart()
    {
        return gui.getChart();
    }

    public void createPartControl(Composite parent)
    {
        gui = new BrowserUI(model, parent, 0);
        controller = new Controller(model, gui);
    }

    public void setFocus()
    {   
        gui.setFocus(); 
    }
    
    public void dispose()
    {
        gui.dispose();
        controller.dispose();
    }

}
