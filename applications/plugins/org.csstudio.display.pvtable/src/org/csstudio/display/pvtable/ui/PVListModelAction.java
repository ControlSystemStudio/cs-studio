/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;


import org.csstudio.display.pvtable.model.PVListModel;
import org.eclipse.jface.action.Action;

/** Action that acts on PVListModel.
 *  <p>
 *  Used by the view, where the PVListModel is known
 *  from the start and stays that way.
 *  Also used by the editor contributor, which sets the pv list to
 *  the one from the current editor.
 *  
 *  @author Kay Kasemir
 */
public class PVListModelAction extends Action
{
    private PVListModel pv_list;

    /** Views initialize with their mode, editor contributor uses null. */
	public PVListModelAction(PVListModel pv_list)
	{
		this.pv_list = pv_list;
	}

    /** Used by editor contributor to pass the model of the current editor. */
    public void setPVListModel(PVListModel pv_list)
    {
        this.pv_list = pv_list;
    }
    
    /** @return Returns the currently assigned PVListModel. Might be null. */
    public PVListModel getPVListModel()
    {
        return pv_list;
    }
}
