/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Combo-type widget that allows selecting multiple items.
 *
 * <p>
 * Takes a list of {@link Object}s as input.
 *
 * <p>
 * The <code>toString()</code> of each Object is displayed in a drop-down list.
 * Overriding the stringRepresention() method, the user can define an
 * alternative way to convert T to String.
 *
 * <p>
 * One or more items can be selected, they're also displayed in the text field.
 *
 * <p>
 * Items can be entered in the text field, comma-separated. If entered text does
 * not match a valid item, text is highlighted and tool-tip indicates error.
 *
 * <p>
 * Keyboard support: 'Down' key in text field opens drop-down. Inside drop-down,
 * single item can be selected via cursor key and 'RETURN' closes the drop-down.
 *
 * TODO Auto-completion while typing?
 *
 * @author Kay Kasemir, Kunal Shroff
 */
public class MultipleSelectionCombo<T> extends Composite {
    final private static String SEPARATOR = ", "; //$NON-NLS-1$
    final private static String SEPERATOR_PATTERN = "\\s*,\\s*"; //$NON-NLS-1$

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
        this);

    private Display display;

    private Text text;

    /** Pushing the drop_down button opens the popup */
    private Button drop_down;

    private Shell popup;
    private Button[] itemButtons;

    /** Items to show in list */
    private List<T> items = new ArrayList<T>();

    /** Selection indices */
    private List<Integer> selectionIndex = new ArrayList<Integer>();

    private String tool_tip = null;
    private Color text_color = null;

    /**
     * When list looses focus, the event time is noted here. This prevents the
     * drop-down button from re-opening the list right away.
     */
    private long lost_focus = 0;

    private volatile boolean modify = false;

    /**
     * Initialize
     *
     * @param parent
     * @param style
     */
    public MultipleSelectionCombo(final Composite parent, final int style) {
    super(parent, style);
    createComponents(parent);
    }

    /**
     * Create SWT components
     *
     * @param parent
     */
    private void createComponents(final Composite parent) {
    display = parent.getDisplay();
    final GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.marginWidth = 0;
    setLayout(layout);

    addPropertyChangeListener(new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
        switch (e.getPropertyName()) {
        case "selection":
            if (modify) {
            break;
            } else {
            updateText();
            break;
            }
        case "items":
            setSelection(Collections.<T> emptyList());
            break;
        default:
            break;
        }
        }
    });

    text = new Text(this, SWT.BORDER);
    GridData gd = new GridData(SWT.FILL, 0, true, false);
    text.setLayoutData(gd);
    text.setText("Select Items ...");
    text.addModifyListener(new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
        // Analyze text, update selection
        final String items_text = text.getText();
        modify = true;
        setSelection(items_text);
        modify = false;
        }
    });
    text.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(final KeyEvent e) {
        switch (e.keyCode) {
        case SWT.ARROW_DOWN:
            drop(true);
            return;
        case SWT.ARROW_UP:
            drop(false);
            return;
        case SWT.CR:
            modify =false;
            updateText();
        }
        }
    });

    drop_down = new Button(this, SWT.ARROW | SWT.DOWN);
    gd = new GridData(SWT.FILL, SWT.FILL, false, false);
    gd.heightHint = text.getBounds().height;
    drop_down.setLayoutData(gd);
    drop_down.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(final SelectionEvent e) {
        // Was list open, user clicked this button to close,
        // and list self-closed because is lost focus?

        // e.time is an unsigned integer and should be AND'ed with
        // 0xFFFFFFFFL so that it can be treated as a signed long.
        if ((e.time & 0xFFFFFFFFL) - lost_focus <= 300)
            return; // Done

        // If list is not open, open it
        if (!isDropped())
            drop(true);
        }
        });
    };

    /** {@inheritDoc} */
    @Override
    public void setForeground(final Color color) {
    text_color = color;
    text.setForeground(color);
    }

    /** {@inheritDoc} */
    @Override
    public void setToolTipText(final String tooltip) {
    tool_tip = tooltip;
    text.setToolTipText(tooltip);
    drop_down.setToolTipText(tooltip);
    }

    /**
     * Define items to be displayed in the list, and returned as the current
     * selection when selected.
     *
     * @param new_items
     *            Items to display in the list
     */
    public void setItems(final List<T> items) {
    List<T> oldValue = this.items;
    this.items = items;
    changeSupport.firePropertyChange("items", oldValue, this.items);
    }

    /**
     * Get the list of items
     *
     * @return list of selectable items
     */
    public List<T> getItems() {
    return this.items;
    }

    /**
     * Set items that should be selected.
     *
     * <p>
     * Selected items must be on the list of items provided via
     * <code>setItems</code>
     *
     * @param sel_items
     *            Items to select in the list
     */
    public void setSelection(final List<T> selection) {
    List<Integer> oldValue = this.selectionIndex;
    List<Integer> newSelectionIndex = new ArrayList<Integer>(
        selection.size());
    for (T t : selection) {
        int index = items.indexOf(t);
        if (index >= 0) {
            newSelectionIndex.add(items.indexOf(t));
        }
    }
    this.selectionIndex = newSelectionIndex;
    changeSupport.firePropertyChange("selection", oldValue,
        this.selectionIndex);
    }

    /**
     * set the items to be selected, the selection is specified as a string with
     * values separated by {@value MultipleSelectionCombo.SEPARATOR}
     *
     * @param selection_text
     *            Items to select in the list as comma-separated string
     */
    public void setSelection(final String selection) {
    setSelection("".equals(selection) ? new String[0] : selection
        .split(SEPERATOR_PATTERN));
    }

    /**
     * Set the items to be selected
     *
     * @param selections
     */
    public void setSelection(final String[] selections) {
    List<Integer> oldValue = this.selectionIndex;
    List<Integer> newSelectionIndex;
    if (selections.length > 0) {
        newSelectionIndex = new ArrayList<Integer>(selections.length);
        // Locate index for each item
        for (String item : selections) {
        int index = getIndex(item);
        if (index >= 0 && index < items.size()) {
            newSelectionIndex.add(getIndex(item));
            text.setForeground(text_color);
            text.setToolTipText(tool_tip);
        } else {
            text.setForeground(display.getSystemColor(SWT.COLOR_RED));
            text.setToolTipText("Text contains invalid items");
        }

        }
    } else {
        newSelectionIndex = Collections.emptyList();
    }
    this.selectionIndex = newSelectionIndex;
    changeSupport.firePropertyChange("selection", oldValue,
        this.selectionIndex);
    }

    /**
     * return the index of the object in items with the string representation
     * _string_
     *
     * @param string
     * @return
     */
    private Integer getIndex(String string) {
    for (T item : items) {
        if (stringRepresention(item).equals(string)) {
        return items.indexOf(item);
        }
    }
    return -1;
    }

    /**
     * get the list of items currently selected. Note: this does not return the
     * list in the order of selection.
     *
     * @return the list of selected items
     */
    public List<T> getSelection() {
    List<T> selection = new ArrayList<T>(this.selectionIndex.size());
    for (int index : this.selectionIndex) {
        selection.add(items.get(index));
    }
    return Collections.unmodifiableList(selection);
    }

    /** Update <code>selection</code> from <code>list</code> */
    private void updateSelectionFromList() {
        List<T> selection = new ArrayList<T>();
        if (itemButtons != null) {
            for (int n=0; n<itemButtons.length; n++)
                if (itemButtons[n].getSelection())
                    selection.add(items.get(n));
        }
        setSelection(selection);
    }

    /** Update <code>text</code> to reflect <code>selection</code> */
    private void updateText() {
    final StringBuilder buf = new StringBuilder();
    for (Integer index : selectionIndex) {
        if (buf.length() > 0)
        buf.append(SEPARATOR);
        buf.append(stringRepresention(items.get(index)));
    }
    text.setText(buf.toString());
    if (selectionIndex.size() == 0)
        text.setText("Select Items ...");
    text.setSelection(buf.length());
    }

    /** @return <code>true</code> if drop-down is visible */
    private boolean isDropped() {
    return popup != null;
    }

    /**
     * @param drop
     *            Display drop-down?
     */
    private void drop(boolean drop) {
    if (drop == isDropped())
        return;
    if (drop)
        createPopup();
    else
        hidePopup();
    }

    /** Create shell that simulates drop-down */
    private void createPopup() {
    popup = new Shell(getShell(), SWT.NO_TRIM);
    popup.setLayout(new GridLayout());
    popup.setToolTipText(tool_tip);

    SelectionListener selectionListener = new SelectionListener() {
        @Override
        public void widgetSelected(SelectionEvent e) {
        updateSelectionFromList();
        updateText();
        }
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        updateSelectionFromList();
        updateText();
        hidePopup();
        }};

    int itemWidth = 0;
    int itemHeight = 0;

    String[] stringItems = new String[items.size()];
    for (int i = 0; i < items.size(); i++) {
        stringItems[i] = stringRepresention(items.get(i));
    }

    int numItems = stringItems.length;
    itemButtons = new Button[numItems];
    for (int n=0; n<numItems; n++) {
        Button button = new Button(popup, SWT.CHECK);
        button.setText(stringItems[n]);
        if (this.selectionIndex.contains(n))
            button.setSelection(true);
        button.pack();
        itemButtons[n] = button;
        itemButtons[n].addSelectionListener(selectionListener);
        itemWidth = Math.max(itemWidth, button.getBounds().width);
        itemHeight = Math.max(itemHeight, button.getBounds().height + button.getBorderWidth()*2);
    }

    // Position popup under the text box
    Point p = text.getParent().toDisplay(text.getLocation());
    Point size = text.getSize();
    Rectangle shellRect = new Rectangle(p.x, p.y + size.y, size.x, 0);
    popup.setLocation(shellRect.x, shellRect.y);
    popup.setSize(popup.computeSize(SWT.DEFAULT,  SWT.DEFAULT, true));
    popup.addShellListener(new ShellAdapter() {
        @Override
        public void shellDeactivated(ShellEvent event) {
            if (popup != null && !popup.isDisposed())
                selectionIndex.clear();
                for (int i=0; i<itemButtons.length; i++) {
                    if (itemButtons[i].getSelection())
                        selectionIndex.add(i);
                }
                hidePopup();
        }
    });
    popup.open();

    }

    /** Hide popup shell */
    private void hidePopup() {
    if (popup != null) {
        popup.close();
        popup.dispose();
        popup = null;
    }
    text.setFocus();
    }

    /**
     * Register a PropertyChangeListener on this widget. the listener will be
     * notified when the items or the selection is changed.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * remove the PropertyChangeListner
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Override this method to define the how the object should be represented
     * as a string.
     *
     * @param object
     * @return the string representation of the object
     */
    public String stringRepresention(T object) {
    return object.toString();
    }
}
