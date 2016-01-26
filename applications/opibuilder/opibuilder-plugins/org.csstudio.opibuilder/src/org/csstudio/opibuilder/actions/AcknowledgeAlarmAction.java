/*******************************************************************************
* Copyright (c) 2010-2016 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.PVWidgetEditpartDelegate;
import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.util.BeastAlarmSeverityLevel;
import org.csstudio.ui.resources.alarms.AlarmIcons;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class AcknowledgeAlarmAction implements IObjectActionDelegate {

    private IStructuredSelection selection;
    private IWorkbenchPart targetPart;

	@Override
	public void run(IAction action) {
        final AbstractBaseEditPart selectedWidget = getSelectedWidget();

		if(selectedWidget == null || selectedWidget.getWidgetModel() == null) return;
        if (!(selectedWidget.getWidgetModel() instanceof IPVWidgetModel) || !(selectedWidget instanceof AbstractPVWidgetEditPart)) return;

//        IPVWidgetModel model = (IPVWidgetModel) selectedWidget.getWidgetModel();

        PVWidgetEditpartDelegate pvDelegate = ((AbstractPVWidgetEditPart) selectedWidget).getPVWidgetEditpartDelegate();
        if (pvDelegate.isBeastAlarmAndConnected() == false) return;

        pvDelegate.acknowledgeAlarm();
//        MessageDialog.openInformation(null, "Acknowledge PV", "When implemented, this will acknowledge AlarmPV ");
	}

	private void updateAction(IAction action) {
        final AbstractBaseEditPart selectedWidget = getSelectedWidget();

		if(selectedWidget == null || selectedWidget.getWidgetModel() == null) return;
        if (!(selectedWidget.getWidgetModel() instanceof IPVWidgetModel) || !(selectedWidget instanceof AbstractPVWidgetEditPart)) return;

        IPVWidgetModel model = (IPVWidgetModel) selectedWidget.getWidgetModel();
        PVWidgetEditpartDelegate pvDelegate = ((AbstractPVWidgetEditPart) selectedWidget).getPVWidgetEditpartDelegate();

        if (!pvDelegate.isBeastAlarmAndConnected()) {
        	// cannot ack/unack
        	action.setEnabled(false);
        	action.setImageDescriptor(null);
        	action.setText("Cannot ACK - not connected");
        	action.setToolTipText("");
        	return;
        }

        if (pvDelegate.getBeastAlarmInfo().isLatchedAlarmOK()) {
        	action.setEnabled(false);
        }

        action.setEnabled(true);
    	action.setImageDescriptor(getImageDescriptor(pvDelegate.getBeastAlarmInfo().currentSeverity, pvDelegate.getBeastAlarmInfo().latchedSeverity));
    	action.setToolTipText("Test tooltip:\nLatched Severity: " + pvDelegate.getBeastAlarmInfo().latchedSeverity.getDisplayName()
    			+ "\nCurrent Severity: " + pvDelegate.getBeastAlarmInfo().currentSeverity.getDisplayName());

    	String actionDesc = String.format("%1$sAcknowledge %2$s: %3$s",
    	        pvDelegate.getBeastAlarmInfo().isAcknowledged() ? "Un-" : "",
    	        pvDelegate.isBeastAlarmNode() ? "NODE" : "PV",
                pvDelegate.getBeastAlarmInfo().getBeastChannelNameNoScheme());
    	action.setText(actionDesc);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection) selection;
            updateAction(action);
        }
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
        updateAction(action);
	}

    private AbstractBaseEditPart getSelectedWidget(){
        if(selection.getFirstElement() instanceof AbstractBaseEditPart){
            return (AbstractBaseEditPart)selection.getFirstElement();
        }else
            return null;
    }

    private static ImageDescriptor getImageDescriptor(BeastAlarmSeverityLevel currentSeverity, BeastAlarmSeverityLevel latchedSeverity) {
        AlarmIcons icons = AlarmIcons.getInstance();
        switch (latchedSeverity) {
            case UNDEFINED_ACK:
            case INVALID_ACK:
                return icons.getInvalidAcknowledged(false);
            case UNDEFINED:
            case INVALID:
                return currentSeverity == BeastAlarmSeverityLevel.OK ?
                        icons.getInvalidClearedNotAcknowledged(false) : icons.getInvalidNotAcknowledged(false);
            case MAJOR:
                return currentSeverity == BeastAlarmSeverityLevel.OK ?
                        icons.getMajorClearedNotAcknowledged(false) : icons.getMajorNotAcknowledged(false);
            case MAJOR_ACK:
                return icons.getMajorAcknowledged(false);
            case MINOR:
                return currentSeverity == BeastAlarmSeverityLevel.OK ?
                        icons.getMinorClearedNotAcknowledged(false) : icons.getMinorNotAcknowledged(false);
            case MINOR_ACK:
                return icons.getMinorAcknowledged(false);
            case OK:
            default:
                return null;
        }
    }

}
