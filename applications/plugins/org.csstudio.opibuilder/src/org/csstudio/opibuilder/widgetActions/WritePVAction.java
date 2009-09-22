package org.csstudio.opibuilder.widgetActions;

import java.util.concurrent.TimeUnit;

import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.ILongValue;
import org.csstudio.platform.data.IStringValue;
import org.csstudio.platform.data.IValue;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class WritePVAction extends AbstractWidgetAction {

	public static final String PROP_PVNAME = "pv_name";//$NON-NLS-1$
	public static final String PROP_VALUE = "value";//$NON-NLS-1$
	private IValue pvValue;
	
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
		PV pv;
		try {
			pv = PVFactory.createPV(getPVName());
		} catch (Exception e1) {
			popErrorDialog(new Exception("Failed to connect to the PV."));
			return;
		}
		//if(!pv.isWriteAllowed()){
		//	popErrorDialog(new Exception("The PV is not allowed to write"));
		//	return;
		//}
		String text = getValue();
		pvValue = pv.getValue();
		pv.addListener(new PVListener(){
			public void pvDisconnected(PV pv) {
				// TODO Auto-generated method stub
				
			}
			public void pvValueUpdate(PV pv) {
				pvValue = pv.getValue();
			}
		});
		try {
			pv.start();
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (Exception e1) {
			popErrorDialog(new Exception("Failed to connect to the PV."));
		}		
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
		}
		pv.stop();
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
					"Failed to write PV:" + getPVName() + "\n" + e;
				MessageDialog.openError(null, "PV write error", message);
				ConsoleService.getInstance().writeError(message);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if(adapter == IWorkbenchAdapter.class)
			return new IWorkbenchAdapter() {
				
				public Object getParent(Object o) {
					return null;
				}
				
				public String getLabel(Object o) {
					return getActionType().getDescription();
				}
				
				public ImageDescriptor getImageDescriptor(Object object) {
					return getActionType().getIconImage();
				}
				
				public Object[] getChildren(Object o) {
					return new Object[0];
				}
			};
		
		return null;
	}

	
	@Override
	public String getDescription() {
		return "Write " + getValue() + " to " + getPVName();
	}

	
}
