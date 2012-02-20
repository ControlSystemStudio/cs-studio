/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.utility.pv.PV;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**Show details information of widget's primary PV.
 * @author Xihui Chen
 *
 */
public class ShowPVInfoAction implements IObjectActionDelegate {
	
	private final class PVsInfoDialog extends MessageDialog {
		
		private Map<String, PV> pvMap;

		public PVsInfoDialog(Shell parentShell, String dialogTitle, Map<String, PV> pvMap) {
			super(parentShell, dialogTitle, null, "PVs' details on this widget:",
					MessageDialog.INFORMATION, new String[] { JFaceResources.getString("ok")}, 0); //$NON-NLS-1$
			this.pvMap = pvMap;
		}
		
		@Override
		protected Control createCustomArea(Composite parent) {
			if(pvMap == null || pvMap.size() == 0)		
				return super.createCustomArea(parent);
			parent.setLayout(new FillLayout());			
			TabFolder tabFolder = new TabFolder(parent, SWT.None);			
			for(Entry<String, PV> entry : pvMap.entrySet()){
				TabItem tabItem = new TabItem(tabFolder, SWT.None);
				tabItem.setText(entry.getKey());
				Text text = new Text(tabFolder, SWT.MULTI|SWT.READ_ONLY);
				text.setText(getPVInfo(entry.getValue()));
				tabItem.setControl(text);				
				
			}
			return tabFolder;
			
		}
		
	}

	private IStructuredSelection selection;
	private IWorkbenchPart targetPart;
	
	public ShowPVInfoAction() {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}
	
	

	public void run(IAction action) {
		if(getSelectedWidget() == null || 
				getSelectedWidget().getAllPVs() == null ||
				getSelectedWidget().getAllPVs().size() == 0){
			MessageDialog.openInformation(null, "No PV", "No related PV on this widget.");
			return;
		}
		
		PVsInfoDialog dialog = new PVsInfoDialog(
				targetPart.getSite().getShell(), "PV Info", getSelectedWidget().getAllPVs());
		dialog.open();
		
	}

	private String getPVInfo(PV pv) {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + pv.getName() + "\n"); //$NON-NLS-2$
		sb.append("State: " + pv.getStateInfo()+ "\n"); //$NON-NLS-2$
		sb.append("Connected: " + pv.isConnected()+ "\n"); //$NON-NLS-2$
		sb.append("Running: " + pv.isRunning()+ "\n"); //$NON-NLS-2$
		if(pv.getValue() != null){
			sb.append((pv.isConnected()? "Value: " : "Last received value: ") + pv.getValue()+ "\n"); //$NON-NLS-2$
			sb.append("Meta Data: " + pv.getValue().getMetaData());
		}
		return sb.toString();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}
	
	private AbstractBaseEditPart getSelectedWidget(){ 
		if(selection.getFirstElement() instanceof AbstractBaseEditPart){
			return (AbstractBaseEditPart)selection.getFirstElement();
		}else
			return null;
	}

}
