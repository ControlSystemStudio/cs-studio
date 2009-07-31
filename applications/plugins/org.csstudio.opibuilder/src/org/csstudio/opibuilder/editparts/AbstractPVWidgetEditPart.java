package org.csstudio.opibuilder.editparts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.util.UIBundlingThread;
import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.draw2d.Border;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

/**The abstract edit part for all PV armed widgets. 
 * Widgets inheritate this class will have the CSS context menu on it.
 * @author Xihui Chen
 *
 */
public abstract class AbstractPVWidgetEditPart extends AbstractWidgetEditPart 
	implements IProcessVariable{
	
	private static RGB DISCONNECTED_COLOR = new RGB(255, 0, 255);
	
	private Map<String, PV> pvMap = new HashMap<String, PV>(); 
	private Map<String, Boolean> pvConnectedStatusMap = new HashMap<String, Boolean>();
	
	private boolean connected = true;
	private boolean preEnableState;
	private Border preBorder;
	private String currentDisconnectPVName = "";
	
	private void markWidgetAsDisconnected(final String pvName){
		if(!connected)
			return;
		connected = false;
		preEnableState = getCastedModel().isEnabled();
		preBorder = figure.getBorder();
		
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				boolean allDisconnected = true;
				for(boolean c : pvConnectedStatusMap.values()){
					allDisconnected &= !c;
				}
				figure.setEnabled(!allDisconnected);
				figure.setBorder(BorderFactory.createBorder(
						BorderStyle.TITLE_BAR, 1, DISCONNECTED_COLOR, 
						allDisconnected ? "Disconnected" : pvName + ": Disconnected"));
				currentDisconnectPVName = pvName;
				figure.repaint();
			}
		});
		
		
	}
	
	private void widgetConnectionRecovered(final String pvName){
		if(connected)
			return;
		connected = true;
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				boolean allConnected = true;
				String nextDisconnecteName = ""; //$NON-NLS-1$
				
				for(String s : pvConnectedStatusMap.keySet()){
					boolean c = pvConnectedStatusMap.get(s);
					allConnected &= c;
					if(!c)
						nextDisconnecteName = s;					
				}
				figure.setEnabled(preEnableState);
				if(allConnected)
					figure.setBorder(preBorder);
				else if(currentDisconnectPVName.equals(pvName) || currentDisconnectPVName.equals("")){ //$NON-NLS-1$
					figure.setBorder(BorderFactory.createBorder(
						BorderStyle.TITLE_BAR, 1, DISCONNECTED_COLOR, 
						nextDisconnecteName + " : Disconnected"));
					currentDisconnectPVName = nextDisconnecteName;
				}
				figure.repaint();
			}
		});		
	}
	
	@Override
	public void activate() {
		if(!isActive()){
			super.activate();
			pvMap.clear();	
			pvConnectedStatusMap.clear();
			if(getExecutionMode() == ExecutionMode.RUN_MODE){
				markWidgetAsDisconnected("");
				final Map<StringProperty, PVValueProperty> pvValueMap = getCastedModel().getPVMap();
				for(final StringProperty sp : pvValueMap.keySet()){
					if(sp.getPropertyValue() == null || 
							sp.getPropertyValue().equals("")) 
						continue;
					
					try {
						PV pv = PVFactory.createPV((String) sp.getPropertyValue());
						pvConnectedStatusMap.put(pv.getName(), false);
						if(!pv.isWriteAllowed()){
							figure.setCursor(new Cursor(null, SWT.CURSOR_NO));
							figure.setEnabled(false);
						}
						pv.addListener(new PVListener(){
							public void pvDisconnected(PV pv) {
								pvConnectedStatusMap.put(pv.getName(), false);
								markWidgetAsDisconnected(pv.getName());
							}

							public void pvValueUpdate(PV pv) {
								if(!pvConnectedStatusMap.get(pv.getName())){
									pvConnectedStatusMap.put(pv.getName(), true);
									widgetConnectionRecovered(pv.getName());
								}
								pvValueMap.get(sp).setPropertyValue(pv.getValue());		
								
							}							
						});
						pv.start();
						pvMap.put(sp.getPropertyID(), pv);
					} catch (Exception e) {
						pvConnectedStatusMap.put((String) sp.getPropertyValue(), false);
						markWidgetAsDisconnected((String) sp.getPropertyValue());
						CentralLogger.getInstance().error(this, "Unable to connect to PV:" +
								(String)sp.getPropertyValue());
					}					
				}
			}
		}
	}
	
	@Override
	public void deactivate() {
		if(isActive()){
			super.deactivate();
			for(PV pv : pvMap.values())				
				pv.stop();
			pvMap.clear();
		}
	}
	
	public String getName() {		
		 if(getCastedModel().getPVMap().isEmpty())
			 return "";
		return (String)((StringProperty)getCastedModel().getPVMap().keySet().toArray()[0])
			.getPropertyValue();
	}
	
	public String getTypeId() {
		return TYPE_ID;
	}
	
	protected void setPVValue(String pvPropId, Object value){
		final PV pv = pvMap.get(pvPropId);
		if(pv != null){
			try {
				pv.setValue(value);
			} catch (Exception e) {
				UIBundlingThread.getInstance().addRunnable(new Runnable(){
					public void run() {
						MessageBox mb = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								SWT.ICON_ERROR | SWT.OK);
						mb.setMessage("Failed to write PV:" + pv.getName());
						mb.setText("PV write error");
						mb.open();
					}
				});
				
				
			}
		}
	}

}
