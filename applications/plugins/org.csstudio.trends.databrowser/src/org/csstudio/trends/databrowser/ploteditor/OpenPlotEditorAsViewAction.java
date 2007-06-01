package org.csstudio.trends.databrowser.ploteditor;

import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.plotview.PlotView;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;

/** Open the active DataBrowser editor's file as a view.
 *  @author Kay Kasemir
 */
public class OpenPlotEditorAsViewAction extends Action
{
    private final PlotEditor editor;
    
    /** Constructor
     *  @param editor The editor who's file to open as a view
     */
    public OpenPlotEditorAsViewAction(PlotEditor editor)
    {
        super(Messages.OpenAsView,
              Plugin.getImageDescriptor("icons/chart.gif")); //$NON-NLS-1$
        this.editor = editor;
    }

    /** Open the editor's file in a view. */
    @Override
    public void run()
    {
        final IFile file = editor.getEditorInputFile();
        PlotView.activateWithFile(file);
    }
}
