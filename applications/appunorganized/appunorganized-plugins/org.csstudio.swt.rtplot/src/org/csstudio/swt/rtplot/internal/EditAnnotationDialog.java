/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.rtplot.Annotation;
import org.csstudio.swt.rtplot.Messages;
import org.csstudio.swt.rtplot.RTPlot;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.csstudio.swt.rtplot.undo.RemoveAnnotationAction;
import org.csstudio.swt.rtplot.undo.UpdateAnnotationTextAction;
import org.csstudio.swt.rtplot.util.MultiLineInputDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/** Dialog for editing or removing annotations
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EditAnnotationDialog<XTYPE extends Comparable<XTYPE>> extends Dialog
{
    final private RTPlot<XTYPE> plot;
    final private List<Annotation<XTYPE>> annotations;
    private Table annotation_list;

    public EditAnnotationDialog(final Shell shell, final RTPlot<XTYPE> plot)
    {
        super(shell);
        this.plot = plot;
        annotations = new ArrayList<>();
        for (Annotation<XTYPE> annotation : plot.getAnnotations())
            if (! annotation.isInternal())
                annotations.add(annotation);
    }

    @Override
    protected void configureShell(final Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.EditAnnotation);
    }

    @Override
    protected boolean isResizable()
    {
        return true;
    }

    @Override
    protected Control createDialogArea(final Composite parent)
    {
        final Composite composite = (Composite) super.createDialogArea(parent);
        final SWTMediaPool media = new SWTMediaPool(composite);

        // Table of annotation
        //   [x] Trace, Text
        // where each line uses the trace color
        annotation_list = new Table(composite, SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        annotation_list.setHeaderVisible(true);
        annotation_list.setLinesVisible(true);
        new TableColumn(annotation_list, SWT.NONE).setText(Messages.EditAnnotation_Trace);
        new TableColumn(annotation_list, SWT.NONE).setText(Messages.EditAnnotation_Text);
        annotation_list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        for (Annotation<XTYPE> annotation : annotations)
        {
            final TableItem item = new TableItem(annotation_list, SWT.NONE);
            item.setText(new String[] { annotation.getTrace().getName(), annotation.getText().replaceAll("\n", "\\\\n") });
            item.setForeground(media.get(annotation.getTrace().getColor()));
            item.setChecked(true);
        }
        for (TableColumn column : annotation_list.getColumns())
            column.pack();
        annotation_list.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
                final int selected = annotation_list.getSelectionIndex();
                if (selected >= 0  &&  selected < annotations.size())
                    editAnnotation(annotations.get(selected));
            }
        });

        final Label info = new Label(composite, SWT.WRAP);
        info.setText(Messages.EditAnnotation_Info);
        info.setLayoutData(new GridData(SWT.FILL, 0, true, false));

        return composite;
    }

    private void editAnnotation(final Annotation<XTYPE> annotation)
    {
        final InputDialog editor = new MultiLineInputDialog(getShell(), "Edit Annotation", "Modify the annotation text",
                annotation.getText(), null);
        if (editor.open() == OK)
        {
            final String new_text = editor.getValue();
            plot.getUndoableActionManager().execute(
                    new UpdateAnnotationTextAction<XTYPE>(plot, annotation, new_text));
            // Close the 'parent' dialog
            super.okPressed();
        }
    }

    @Override
    protected void okPressed()
    {   // List indices change as the list is modified.
        // Simplify this via separate list of items to remove.
        final List<Annotation<XTYPE>> to_remove = new ArrayList<>();
        for (int i=0; i<annotation_list.getItemCount(); ++i)
            if (! annotation_list.getItem(i).getChecked())
                to_remove.add(annotations.get(i));
        for (Annotation<XTYPE> annotation : to_remove)
        {
            plot.getUndoableActionManager().execute(
                new RemoveAnnotationAction<XTYPE>(plot, annotation));
        }
        super.okPressed();
    }
}
