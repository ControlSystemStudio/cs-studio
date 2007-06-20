package org.csstudio.trends.databrowser;

import util.eclipse.menu.NewFileWizardMenuAction;


/** Action to run the NewChartEditorWizard.
 *  <p>
 *  Hooked into navigator or workspace explorer context menu
 *  via object contrib to IContainer,
 *  or into the file/new menu.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CreateNewChartConfig extends NewFileWizardMenuAction
{
    public CreateNewChartConfig()
    {
        super(NewChartEditorWizard.class);
    }
}
