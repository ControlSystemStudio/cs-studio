/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Combo-type widget that allows selecting multiple items.
 *  
 *  <p>Takes a list of {@link Object}s as input.
 *  
 *  <p>The <code>toString()</code> of each Object is displayed
 *  in a drop-down list.
 *  One or more items can be selected, they're also displayed
 *  in the text field.
 *  
 *  <p>Items can be entered in the text field, comma-separated.
 *  If entered text does not match a valid item,
 *  text is highlighted and tool-tip indicates error.
 *  
 *  <p>Keyboard support: 'Down' key in text field opens drop-down.
 *  Inside drop-down, single item can be selected via cursor key
 *  and 'RETURN' closes the drop-down.
 *  
 *  TODO Auto-completion while typing?
 *  
 *  @author Kay Kasemir
 */
public class MultiSelectionCombo<T> extends Composite
{
    final private static String SEPARATOR = ", "; //$NON-NLS-1$
    final private static String SEPERATOR_PATTERN = "\\s*,\\s*"; //$NON-NLS-1$

    private Display display;
    
    private Text text;

    /** Pushing the drop_down button opens the popup */
    private Button drop_down;
    
    private Shell popup;
    private org.eclipse.swt.widgets.List list;
    
    /** Items to show in list */
    private ArrayList<T> items = new ArrayList<>();
 
    /** Selection indices */
    private ArrayList<Integer> selection = new ArrayList<>();
    
    private String tool_tip = null;

    private Color text_color = null;
    
    /** When list looses focus, the event time is noted here.
     *  This prevents the drop-down button from re-opening
     *  the list right away.
     */
    private int lost_focus = 0;
   
    /** Initialize
     *  @param parent
     *  @param style
     */
    public MultiSelectionCombo(final Composite parent, final int style)
    {
        super(parent, style);
        createComponents(parent);
    }
    
    /** Create SWT components
     *  @param parent
     */
    private void createComponents(final Composite parent)
    {
        display = parent.getDisplay();
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        setLayout(layout);
        
        text = new Text(this, SWT.BORDER);
        GridData gd = new GridData(SWT.FILL, 0, true, false);
        text.setLayoutData(gd);
        text.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                // Analyze text, update selection
                final String items_text = text.getText();
                final boolean good_selection = setSelectionIndices(items_text);
                if (good_selection)
                {
                    text.setForeground(text_color);
                    text.setToolTipText(tool_tip);
                }
                else
                {
                    text.setForeground(display.getSystemColor(SWT.COLOR_RED));
                    text.setToolTipText("Text contains invalid items");
                }
            }
        });
        text.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(final KeyEvent e)
            {
                switch (e.keyCode)
                {
                case SWT.ARROW_DOWN:
                    drop(true);
                    return;
                case SWT.ARROW_UP:
                    drop(false);
                    return;
                }
            }
        });
        
        drop_down = new Button(this, SWT.ARROW | SWT.DOWN);
        gd = new GridData(SWT.FILL, SWT.FILL, false, false);
        gd.heightHint = text.getBounds().height;
        drop_down.setLayoutData(gd);
        drop_down.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                // Was list open, user clicked this button to close,
                // and list self-closed because is lost focus?
                if (e.time - lost_focus <= 300)
                    return; // Done
                
                // If list is not open, open it
                if (!isDropped())
                    drop(true);
            }
        });
    }
    
    /** {@inheritDoc} */
    @Override
    public void setForeground(final Color color)
    {
        text_color = color;
        text.setForeground(color);
    }
    
    /** {@inheritDoc} */
    @Override
    public void setToolTipText(final String tooltip)
    {
        tool_tip = tooltip;
        text.setToolTipText(tooltip);
        drop_down.setToolTipText(tooltip);
    }
    
    /** Define items to be displayed in the list,
     *  and returned as the current selection when selected.
     *  @param new_items Items to display in the list
     */
    public void setItems(final List<T> new_items)
    {
        items.clear();
        items.addAll(new_items);
        setSelection(Collections.<T>emptyList());
    }

    /** Define items that should be selected.
     *  
     *  <p>Selected items must be on the list of items
     *  provided via <code>setItems</code>
     *  
     *  @param selection_text Items to select in the list as comma-separated string
     *  @return <code>true</code> if all requested items could be selected,
     *          <code>false</code> if some are not in the list
     */
    public boolean setSelection(final String selection_text)
    {
        final boolean good_selection = setSelectionIndices(selection_text);
        updateText();
        return good_selection;
    }

    /** Define items that should be selected.
     *  
     *  <p>Selected items must be on the list of items
     *  provided via <code>setItems</code>
     *  
     *  @param sel_items Items to select in the list
     *  @return <code>true</code> if all requested items could be selected,
     *          <code>false</code> if some are not in the list
     */
    public boolean setSelection(final List<T> sel_items)
    {
        final boolean good_selection = setSelectionIndices(sel_items);
        updateText();
        return good_selection;
    }
    
    /** Obtain currently selected items
     *  
     *  <p>The order of selected items is not guaranteed.
     *  In the current implementation, it will match
     *  the order of items provided to <code>setItems()</code>.
     *  It does <u>not</u> reflect the order in which items
     *  were selected inside the GUI's drop-down list.
     *  That may change in the future, so treat the
     *  result as a <code>Collection</code> rather than
     *  ordered <code>List</code>.
     *  
     *  @return Currently selected items
     */
    public List<T> getSelection()
    {
        final List<T> result = new ArrayList<>(selection.size());
        for (int i : selection)
            result.add(items.get(i));
        return result;
    }

    /** Set indices of <code>selection</code> based on desired selection.
     *  @param selection_text Items to select in the list as comma-separated string
     *  @return <code>true</code> if all requested items could be selected,
     *          <code>false</code> if some are not in the list
     */
    private boolean setSelectionIndices(final String selection_text)
    {
        final String[] item_texts = selection_text.split(SEPERATOR_PATTERN);
        boolean good_selection = true;
        selection.clear();
        // Locate index for each item
        for (String text : item_texts)
        {
            final int index = findItemIndex(text);
            if (index >= 0)
                selection.add(index);
            else
                good_selection = false;
        }
        return good_selection;
    }

    /** Set indices of <code>selection</code> based on desired selection.
     *  @param sel_items Items to select in the list
     *  @return <code>true</code> if all requested items could be selected,
     *          <code>false</code> if some are not in the list
     */
    private boolean setSelectionIndices(final List<T> sel_items)
    {
        boolean good_selection = true;
        selection.clear();
        // Locate index for each item
        for (T item : sel_items)
        {
            final int index = findItemIndex(item.toString());
            if (index >= 0)
                selection.add(index);
            else
                good_selection = false;
        }
        return good_selection;
    }
    
    /** @param item Item for which to locate the index in <code>items</code>
     *  @return Index 0, ... within <code>items</code>, or -1 if not found
     */
    private int findItemIndex(final String item_text)
    {
        for (int i=0; i<items.size(); ++i)
            if (items.get(i).toString().equals(item_text))
                return i;
        return -1;
    }

    /** Update <code>selection</code> from <code>list</code> */
    private void updateSelectionFromList()
    {
        selection.clear();
        for (int i : list.getSelectionIndices())
            selection.add(i);
    }
    
    /** Update <code>text</code> to reflect <code>selection</code> */
    private void updateText()
    {
        final StringBuilder buf = new StringBuilder();
        for (int i : selection)
        {
            if (buf.length() > 0)
                buf.append(SEPARATOR);
            buf.append(items.get(i));
        }
        text.setText(buf.toString());
    }
    
    /** @return <code>true</code> if drop-down is visible */
    private boolean isDropped()
    {
        return popup != null;
    }

    /** @param drop Display drop-down? */
    private void drop(boolean drop)
    {
        if (drop == isDropped())
            return;
        
        if (drop)
            createPopup();
        else
            hidePopup();
    }

    /** Create shell that simulates drop-down */
    private void createPopup()
    {
        popup = new Shell (getShell (), SWT.NO_TRIM | SWT.ON_TOP);
        popup.setLayout(new FillLayout());
        list = new org.eclipse.swt.widgets.List (popup, SWT.MULTI | SWT.V_SCROLL);
        list.setToolTipText(tool_tip);
                
        // Position popup under the text box
        Rectangle bounds = text.getBounds();
        bounds.y += bounds.height;
        // As high as necessary for items
        bounds.height = 5+2*list.getBorderWidth() + list.getItemHeight() * items.size();
        // ..with limitation
        bounds.height = Math.min(bounds.height, display.getBounds().height / 2);
        // Map to screen coordinates
        bounds = display.map(text, null, bounds);
        popup.setBounds(bounds);
        popup.open();
        
        // Update text from changed list selection
        list.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateSelectionFromList();
                updateText();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                updateSelectionFromList();
                updateText();
                hidePopup();
            }
        });
        
        setListItemTexts();
        setListItemSelection();
        
        // Hide popup when loosing focus
        list.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(final FocusEvent e)
            {
                lost_focus = e.time;
                hidePopup();
            }
        });
        list.setFocus();
    }

    /** Hide popup shell */
    private void hidePopup()
    {
        if (popup != null)
        {
            popup.close();
            popup.dispose();
            popup = null;
        }
        text.setFocus();
    }

    /** Show items' text in List */
    private void setListItemTexts()
    {
        final String texts[] = new String[items.size()];
        for (int i=0; i<texts.length; ++i)
            texts[i] = items.get(i).toString();
        list.setItems(texts);
    }

    /** Select appropriate items in list */
    private void setListItemSelection()
    {
        final int selected[] = new int[selection.size()];
        for (int i=0; i<selected.length; ++i)
            selected[i] = selection.get(i);
        list.setSelection(selected);
    }
}
