/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import static org.epics.pvmanager.ExpressionLanguage.channel;

import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.csstudio.opibuilder.editparts.IPVWidgetEditpart;
import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.pvmanager.BOYPVFactory;
import org.csstudio.opibuilder.scriptUtil.GUIUtil;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.util.time.TimeDuration;

/**
 * An actions writing value to a PV.
 * 
 * @author Xihui Chen
 * 
 */
public class WritePVAction extends AbstractWidgetAction {

	public static final String PROP_PVNAME = "pv_name";//$NON-NLS-1$
	public static final String PROP_VALUE = "value";//$NON-NLS-1$
	public static final String PROP_TIMEOUT = "timeout";//$NON-NLS-1$
	public static final String PROP_CONFIRM_MESSAGE = "confirm_message"; //$NON-NLS-1$		

	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(PROP_PVNAME, "PV Name",
				WidgetPropertyCategory.Basic, "$(pv_name)")); //$NON-NLS-1$
		addProperty(new StringProperty(PROP_VALUE, "Value",
				WidgetPropertyCategory.Basic, "")); //$NON-NLS-1$
		addProperty(new IntegerProperty(PROP_TIMEOUT, "Timeout (second)",
				WidgetPropertyCategory.Basic, 10, 1, 3600));
		addProperty(new StringProperty(PROP_CONFIRM_MESSAGE, "Confirm Message",
				WidgetPropertyCategory.Basic, "")); //$NON-NLS-1$
	}

	@Override
	public ActionType getActionType() {
		return ActionType.WRITE_PV;
	}

	public String getPVName() {
		return (String) getPropertyValue(PROP_PVNAME);
	}

	public String getValue() {
		return (String) getPropertyValue(PROP_VALUE);
	}

	public int getTimeout() {
		return (Integer) getPropertyValue(PROP_TIMEOUT);
	}

	public String getConfirmMessage() {
		return (String) getPropertyValue(PROP_CONFIRM_MESSAGE);
	}

	@Override
	public void run() {

		if (!getConfirmMessage().isEmpty())
			if (!GUIUtil.openConfirmDialog("PV Name: " + getPVName()
					+ "\nNew Value: " + getValue() + "\n\n"
					+ getConfirmMessage()))
				return;

		// If it has the same nave as widget PV name, use it.
		if (getWidgetModel() instanceof IPVWidgetModel) {
			String mainPVName = ((IPVWidgetModel) getWidgetModel()).getPVName();
			if (getPVName().equals(mainPVName)) {
				Object o = getWidgetModel().getRootDisplayModel().getViewer()
						.getEditPartRegistry().get(getWidgetModel());
				if (o instanceof IPVWidgetEditpart) {
					((IPVWidgetEditpart) o).setPVValue(
							IPVWidgetModel.PROP_PVNAME, getValue().trim());
					return;
				}
			}
		}

		Job job = new Job(getDescription()) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				switch (PreferencesHelper.getPVConnectionLayer()) {
				case PV_MANAGER:
					return writePVManagerPV();
				case UTILITY_PV:
				default:
					return writeUtilityPV(monitor);
				}
			}

		};

		job.schedule();
	}

	private IStatus writeUtilityPV(IProgressMonitor monitor) {
		String text = getValue().trim();
		PV pv = null;
		try {
			pv = BOYPVFactory.createPV(getPVName(), false);
			pv.start();
			long startTime = System.currentTimeMillis();
			int timeout = getTimeout() * 1000;
			while ((Calendar.getInstance().getTimeInMillis() - startTime) < timeout
					&& !pv.isConnected() && !monitor.isCanceled()) {
				Thread.sleep(100);
			}
			if (monitor.isCanceled()) {
				ConsoleService.getInstance().writeInfo(
						"\"" + getDescription() + "\" " //$NON-NLS-1$ //$NON-NLS-2$
								+ "has been canceled");
				return Status.CANCEL_STATUS;
			}

			if (!pv.isConnected()) {
				throw new Exception(
						"Connection Timeout! Failed to connect to the PV.");
			}
			if (!pv.isWriteAllowed())
				throw new Exception("The PV is not allowed to write");
			setPVValue(pv, text);
			// If no sleep here, other listeners will have a delay to get
			// update.
			// Don't know the reason, but this is a work around.
			Thread.sleep(200);
		} catch (Exception e1) {
			popErrorDialog(new Exception(e1));
			return Status.OK_STATUS;
		} finally {
			if (pv != null)
				pv.stop();
		}
		return Status.OK_STATUS;
	}

	private IStatus writePVManagerPV(){
		String text = getValue().trim();
		final CountDownLatch latch = new CountDownLatch(1);
		PVWriter<Object> pvWriter = PVManager.write(channel(getPVName()))
				.timeout(TimeDuration.ofSeconds(getTimeout())).writeListener(
						new PVWriterListener<Object>() {
							@Override
							public void pvChanged(PVWriterEvent<Object> event) {
								latch.countDown();
							}
						}).sync();
		try {
			if(latch.await(getTimeout(), TimeUnit.SECONDS))
				pvWriter.write(text);
			else
				throw new Exception(NLS.bind("Failed to connect to the PV in {0} seconds.", getTimeout()));
		} catch (Exception e) {
			popErrorDialog(e);
		}finally{
			pvWriter.close();
		}
				
		return Status.OK_STATUS;
	}
	
	
	/**
	 * Set PV to given value. Should accept Double, Double[], Integer, String,
	 * maybe more.
	 * 
	 * @param pvPropId
	 * @param value
	 */
	protected void setPVValue(final PV pv, final Object value) {
		if (pv != null) {
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
		UIBundlingThread.getInstance().addRunnable(
				getWidgetModel().getRootDisplayModel().getViewer().getControl()
						.getDisplay(), new Runnable() {
					public void run() {
						String message = "Failed to write PV:" + getPVName()
								+ "\n" + 
								(e.getCause() != null? e.getCause().getMessage():e.getMessage());
						ErrorHandlerUtil.handleError(message, e, true, true);
						// ConsoleService.getInstance().writeError(message);
					}
				});
	}

	@Override
	public String getDefaultDescription() {
		return "Write " + getValue() + " to " + getPVName();
	}

}
