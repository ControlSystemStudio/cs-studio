/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

/** Action for 'View' toolbar that uses a drop-down menu.
 *
 *  <p>To use, the current list of strings needs to
 *  be provided and a handler.
 *
 *  <p>This helper implements the required {@link IMenuCreator}.
 *  It also handles clicking on the basic 'button' area as opposed
 *  to the default action behavior which only opens the drop-down
 *  when clicking the tiny triangle.
 *
 *  @author Kay Kasemir
 */
public abstract class DropdownToolbarAction extends Action implements IMenuCreator
{
    private Menu menu;
    private String selection = null;

    /** Initialize
     *
     *  <p>The provided label is the initial label of the action.
     *  It can later be updated via <code>setText(String)</code>.
     *
     *  @param label Label to show in the drop-down 'button'
     *  @param tooltip Tool tip text
     */
    public DropdownToolbarAction(final String label, final String tooltip)
    {
        super(label, AS_DROP_DOWN_MENU);
        setToolTipText(tooltip);
        setMenuCreator(this);
    }

    /** @return Options to show, i.e. menu entries to create */
    abstract public String[] getOptions();

    /** @return Currently selected option */
    public String getSelection()
    {
        return selection;
    }

    /** @param selection Set selected option */
    public void setSelection(final String selection)
    {
        this.selection = selection;
    }

    /** Invoked when user selects an entry from the drop-down
     *  @param option Text of the entry
     */
    abstract public void handleSelection(String option);

    /** Invoked when user selects the action (clicks on 'main' section of the button)
     *  {@inheritDoc}
     */
    @Override
    public void runWithEvent(final Event event)
    {
        // User clicked on the action's main section, not the small drop-down indicator.
        // Execute drop-down behavior anyway, based on code copied from
        // Eclipse 3.6.1 ActionContributionItem.ActionContributionItem.handleWidgetSelection
        final ToolItem item = (ToolItem) event.widget;
        final Rectangle bounds = item.getBounds();
        final Menu menu = getMenu(item.getParent());
        final Point point = item.getParent().toDisplay(bounds.x, bounds.height);
        menu.setLocation(point.x, point.y);
        menu.setVisible(true);
    }

    /** Dispose menu (if there was one) */
    private void disposeMenu()
    {
        if (menu == null)
            return;
        menu.dispose();
        menu = null;
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        disposeMenu();
    }

    /** {@inheritDoc} */
    @Override
    public Menu getMenu(final Control parent)
    {
        disposeMenu();
        menu = new Menu(parent);
        return getMenu(menu);
    }

    /** {@inheritDoc} */
    @Override
    public Menu getMenu(final Menu menu)
    {
        final String[] options = getOptions();
        // GUI shows menu even when the action is disabled.
        // Hack around this by showing an empty menu
        if (isEnabled())
            for (int i=0; i<options.length; ++i)
            {
                final String option = options[i];
                final MenuItem item = new MenuItem(menu, SWT.RADIO);
                item.setText(option);
                if (option.equals(selection))
                    item.setSelection(true);
                item.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(final SelectionEvent e)
                    {   // Only react to the selection, not de-selection of
                        // the 'radio' buttons.
                        if (item.getSelection())
                        {
                            selection = option;
                            handleSelection(option);
                        }
                    }
                });
            }
        return menu;
    }
}
