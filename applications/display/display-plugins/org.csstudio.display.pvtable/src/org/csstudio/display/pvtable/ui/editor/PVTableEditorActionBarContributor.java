/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui.editor;

import org.csstudio.display.pvtable.ui.DeleteAllMesureAction;
import org.csstudio.display.pvtable.ui.DeleteLastMesureAction;
import org.csstudio.display.pvtable.ui.ExportXLSAction;
import org.csstudio.display.pvtable.ui.MesureAction;
import org.csstudio.display.pvtable.ui.PVTableAction;
import org.csstudio.display.pvtable.ui.RestoreAction;
import org.csstudio.display.pvtable.ui.SnapshotAction;
import org.csstudio.display.pvtable.ui.ToleranceAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * EditorActionBarContributor for the PV Table
 * 
 * @author Kay Kasemir, A. PHILIPPE L. PHILIPPE GANIL/FRANCE
 */
public class PVTableEditorActionBarContributor extends EditorActionBarContributor {
	private IToolBarManager manager;
	final private PVTableAction snap = new SnapshotAction(null);
	final private PVTableAction restore = new RestoreAction(null);
	final private PVTableAction tolerance = new ToleranceAction(null);
	final private PVTableAction mesure = new MesureAction(null);
	final private PVTableAction deleteAllMesure = new DeleteAllMesureAction(null);
	final private PVTableAction deleteLastMesure = new DeleteLastMesureAction(null);
	final private PVTableAction exportXLS = new ExportXLSAction(null);
	final private String mesureActionID = "org.csstudio.display.pvtable.mesureAction";
	final private String deleteAllMesureActionID = "org.csstudio.display.pvtable.deleteAllMesureAction";
	final private String deleteLastMesureActionID = "org.csstudio.display.pvtable.deleteLastAction";
	final private String exportXLSActionID = "org.csstudio.display.pvtable.exportXLSAction";
	{
		mesure.setId(mesureActionID);
		deleteAllMesure.setId(deleteAllMesureActionID);
		deleteLastMesure.setId(deleteLastMesureActionID);
		exportXLS.setId(exportXLSActionID);
	}

	/**
	 * Invoked once when the first PVTableEditor gets opened. Items stay in
	 * toolbar until the last PVTableEditor exits.
	 */
	@Override
	public void contributeToToolBar(final IToolBarManager mgr) {
		mgr.add(snap);
		mgr.add(restore);
		mgr.add(tolerance);
		mgr.add(exportXLS);
		mgr.add(new Separator());
		this.manager = mgr;
	}

	/** @see org.eclipse.ui.part.EditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart) */
	@Override
	public void setActiveEditor(final IEditorPart target) {
		final PVTableEditor editor = (PVTableEditor) target;
		snap.setViewer(editor.getTableViewer());
		restore.setViewer(editor.getTableViewer());
		tolerance.setViewer(editor.getTableViewer());
		mesure.setViewer(editor.getTableViewer());
		deleteLastMesure.setViewer(editor.getTableViewer());
		deleteAllMesure.setViewer(editor.getTableViewer());
		exportXLS.setViewer(editor.getTableViewer());
		
		this.refreshMeasureItemVisibility(editor);
	}

	public void refreshMeasureItemVisibility(PVTableEditor editor) {
		if (editor.getModel().getConfig() != null) {
			if(manager.find(mesureActionID) == null){
				manager.add(mesure);
				manager.add(deleteLastMesure);
				manager.add(deleteAllMesure);
			}
		} else {
			manager.remove(mesureActionID);
			manager.remove(deleteAllMesureActionID);
			manager.remove(deleteLastMesureActionID);
		}
		// Force the update.
		manager.update(true);
	}

	/** @see org.eclipse.ui.part.EditorActionBarContributor#dispose() */
	@Override
	public void dispose() {
		snap.setViewer(null);
		restore.setViewer(null);
		tolerance.setViewer(null);
		mesure.setViewer(null);
		deleteLastMesure.setViewer(null);
		deleteAllMesure.setViewer(null);
		exportXLS.setViewer(null);
		super.dispose();
	}
}