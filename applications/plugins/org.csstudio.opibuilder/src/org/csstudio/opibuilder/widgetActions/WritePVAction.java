package org.csstudio.opibuilder.widgetActions;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;

/**An actions writing value to a PV.
 * @author Xihui Chen
 *
 */
public class WritePVAction extends AbstractWidgetAction {

	private static final int TIMEOUT = 10000;
	public static final String PROP_PVNAME = "pv_name";//$NON-NLS-1$
	public static final String PROP_VALUE = "value";//$NON-NLS-1$
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(PROP_PVNAME, "PV Name", 
				WidgetPropertyCategory.Basic, "$(pv_name)")); //$NON-NLS-1$
		addProperty(new StringProperty(PROP_VALUE, "Value", 
				WidgetPropertyCategory.Basic, "")); //$NON-NLS-1$
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
	
	@Override
	public void run() {
		
		Job job = new Job(getDescription()){
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				String text = getValue().trim();
				PV pv = null;	
				try {
					pv = PVFactory.createPV(getPVName());
					pv.start();
					long startTime = Calendar.getInstance().getTimeInMillis();
					
					while((Calendar.getInstance().getTimeInMillis() - startTime) < TIMEOUT && 
							!pv.isConnected() && !monitor.isCanceled()){
						TimeUnit.MILLISECONDS.sleep(100);
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
		
	/*
		if(pvValue == null){			
			popErrorDialog(new Exception("Unknown PV"));
		}
		else if(pvValue instanceof IDoubleValue){
			try {
				Double d = Double.parseDouble(text);
				setPVValue(pv, d);
			} catch (NumberFormatException e) {
				popErrorDialog(e);
			}
		}else if(pvValue instanceof ILongValue || pvValue instanceof IEnumeratedValue){
			try {
				Integer l = Integer.parseInt(text);
				setPVValue(pv, l);
			} catch (NumberFormatException e) {		
				popErrorDialog(e);
			}
		}else if(pvValue instanceof IStringValue){
			setPVValue(pv, text);
		}*/
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
				MessageDialog.openError(null, "PV write error", message);
				ConsoleService.getInstance().writeError(message);
			}
		});
	}

	
	@Override
	public String getDescription() {
		return "Write " + getValue() + " to " + getPVName();
	}

	
}
