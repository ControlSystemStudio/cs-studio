/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable.opiwidget;

import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.ui.alarmtable.GUI;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.util.AlarmRepresentationScheme;
import org.eclipse.draw2d.IFigure;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

/**
 *
 * <code>AlarmTableWidgetEditPart</code> is the edit part for the alarm table opi widget.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class AlarmTableWidgetEditPart extends AbstractWidgetEditPart implements AlarmClientModelListener {

    private static final Logger LOGGER = Logger.getLogger(AlarmTableWidgetEditPart.class.getName());

    private AlarmClientModel model;

    private boolean isItemNull;

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.editparts.AbstractBaseEditPart#doCreateFigure()
     */
    @Override
    protected IFigure doCreateFigure() {
        AlarmTableWidgetFigure figure = new AlarmTableWidgetFigure(this);
        updateFilter(figure.getAlarmTable());
        return figure;
    }

    private GUI getAlarmTable() {
        return ((AlarmTableWidgetFigure) getFigure()).getAlarmTable();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.editparts.AbstractBaseEditPart#activate()
     */
    @Override
    public void activate() {
        super.activate();
        setUpModel();
    }

    private void setUpModel() {
        if (getExecutionMode() == ExecutionMode.RUN_MODE) {
            if (model != null) {
                model.removeListener(this);
                model.release();
                model = null;
            }
            try {
                model = AlarmClientModel.getInstance(getWidgetModel().getAlarmConfigName());
                model.addListener(this);
                // if the model already exists from before, no connection events will be triggered. In that case
                // the border needs to be set now
                if (model.isServerAlive()) {
                    figure.setBorder(calculateBorder());
                } else {
                    figure.setBorder(AlarmRepresentationScheme.getDisonnectedBorder());
                }
                updateFilter(getAlarmTable());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, NLS.bind(Messages.ModelCreationError, getWidgetModel().getAlarmConfigName()),
                    e);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.editparts.AbstractBaseEditPart#deactivate()
     */
    @Override
    public void deactivate() {
        super.deactivate();
        if (model != null) {
            model.removeListener(this);
            model.release();
            model = null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.editparts.AbstractBaseEditPart#registerPropertyChangeHandlers()
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        AlarmTableWidgetModel wmodel = getWidgetModel();
        wmodel.getProperty(AlarmTableWidgetModel.PROP_UNACKNOWLEDGED_BLINKING).addPropertyChangeListener(evt -> {
            Object obj = evt.getNewValue();
            if (obj instanceof Boolean) {
                getAlarmTable().setBlinking((Boolean) obj);
            }
        });
        wmodel.getProperty(AlarmTableWidgetModel.PROP_TIMEFORMAT).addPropertyChangeListener(evt -> {
            Object obj = evt.getNewValue();
            if (obj instanceof String) {
                getAlarmTable().setTimeFormat((String) obj);
            }
        });
        wmodel.getProperty(AlarmTableWidgetModel.PROP_FILTER_ITEM).addPropertyChangeListener(evt -> setUpModel());
        wmodel.getProperty(AlarmTableWidgetModel.PROP_MAX_NUMBER_OF_ALARMS).addPropertyChangeListener(evt -> {
            Object obj = evt.getNewValue();
            if (obj instanceof Integer) {
                getAlarmTable().setNumberOfAlarmsLimit((Integer) obj);
            }

        });
        PropertyChangeListener listener = evt -> {
            int ack = getWidgetModel().getAcknowledgeTableWeight();
            int unack = getWidgetModel().getUnacknowledgeTableWeight();
            getAlarmTable().setSashWeights(ack, unack);

        };
        wmodel.getProperty(AlarmTableWidgetModel.PROP_ACK_TABLE_WEIGHT).addPropertyChangeListener(listener);
        wmodel.getProperty(AlarmTableWidgetModel.PROP_UNACK_TABLE_WEIGHT).addPropertyChangeListener(listener);
        getWidgetModel().getProperty(AlarmTableWidgetModel.PROP_SEPARATE_TABLES).addPropertyChangeListener(evt -> {
            Object obj = evt.getNewValue();
            if (obj instanceof Boolean) {
                boolean b = (Boolean) obj;
                wmodel.setPropertyVisible(AlarmTableWidgetModel.PROP_ACK_TABLE_WEIGHT, b);
                wmodel.setPropertyVisible(AlarmTableWidgetModel.PROP_UNACK_TABLE_WEIGHT, b);
            }

        });

        listener = evt -> {
            ((AlarmTableWidgetFigure) getFigure()).redoAlarmTable();
            updateFilter(getAlarmTable());
        };
        getWidgetModel().getProperty(AlarmTableWidgetModel.PROP_COLUMNS).addPropertyChangeListener(listener);
        getWidgetModel().getProperty(AlarmTableWidgetModel.PROP_SEPARATE_TABLES).addPropertyChangeListener(listener);
        getWidgetModel().getProperty(AlarmTableWidgetModel.PROP_SORT_ASCENDING).addPropertyChangeListener(listener);
        getWidgetModel().getProperty(AlarmTableWidgetModel.PROP_SORTING_COLUMN).addPropertyChangeListener(listener);
        getWidgetModel().getProperty(AlarmTableWidgetModel.PROP_WRITABLE).addPropertyChangeListener(listener);
        getWidgetModel().getProperty(AlarmTableWidgetModel.PROP_TABLE_HEADER_VISIBLE)
            .addPropertyChangeListener(listener);
        getWidgetModel().getProperty(AlarmTableWidgetModel.PROP_COLUMNS_HEADERS_VISIBLE)
            .addPropertyChangeListener(evt -> {
                Object obj = evt.getNewValue();
                if (obj instanceof Boolean) {
                    ((AlarmTableWidgetFigure) getFigure()).getAlarmTable().setTableColumnsHeadersVisible((Boolean) obj);
                }
            });
    }

    private void updateFilter(GUI gui) {
        if (model != null) {
            String filterItemPath = getWidgetModel().getFilterItem();
            AlarmTreeRoot root = model.getConfigTree().getRoot();
            AlarmTreeItem item = root.getItemByPath(filterItemPath);
            isItemNull = (item == null) ? true : false;
            if (isItemNull) {
                executeWithDisplay(() -> figure.setBorder(AlarmRepresentationScheme.getDisonnectedBorder()));
                getAlarmTable().getActiveAlarmTable().getTable().setEnabled(false);
            }
            else {
                getAlarmTable().getActiveAlarmTable().getTable().setEnabled(true);
                getAlarmTable().setFilterItem(item, model);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.editparts.AbstractBaseEditPart#getWidgetModel()
     */
    @Override
    public AlarmTableWidgetModel getWidgetModel() {
        return (AlarmTableWidgetModel) super.getWidgetModel();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelConfigListener#newAlarmConfiguration(org.csstudio.alarm.
     * beast.ui.clientmodel.AlarmClientModel)
     */
    @Override
    public void newAlarmConfiguration(AlarmClientModel model) {
        executeWithDisplay(() -> {
            updateFilter(getAlarmTable());
            if (!getWidgetModel().isTableHeaderVisible()) {
                if (!model.isServerAlive() || isItemNull) {
                    figure.setBorder(AlarmRepresentationScheme.getDisonnectedBorder());
                } else {
                    figure.setBorder(calculateBorder());
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener#serverModeUpdate(org.csstudio.alarm.beast.ui.
     * clientmodel.AlarmClientModel, boolean)
     */
    @Override
    public void serverModeUpdate(AlarmClientModel model, boolean maintenance_mode) {
        // ignore
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener#serverTimeout(org.csstudio.alarm.beast.ui.
     * clientmodel.AlarmClientModel)
     */
    @Override
    public void serverTimeout(AlarmClientModel model) {
        // ignore
        if (!getWidgetModel().isTableHeaderVisible()) {
            executeWithDisplay(() -> figure.setBorder(AlarmRepresentationScheme.getDisonnectedBorder()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener#newAlarmState(org.csstudio.alarm.beast.ui.
     * clientmodel.AlarmClientModel, org.csstudio.alarm.beast.client.AlarmTreePV, boolean)
     */
    @Override
    public void newAlarmState(AlarmClientModel model, AlarmTreePV pv, boolean parent_changed) {
        if (!getWidgetModel().isTableHeaderVisible()) {
            if (isItemNull)
                executeWithDisplay(() -> figure.setBorder(AlarmRepresentationScheme.getDisonnectedBorder()));
            else
                executeWithDisplay(() -> figure.setBorder(calculateBorder()));
        }
    }

    private void executeWithDisplay(Runnable r) {
        if (getViewer().getControl().isDisposed()) {
            return;
        }
        final Display display = getViewer().getControl().getDisplay();
        if (display.isDisposed()) {
            return;
        }

        display.asyncExec(() -> {
            if (!display.isDisposed()) {
                r.run();
            }
        });
    }

}
