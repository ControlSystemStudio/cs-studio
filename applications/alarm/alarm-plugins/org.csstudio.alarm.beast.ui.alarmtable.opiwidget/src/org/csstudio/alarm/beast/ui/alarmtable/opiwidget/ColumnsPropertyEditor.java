/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable.opiwidget;

import org.csstudio.alarm.beast.ui.alarmtable.ColumnConfigurer;
import org.csstudio.alarm.beast.ui.alarmtable.ColumnWrapper;
import org.csstudio.opibuilder.visualparts.AbstractDialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * <code>ColumnsPropertyEditor</code> provides the columns editor dialog to select the columns to display in the table
 * and their order, width, and weight.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ColumnsPropertyEditor extends AbstractDialogCellEditor {

    private ColumnsInput columnsInput;

    /**
     * Constructs a new property editor.
     *
     * @param parent the parent component
     */
    public ColumnsPropertyEditor(Composite parent) {
        super(parent, "");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.visualparts.AbstractDialogCellEditor#openDialog(org.eclipse.swt.widgets.Shell,
     * java.lang.String)
     */
    @Override
    protected void openDialog(Shell parentShell, String dialogTitle) {
        ColumnWrapper[] columns = ColumnWrapper.getCopy(columnsInput.getColumns());
        ColumnConfigurer dialog = new ColumnConfigurer(parentShell, columns, "", false, true);

        if (dialog.open() == Window.OK) {
            columnsInput = new ColumnsInput(dialog.getColumns());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.visualparts.AbstractDialogCellEditor#shouldFireChanges()
     */
    @Override
    protected boolean shouldFireChanges() {
        return columnsInput != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.CellEditor#doGetValue()
     */
    @Override
    protected Object doGetValue() {
        return columnsInput;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.viewers.CellEditor#doSetValue(java.lang.Object)
     */
    @Override
    protected void doSetValue(Object value) {
        if (value instanceof ColumnsInput) {
            columnsInput = (ColumnsInput) value;
        } else {
            columnsInput = new ColumnsInput();
        }
    }
}
