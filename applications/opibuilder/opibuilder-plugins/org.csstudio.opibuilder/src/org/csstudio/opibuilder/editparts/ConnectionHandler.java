/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editparts;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.AlarmRepresentationScheme;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Display;

/**
 * The handler help a widget to handle the pv connection event such as
 * PVs' disconnection, connection recovered. It will show a disconnect border on the widget
 * if any one of the PVs is disconnected. The detailed disconnected information will be displayed
 * as tooltip.
 * @author Xihui Chen
 *
 */
public class ConnectionHandler {

    private final class PVConnectionListener extends IPVListener.Stub {

        private boolean lastValueIsNull;

        @Override
        public void valueChanged(IPV pv) {
            if(lastValueIsNull && pv.getValue()!=null){
                lastValueIsNull = false;
                widgetConnectionRecovered(pv, true);
            }
        }

        @Override
        public void connectionChanged(IPV pv) {
            if(pv.isConnected()){
                lastValueIsNull = (pv.getValue()==null);
                widgetConnectionRecovered(pv, false);
            }
            else
                markWidgetAsDisconnected(pv);
        }

    }

    private Map<String, IPV> pvMap;

    /**
     * True if all PVs are connected.
     */
    private boolean connected;

    private String toolTipText;

    private IFigure figure;

    private AbstractWidgetModel widgetModel;
    private Display display;

    protected AbstractBaseEditPart editPart;

    private boolean hasNullValue;

    /**
     * @param editpart the widget editpart to be handled.
     */
    public ConnectionHandler(AbstractBaseEditPart editpart) {
        this.editPart = editpart;
        figure = editpart.getFigure();
        widgetModel = editpart.getWidgetModel();
        this.display = editpart.getViewer().getControl().getDisplay();
        pvMap = new ConcurrentHashMap<String, IPV>();
        connected = true;
    }

    /**Add a PV to this handler, so its connection event can be handled.
     * @param pvName name of the PV.
     * @param pv the PV object.
     */
    public void addPV(final String pvName, final IPV pv){
        pvMap.put(pvName, pv);
        markWidgetAsDisconnected(pv);
        pv.addListener(new PVConnectionListener());
    }

    public void removePV(final String pvName){
        if(pvMap == null){
            return;
        }
        pvMap.remove(pvName);
    }

    private void refreshModelTooltip(){
        StringBuilder sb = new StringBuilder();
        for(Entry<String, IPV> entry : pvMap.entrySet()){
            if(!entry.getValue().isConnected()){
                sb.append(entry.getKey() + " is disconnected.\n");
            }else if(entry.getValue().getValue() == null){
                sb.append(entry.getKey() + " has null value.\n");
            }
        }
        if(sb.length()>0){
            sb.append("------------------------------\n");
            toolTipText = sb.toString();
        }else
            toolTipText = "";
    }

    /**Mark a widget as disconnected.
     * @param pvName the name of the PV that is disconnected.
     */
    protected void markWidgetAsDisconnected(IPV pv){
        refreshModelTooltip();
        if(!connected)
            return;
        connected = false;
        //Making this task execute in UI Thread
        //It will also delay the disconnect marking requested during widget activating
        //to execute after widget is fully activated.
        UIBundlingThread.getInstance().addRunnable(display, new Runnable(){
            @Override
            public void run() {
                figure.setBorder(AlarmRepresentationScheme.getDisonnectedBorder());
            }
        });
    }

    /**Update the widget when a PV' connection is recovered.
     * @param pvName the name of the PV whose connection is recovered.
     * @param valueChangedFromNull true if this is called because value changed from null value.
     */
    protected void widgetConnectionRecovered(IPV pv, boolean valueChangedFromNull){

        if (connected && !valueChangedFromNull)
            return;
        boolean allConnected = true;
        hasNullValue = false;
        for (IPV pv2 : pvMap.values()) {
            allConnected &= pv2.isConnected();
            hasNullValue |=(pv2.getValue()==null);
        }
        refreshModelTooltip();
        if (allConnected) {
            connected = true;
            UIBundlingThread.getInstance().addRunnable(display, new Runnable() {
                @Override
                public void run() {
                    if(hasNullValue)
                        figure.setBorder(
                                AlarmRepresentationScheme.getInvalidBorder(BorderStyle.DOTTED));
                    else
                        figure.setBorder(editPart.calculateBorder());

                }
            });
        }
    }

    /**
     * @return true if all pvs are connected.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * @return true if one or some PVs have null values.
     */
    public boolean isHasNullValue() {
        return hasNullValue;
    }

    /**
     * @return the map with all PVs. It is not allowed to change the Map.
     */
    public Map<String, IPV> getAllPVs() {
        return pvMap;
    }

    public String getToolTipText() {
        return toolTipText;
    }

}
