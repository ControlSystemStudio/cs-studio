package org.csstudio.trends.databrowser;

import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.ploteditor.PlotEditor;
import org.csstudio.util.wizard.NewFileWizard;

/** Wizard for creating a new DataBrowser config file & opening editor on it.
 *  @author Kay Kasemir
 */
public class NewChartEditorWizard extends NewFileWizard
{
    public NewChartEditorWizard()
    {
        super(Plugin.getDefault(),
              PlotEditor.ID,
            Messages.DataBrowser,
            "data.xml", //$NON-NLS-1$
            new Model().getXMLContent());
    }
}
