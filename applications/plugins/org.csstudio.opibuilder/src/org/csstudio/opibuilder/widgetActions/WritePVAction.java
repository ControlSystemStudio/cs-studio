/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.util.Calendar;

import org.csstudio.opibuilder.editparts.IPVWidgetEditpart;
import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**An actions writing value to a PV.
 * @author Xihui Chen
 *
 */
public class WritePVAction extends AbstractWidgetAction {

	public static final String PROP_PVNAME = "pv_name";//$NON-NLS-1$
	public static final String PROP_VALUE = "value";//$NON-NLS-1$
	public static final String PROP_TIMEOUT = "timeout";//$NON-NLS-1$
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(PROP_PVNAME, "PV Name", 
				WidgetPropertyCategory.Basic, "$(pv_name)")); //$NON-NLS-1$
		addProperty(new StringProperty(PROP_VALUE, "Value", 
				WidgetPropertyCategory.Basic, "")); //$NON-NLS-1$
		addProperty(new IntegerProperty(PROP_TIMEOUT, "Timeout (second)", 
				WidgetPropertyCategory.Basic, 10, 1, 3600));
	}

	@Override
	public ActionType getActionType() {
		return ActionType.WRITE_PV;
	}

	public String getPVName(){
		return (String)getPropertyValue(PROP_PVNAME);
	}
	
	public String getValue(){
		return (String)getPropertyValue(PROP_VALUE);
	}
	
	public int getTimeout(){
		return (Integer)getPropertyValue(PROP_TIMEOUT);
	}
	
	@Override
	public void run() {
		
		//If it has the same nave as widget PV name, use it.
		if(getWidgetModel() instanceof IPVWidgetModel){
			String mainPVName=((IPVWidgetModel)getWidgetModel()).getPVName();
			if(getPVName().equals(mainPVName)){
				Object o = getWidgetModel().getRootDisplayModel().getViewer().getEditPartRegistry().get(getWidgetModel());
				if(o instanceof IPVWidgetEditpart){
					((IPVWidgetEditpart)o).setPVValue(IPVWidgetModel.PROP_PVNAME, getValue().trim());
					return;
				}
			}
		}
		
		
		Job job = new Job(getDescription()){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				String text = getValue().trim();
				PV pv = null;	
				try {
					pv = PVFactory.createPV(getPVName());
					pv.start();
					long startTime = System.currentTimeMillis();
					int timeout = getTimeout()*1000;
					while((Calendar.getInstance().getTimeInMillis() - startTime) < timeout && 
							!pv.isConnected() && !monitor.isCanceled()){
						Thread.sleep(100);
					}
					if(monitor.isCanceled()){
						ConsoleService.getInstance().writeInfo("\"" + getDescription() + "\" " //$NON-NLS-1$ //$NON-NLS-2$
								+"has been canceled");
						return Status.CANCEL_STATUS;
					}
						
					if(!pv.isConnected()){
						throw new Exception(
								"Connection Timeout! Failed to connect to the PV.");
					}
					if(!pv.isWriteAllowed())
					 throw new Exception("The PV is not allowed to write");
					setPVValue(pv, text);
					//If no sleep here, other listeners will have a delay to get update.
					//Don't know the reason, but this is a work around.
					Thread.sleep(200);
				} catch (Exception e1) {
					popErrorDialog(new Exception(e1));
					return Status.OK_STATUS;
				}finally{
					if(pv !=null)
						pv.stop();
				}
				return Status.OK_STATUS;
			}
			
		};
		
		job.schedule();	
	}
	
	/**Set PV to given value. Should accept Double, Double[], Integer, String, maybe more.
	 * @param pvPropId
	 * @param value
	 */
	protected void setPVValue(final PV pv, final Object value){
		if(pv != null){
			try {
				pv.setValue(value);
			} catch (final Exception e) {
				popErrorDialog(e);				
			}
		}
	}

	/**
	 * @param pv
	 * @param e
	 */
	private void popErrorDialog(final Exception e) {
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				String message = 
					"Failed to write PV:" + getPVName() + "\n" + e.getMessage();
				ConsoleService.getInstance().writeError(message);
			}
		});
	}

	
	@Override
	public String getDefaultDescription() {
		return "Write " + getValue() + " to " + getPVName();
	}

	
}
