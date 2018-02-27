/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.opiwidget;

import org.csstudio.alarm.beast.msghist.gui.GUI;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchSite;

/**
 *
 * <code>MessageHistoryWidgetFigure</code> is an implementation of the message
 * history as a GEF Figure.
 *
 * @author Borut Terpinc
 *
 */
public class MessageHistoryWidgetFigure extends AbstractSWTWidgetFigure<Composite> {

    private GUI gui;

    /**
     * Constructs a new figure.
     *
     * @param editPart
     *            the edit part that owns this figure
     */
    public MessageHistoryWidgetFigure(MessageHistoryWidgetEditPart editPart) {
        super(editPart);
    }

    @Override
    protected Composite createSWTWidget(Composite parent, int style) {
        MessageHistoryWidgetEditPart widgetEditPart = (MessageHistoryWidgetEditPart) editPart;
        MessageHistoryWidgetModel widgetModel = widgetEditPart.getWidgetModel();

        gui = new GUI(null, parent, widgetEditPart.getMessageHistoryModel(),
                widgetModel.getColumns(), widgetModel.getSortingColumn(), widgetModel.isSortAscending(),
                widgetModel.isColumnHeaders());
        
        // switch selection listener on focus
        TableViewer tableViewer = gui.getTableViewer();
        tableViewer.getTable().addFocusListener(new FocusListener() {
            
            @Override
            public void focusLost(FocusEvent e) {                
            }
            
            @Override
            public void focusGained(FocusEvent e) {
                IWorkbenchSite site = editPart.getSite();
                if (site != null && (site.getSelectionProvider() instanceof SelectionProviderWrapper)) {
                    SelectionProviderWrapper selectionProviderWrapper = (SelectionProviderWrapper) site
                            .getSelectionProvider();
                    selectionProviderWrapper.setSelectionProvider(tableViewer);
                }
            }
        });
        return gui;

    }

    /**
     * @return the message history GUI
     */
    public GUI getGUI() {
        return gui;
    }
}
