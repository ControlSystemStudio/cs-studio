package org.csstudio.display.pvtable;

import util.eclipse.menu.NewFileWizardMenuAction;

/** Action to run the NewPVTableWizard.
 *  <p>
 *  Hooked into navigator or workspace explorer context menu
 *  via object contrib to IContainer.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CreateNewPVTableConfig extends NewFileWizardMenuAction
{
    public CreateNewPVTableConfig()
    {
        super(NewPVTableWizard.class);
    }
}
