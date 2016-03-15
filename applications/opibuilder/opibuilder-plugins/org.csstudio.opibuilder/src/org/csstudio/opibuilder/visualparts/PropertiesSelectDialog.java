/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**Select properties dialog.
 * @author Xihui Chen
 *
 */
public class PropertiesSelectDialog extends Dialog {

    private ListViewer propertiesViewer;
    private List<String> selectedProps;
    private AbstractWidgetModel widgetModel;

    public PropertiesSelectDialog(Shell parentShell,
            AbstractWidgetModel widgetModel) {
        super(parentShell);
        // Allow resize
        setShellStyle(getShellStyle() | SWT.RESIZE);
        selectedProps = new ArrayList<String>();
        this.widgetModel = widgetModel;

    }

    public List<String> getOutput(){
        return selectedProps;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Select Properties to Copy");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite parent_Composite = (Composite) super.createDialogArea(parent);
        Composite rightComposite = new Composite(parent_Composite, SWT.NONE);
        rightComposite.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 320;
        gd.heightHint = 500;
        rightComposite.setLayoutData(gd);

        propertiesViewer = createPropertiesViewer(rightComposite);

        Set<String> propSet = widgetModel.getAllPropertyIDs();

        propSet.remove(AbstractPVWidgetModel.PROP_PVVALUE);

        for(Object propId : propSet.toArray()){
            if(!widgetModel.getProperty(propId.toString()).isVisibleInPropSheet())
                propSet.remove(propId);
        }


        String[] propArray = propSet.toArray(new String[0]);
        Arrays.sort(propArray);

        propertiesViewer.setInput(propArray);

        return parent_Composite;

    }

    private ListViewer createPropertiesViewer(Composite parent) {
        final ListViewer viewer = new ListViewer(parent, SWT.V_SCROLL
                | SWT.H_SCROLL | SWT.BORDER | SWT.MULTI);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new PropertyListLableProvider());
        viewer.getList().setLayoutData(
                new GridData(SWT.FILL, SWT.FILL, true, true));

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                selectedProps = ((StructuredSelection)viewer.getSelection()).toList();
            }
        });
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                okPressed();
            }
        });
        return viewer;
    }

    class PropertyListLableProvider extends LabelProvider {
        @Override
        public String getText(Object element) {
            String propID = (String)element;
            return widgetModel.getProperty(propID).getDescription() + " (" + propID + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

}
