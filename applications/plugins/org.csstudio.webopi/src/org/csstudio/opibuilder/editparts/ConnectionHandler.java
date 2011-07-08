/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editparts;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.AlarmRepresentationScheme;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;

/**
 * The handler help a widget to handle the pv connection event such as
 * PVs' disconnection, connection recovered. It will show a disconnect border on the widget
 * if any one of the PVs is disconnected. The detailed disconnected information will be displayed
 * as tooltip.
 * @author Xihui Chen
 *
 */
public class ConnectionHandler {

	private final class PVConnectionListener implements PVListener {
		public void pvValueUpdate(PV pv) {
			widgetConnectionRecovered(pv);
		}

		public void pvDisconnected(PV pv) {
			markWidgetAsDisconnected(pv);
		}
	}

	private Map<String, PV> pvMap;
	
	/**
	 * True if all PVs are connected.
	 */
	private boolean connected;
	
	/**
	 * The original border of the figure when is was connected.
	 */
	private Border preBorder;
	
	/**
	 * The original tool tip when is was connected.
	 */
	private String preTooltip;	
	
	private IFigure figure;
	
	private AbstractWidgetModel widgetModel;
	
	private PVConnectionListener pvConnectionListener;
	
	/**
	 * @param editpart the widget editpart to be handled.
	 */
	public ConnectionHandler(AbstractBaseEditPart editpart) {
		figure = editpart.getFigure();
		widgetModel = editpart.getWidgetModel();
		pvMap = new HashMap<String, PV>();
		preTooltip = widgetModel.getRawTooltip();
		preBorder = figure.getBorder();
		connected = true;
	}
	
	/**Add a PV to this handler, so its connection event can be handled.
	 * @param pvName name of the PV.
	 * @param pv the PV object.
	 */
	public void addPV(final String pvName, final PV pv){
		pvMap.put(pvName, pv);
		markWidgetAsDisconnected(pv);
		if(pvConnectionListener == null)
			pvConnectionListener = new PVConnectionListener();
		pv.addListener(pvConnectionListener);
	}
	
	public void removePV(final String pvName){	
		if(pvMap == null){
			return;
		}
		if(pvMap.containsKey(pvName)){
			pvMap.get(pvName).removeListener(pvConnectionListener);			
		}
		pvMap.remove(pvName);
	}
	
	private void refreshModelTooltip(){		
		StringBuilder sb = new StringBuilder();
		for(Entry<String, PV> entry : pvMap.entrySet()){
			if(!entry.getValue().isConnected()){
				sb.append(entry.getKey() + " is disconnected.\n");
			}
		}		
		if(sb.length()>0)
			sb.append("------------------------------\n");
		widgetModel.setTooltip(sb.toString() + preTooltip);
	}
	
	/**Mark a widget as disconnected.
	 * @param pvName the name of the PV that is disconnected.
	 */
	protected void markWidgetAsDisconnected(PV pv){
		if(connected){
			preBorder = figure.getBorder();
			preTooltip = widgetModel.getRawTooltip();
		}
		refreshModelTooltip();
		if(!connected)
			return;
		connected = false;	
		//Making this task execute in UI Thread
		//It will also delay the disconnect marking requested during widget activating
		//to execute after widget is fully activated.
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				figure.setBorder(AlarmRepresentationScheme.getDisonnectedBorder());
				figure.repaint();
			}
		});		
	}
	
	/**Update the widget when a PV' connection is recovered.
	 * @param pvName the name of the PV whose connection is recovered.
	 */
	protected void widgetConnectionRecovered(PV pv){		
		
		if(connected)
			return;
		
		UIBundlingThread.getInstance().addRunnable(new Runnable(){
			public void run() {
				boolean allConnected = true;
				refreshModelTooltip();
				for(PV pv : pvMap.values()){
					allConnected &= pv.isConnected();
				}
				if(allConnected){
					figure.setBorder(preBorder);
					connected = true;
				}				
				figure.repaint();
			}
		});		
	}

	/**
	 * @return true if all pvs are connected.
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @return the map with all PVs. It is not allowed to change the Map.
	 */
	public Map<String, PV> getAllPVs() {
		return pvMap;
	}
	
}
