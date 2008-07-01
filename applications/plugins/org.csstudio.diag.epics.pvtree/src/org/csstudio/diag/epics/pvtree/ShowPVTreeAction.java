package org.csstudio.diag.epics.pvtree;

import org.csstudio.platform.ui.workbench.OpenViewAction;

/** Action connected to workbench menu action set for showing the view.
 *  @author Kay Kasemir
 */
public class ShowPVTreeAction extends OpenViewAction
{
	public ShowPVTreeAction()
    {
	    super(PVTreeView.ID);
    }
}