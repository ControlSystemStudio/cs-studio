/*******************************************************************************
 * Copyright (c) 2010-2017 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.csstudio.alarm.beast.msghist.Messages;
import org.csstudio.alarm.beast.msghist.PropertyColumnPreference;
import org.csstudio.alarm.beast.ui.alarmtable.Activator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 *
 * <code>ColumnConfigurer</code> is a dialog that allows to configure the visibility/order of table columns as well as their their
 * width and weight.
 *
 * @author Borut Terpinc
 *
 */
public class ColumnConfigurer extends TitleAreaDialog {

    private static final String LEFT = "Back24.gif";
    private static final String RIGHT = "Forward24.gif";
    private static final String UP = "Up24.gif";
    private static final String DOWN = "Down24.gif";
    private static final String ICONS_PATH = "icons/";
    private static final ImageRegistry IMAGES = new ImageRegistry(Display.getDefault());
    static {

        IMAGES.put(LEFT, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, ICONS_PATH + LEFT));
        IMAGES.put(RIGHT, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, ICONS_PATH + RIGHT));
        IMAGES.put(UP, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, ICONS_PATH + UP));
        IMAGES.put(DOWN, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.ID, ICONS_PATH + DOWN));
    }

    private static class ContentProvider implements IStructuredContentProvider {
        @Override
        public Object[] getElements(Object inputElement) {
            List<?> list = (List<?>) inputElement;
            return list.toArray(new PropertyColumnPreference[list.size()]);
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            return;
        }

        @Override
        public void dispose() {
            return;
        }
    }

    private CellLabelProvider labelProvider = new CellLabelProvider() {

        @Override
        public void update(ViewerCell cell) {
            PropertyColumnPreference column = (PropertyColumnPreference) cell.getElement();
            cell.setText(column.getName());
        }

    };

    private TableViewer shownList;
    private TableViewer hiddenList;
    private Button leftButton;
    private Button rightButton;
    private Button upButton;
    private Button downButton;
    private Text widthField;
    private Text weightField;

    private Button okButton;

    private final PropertyColumnPreference[] columns;

    /**
     * Constructs a new configurer.
     *
     * @param parentShell
     *            the parent window
     * @param columns
     *            the columns that will be manipulated
     */
    public ColumnConfigurer(Shell parentShell, PropertyColumnPreference[] columns) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.columns = columns;
    }

    /**
     * @return the columns as they were set by this configurator
     */
    public PropertyColumnPreference[] getColumns() {
        return columns;
    }

    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button b = super.createButton(parent, id, label, defaultButton);
        if (id == IDialogConstants.OK_ID) {
            okButton = b;
        }
        return b;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void okPressed() {
        List<PropertyColumnPreference> hidden = (List<PropertyColumnPreference>) hiddenList.getInput();
        List<PropertyColumnPreference> shown = (List<PropertyColumnPreference>) shownList.getInput();
        int i = 0;
        for (PropertyColumnPreference column : shown) {
            columns[i++] = column;
        }
        for (PropertyColumnPreference column : hidden) {
            columns[i++] = column;
        }
        super.okPressed();
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.ColumnConfigTitle);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        setTitle(Messages.ColumnConfigTitle);
        setMessage(Messages.ColumnConfigDescription);
        Composite base = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout(2, true);
        layout.marginHeight = 0;
        base.setLayout(layout);
        base.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite left = new Composite(base, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        left.setLayout(layout);
        left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label leftTitle = new Label(left, SWT.LEFT);
        leftTitle.setText("Hidden Columns");
        GridData data = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1);
        leftTitle.setLayoutData(data);

        hiddenList = new TableViewer(left);
        hiddenList.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        hiddenList.setLabelProvider(labelProvider);
        hiddenList.setContentProvider(new ContentProvider());
        hiddenList.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                moveLeftRight(true);
            }
        });

        Composite leftRight = new Composite(left, SWT.NONE);
        layout = new GridLayout(1, true);
        layout.marginLeft = 5;
        leftRight.setLayout(layout);
        rightButton = new Button(leftRight, SWT.PUSH);
        rightButton.setToolTipText("Show");
        rightButton.setImage(IMAGES.get(RIGHT));
        rightButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveLeftRight(true);
            }
        });
        rightButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
        leftButton = new Button(leftRight, SWT.PUSH);
        leftButton.setToolTipText("Hide");
        leftButton.setImage(IMAGES.get(LEFT));
        leftButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveLeftRight(false);
            }
        });
        leftButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        leftRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        Composite right = new Composite(base, SWT.NONE);
        right.setLayout(new GridLayout(2, false));
        right.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label rightTitle = new Label(right, SWT.LEFT);
        rightTitle.setText("Shown Columns");
        data = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1);
        rightTitle.setLayoutData(data);

        shownList = new TableViewer(right);
        shownList.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        shownList.setLabelProvider(labelProvider);
        shownList.setContentProvider(new ContentProvider());
        shownList.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                moveLeftRight(false);
            }
        });
        shownList.addSelectionChangedListener(event -> {
            Object obj = ((IStructuredSelection) event.getSelection()).getFirstElement();
            if (obj instanceof PropertyColumnPreference) {
                widthField.setText(String.valueOf(((PropertyColumnPreference) obj).getSize()));
                weightField.setText(String.valueOf(((PropertyColumnPreference) obj).getWeight()));
                widthField.setEnabled(true);
                weightField.setEnabled(true);
            } else {
                widthField.setText("");
                weightField.setText("");
                widthField.setEnabled(false);
                weightField.setEnabled(false);
            }
        });

        Composite upDown = new Composite(right, SWT.NONE);
        upDown.setLayout(new GridLayout(1, true));
        upButton = new Button(upDown, SWT.PUSH);
        upButton.setToolTipText("Up");
        upButton.setImage(IMAGES.get(UP));
        upButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveUpDown(true);
            }
        });
        upButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
        downButton = new Button(upDown, SWT.PUSH);
        downButton.setToolTipText("Down");
        downButton.setImage(IMAGES.get(DOWN));
        downButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveUpDown(false);
            }
        });
        downButton.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 1, 1));
        upDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        List<PropertyColumnPreference> hidden = new LinkedList<>();
        List<PropertyColumnPreference> shown = new LinkedList<>();
        for (PropertyColumnPreference c : columns) {
            if (c.isVisible()) {
                hidden.add(c);
            } else {
                shown.add(c);
            }
        }
        hiddenList.setInput(hidden);
        shownList.setInput(shown);

        updateTables();

        createWidthAndWeightPanel(right);
        Composite c = new Composite(left, SWT.NONE);
        data = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
        data.heightHint = 63;
        c.setLayoutData(data);

        return composite;

    }

    private void createWidthAndWeightPanel(Composite base) {
        Composite widthAndWeightPanel = new Composite(base, SWT.NONE);
        GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        data.heightHint = 63;
        widthAndWeightPanel.setLayoutData(data);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.verticalSpacing = 3;
        layout.marginRight = 0;
        layout.marginWidth = 0;
        widthAndWeightPanel.setLayout(layout);

        Label widthLabel = new Label(widthAndWeightPanel, SWT.NONE);
        widthLabel.setText(Messages.WidthLabel);
        widthLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        widthField = new Text(widthAndWeightPanel, SWT.BORDER);
        data = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        data.widthHint = 80;
        widthField.setLayoutData(data);
        widthField.addModifyListener(e -> {
            if (checkButtons()) {
                IStructuredSelection selection = (IStructuredSelection) shownList.getSelection();
                Object obj = selection.getFirstElement();
                if (obj instanceof PropertyColumnPreference) {
                    ((PropertyColumnPreference) obj).setSize(Integer.parseInt(widthField.getText()));
                }
            }
        });

        Label weightLabel = new Label(widthAndWeightPanel, SWT.NONE);
        weightLabel.setText(Messages.WeightLabel);
        weightLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        weightField = new Text(widthAndWeightPanel, SWT.BORDER);
        data = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        data.widthHint = 80;
        weightField.setLayoutData(data);
        weightField.addModifyListener(e -> {
            if (checkButtons()) {
                IStructuredSelection selection = (IStructuredSelection) shownList.getSelection();
                Object obj = selection.getFirstElement();
                if (obj instanceof PropertyColumnPreference) {
                    ((PropertyColumnPreference) obj).setWeight(Integer.parseInt(weightField.getText()));
                }
            }
        });
    }

    private boolean checkButtons() {
        if (shownList.getSelection().isEmpty()) {
            if (okButton != null) {
                okButton.setEnabled(true);
            }
            return false;
        } else {
            String weight = weightField.getText();
            String width = widthField.getText();
            try {
                // try to convert
                int wi = Integer.parseInt(width);
                int we = Integer.parseInt(weight);
                if (okButton != null) {
                    boolean b = wi > 0 && we > -1;
                    okButton.setEnabled(b);
                    return b;
                }
            } catch (Exception ex) {
                if (okButton != null)
                    okButton.setEnabled(false);
            }
            return false;
        }
    }

    private void moveLeftRight(boolean right) {
        IStructuredSelection selection;
        if (right) {
            selection = (IStructuredSelection) hiddenList.getSelection();
        } else {
            selection = (IStructuredSelection) shownList.getSelection();
        }
        Iterator<?> it = selection.iterator();
        while (it.hasNext()) {
            ((PropertyColumnPreference) it.next()).setVisible(right);
        }
        updateTables();
    }

    private void moveUpDown(boolean up) {
        IStructuredSelection selection = (IStructuredSelection) shownList.getSelection();

        @SuppressWarnings("unchecked")
        List<PropertyColumnPreference> shown = (List<PropertyColumnPreference>) shownList.getInput();
        Iterator<?> it = selection.iterator();
        while (it.hasNext()) {
            PropertyColumnPreference wrapper = ((PropertyColumnPreference) it.next());
            int idx = -1;
            Iterator<PropertyColumnPreference> shownIt = shown.iterator();
            while (shownIt.hasNext()) {
                idx++;
                if (shownIt.next() == wrapper) {
                    break;
                }
            }

            if (up) {
                if (idx > 0) {
                    PropertyColumnPreference w = shown.get(idx - 1);
                    shown.set(idx - 1, wrapper);
                    shown.set(idx, w);
                }
            } else {
                if (idx < shown.size() - 1) {
                    PropertyColumnPreference w = shown.get(idx + 1);
                    shown.set(idx + 1, wrapper);
                    shown.set(idx, w);
                }
            }
        }
        hiddenList.refresh();
        shownList.refresh();
    }

    @SuppressWarnings("unchecked")
    private void updateTables() {
        List<PropertyColumnPreference> hidden = (List<PropertyColumnPreference>) hiddenList.getInput();
        List<PropertyColumnPreference> shown = (List<PropertyColumnPreference>) shownList.getInput();

        ListIterator<PropertyColumnPreference> hi = hidden.listIterator();
        while (hi.hasNext()) {
            PropertyColumnPreference w = hi.next();
            if (w.isVisible()) {
                hi.remove();
                shown.add(w);
            }
        }

        ListIterator<PropertyColumnPreference> si = shown.listIterator();
        while (si.hasNext()) {
            PropertyColumnPreference w = si.next();
            if (!w.isVisible()) {
                si.remove();
                hidden.add(w);
            }
        }

        hiddenList.refresh();
        shownList.refresh();
    }
}
