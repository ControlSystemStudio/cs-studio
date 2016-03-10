/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editparts;

import org.csstudio.simplepv.IPV;
import org.csstudio.ui.util.thread.UIBundlingThread;

/**
 * The connection handler for PV widget. It will set the enable state of the widget
 * based on control PV's connectivity.
 * @author Xihui Chen
 *
 */
public class PVWidgetConnectionHandler extends ConnectionHandler{


    /**
     * @param editpart the editpart must implemented {@link IPVWidgetEditpart}
     */
    public PVWidgetConnectionHandler(AbstractBaseEditPart editpart) {
        super(editpart);
    }

    @Override
    protected void markWidgetAsDisconnected(IPV pv) {
        super.markWidgetAsDisconnected(pv);
        IPVWidgetEditpart pvWidgetEditpart = (IPVWidgetEditpart) editPart;
        final IPV controlPV = pvWidgetEditpart.getControlPV();
        if(controlPV != null && controlPV == pv){
        UIBundlingThread.getInstance().addRunnable(
                editPart.getRoot().getViewer().getControl().getDisplay(),
                new Runnable() {
                    @Override
                    public void run() {
                        pvWidgetEditpart.setControlEnabled(false);
                    }
                });
        }
    }

//    @Override
//    protected void widgetConnectionRecovered(PV pv) {
//        if(isConnected())
//            return;
//        super.widgetConnectionRecovered(pv);
//        final PV controlPV = ((IPVWidgetEditpart)editPart).getControlPV();
//        if(controlPV != null && controlPV == pv){
//        UIBundlingThread.getInstance().addRunnable(
//                editPart.getRoot().getViewer().getControl().getDisplay(),
//                new Runnable() {
//            public void run() {
//                editPart.getFigure().setEnabled(
//                        editPart.getWidgetModel().isEnabled()
//                        && controlPV.isWriteAllowed());
//            }
//        });
//        }
//
//    }




}
