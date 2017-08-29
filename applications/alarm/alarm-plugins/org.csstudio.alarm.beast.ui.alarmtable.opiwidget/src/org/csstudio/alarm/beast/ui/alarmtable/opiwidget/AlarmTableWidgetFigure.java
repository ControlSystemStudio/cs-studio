/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable.opiwidget;

import org.csstudio.alarm.beast.ui.alarmtable.GUI;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 *
 * <code>AlarmTableWidgetFigure</code> is an implementation of the alarm table as a GEF Figure.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class AlarmTableWidgetFigure extends AbstractSWTWidgetFigure<Composite> {

    private GUI gui;
    private Composite base;

    /**
     * Constructs a new figure.
     *
     * @param editPart the edit part that owns this figure
     */
    public AlarmTableWidgetFigure(AlarmTableWidgetEditPart editPart) {
        super(editPart);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure#createSWTWidget(org.eclipse.swt.widgets.
     * Composite, int)
     */
    @Override
    protected Composite createSWTWidget(Composite parent, int style) {
        base = new Composite(parent, style);
        base.setLayout(new GridLayout(1, true));
        redoAlarmTable();
        return base;
    }

    /**
     * @return the alarm table GUI
     */
    public GUI getAlarmTable() {
        return gui;
    }

    /**
     * Redo the alarm table GUI. If a change of one of the properties in the alarm table model requires a recreation of
     * the alarm table, use this method to create and layout the new table.
     */
    void redoAlarmTable() {
        if (gui != null) {
            gui.dispose();
        }
        if (base != null) {
            AlarmTableWidgetEditPart widgetEditPart = (AlarmTableWidgetEditPart) editPart;
            AlarmTableWidgetModel model = widgetEditPart.getWidgetModel();

            gui = new GUI(base, widgetEditPart.getSite(), model.isWritable(), model.isSeparateTables(), model.getColumns(),
                    model.getSortingColumn(), model.isSortAscending(),model.isTableHeaderVisible());
            gui.setNumberOfAlarmsLimit(model.getMaxNumberOfAlarms());
            gui.setBlinking(model.isUnacknowledgedBlinking());
            gui.setTimeFormat(model.getTimeFormat());
            /*
             * This fixes issue with collapsed rows bug on GNOME. When grabExcessVerticalSpace is true,
             * we have some strange indeterministic issues with rows sometime not
             * rendering properly (collapsed with very small height).
             * This workaround sets the grabExcessVerticalSpace to false and sets the height hint to the size defined by the model.
             */
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
            gd.heightHint = model.getBounds().height;

            gui.setLayoutData(gd);
            int ack = model.getAcknowledgeTableWeight();
            int unack = model.getUnacknowledgeTableWeight();
            gui.setSashWeights(ack, unack);
            gui.setTableColumnsHeadersVisible(model.isColumnsHeadersVisible());
            base.layout();
        }
    }

}
