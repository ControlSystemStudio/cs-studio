package org.csstudio.trends.databrowser.plotview;

import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.ploteditor.PlotEditor;
import org.csstudio.trends.databrowser.plotpart.PlotPart;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.part.FileEditorInput;

/** Opens editor for the current DataBrowser view.
 *  @author Kay Kasemir
 */
public class OpenAsPlotEditorAction extends Action
{
    /** The plot part of the view. */
    private final PlotPart plot_part;
    
    public OpenAsPlotEditorAction(PlotPart plot_part)
    {
        super(Messages.OpenInEditor,
              Plugin.getImageDescriptor("icons/chart.gif")); //$NON-NLS-1$
        this.plot_part = plot_part;
    }

    @Override
    public boolean isEnabled()
    {
        return plot_part.getFile() != null;
    }

    /** Open currently selected IFile as View. */
    @Override
    public void run()
    {
        PlotEditor.createInstance(new FileEditorInput(plot_part.getFile()));
    }
}
