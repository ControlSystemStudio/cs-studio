/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.ui;

import org.csstudio.swt.xygraph.undo.IUndoableCommand;
import org.csstudio.swt.xygraph.undo.OperationsManager;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToggleButton;

/** Button (Draw2d) for turning scrolling off/on
 *  @author Kay Kasemir
 */
public class ScrollButton extends ToggleButton
{
    private static final String OFF_ICON = "icons/scroll_off.gif"; //$NON-NLS-1$
    private static final String ON_ICON = "icons/scroll_on.gif"; //$NON-NLS-1$

    /** Label that shows one of the ICONs */
    private Label icon;

    /** Listener to invoke on button presses */
    private PlotListener listener = null;

    /** Used to remember the button state */
    private boolean scroll_on;

    /** Button creates this undoable command when clicked,
     *  then executes it ('redo'). Since it's on the operations stack,
     *  it can later be undone or re-done.
     * @author Kay Kasemir
     */
    class ScrollCommand implements IUndoableCommand
    {
        final private boolean new_scroll_state;

        ScrollCommand(final boolean new_scroll_state)
        {
            this.new_scroll_state = new_scroll_state;
        }

        @Override
        public void redo()
        {
            setScrollState(new_scroll_state);
        }

        @Override
        public void undo()
        {
            setScrollState(!new_scroll_state);
        }

        @Override
        public String toString()
        {
            return Messages.ScrollButtonTT;
        }

        private void setScrollState(final boolean scroll_on)
        {
            setButtonState(scroll_on);
            // Does anybody care?
            if (listener != null)
                listener.scrollRequested(scroll_on);
        }
    }

    /** Initialize
     *  @param operations_manager Used for undo/redo of scroll on/off
     */
    public ScrollButton(final OperationsManager operations_manager)
    {
        icon = new Label(Activator.getDefault().getImage(ON_ICON));
        scroll_on = true;
        setContents(icon);
        setToolTip(new Label(Messages.ScrollButtonTT));
        addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                final IUndoableCommand command = new ScrollCommand(!isSelected());
                operations_manager.addCommand(command);
                command.redo();
            }
        });
    }

    /** Add a listener that will be informed about scroll on/off requests */
    public void addPlotListener(final PlotListener listener)
    {
        if (this.listener != null)
            throw new IllegalStateException();
        this.listener = listener;
    }

    /** Update scroll button to reflect the desired scroll mode
     *  @param on <code>true</code> when scrolling is 'on'
     */
    public void setButtonState(final boolean scroll_on)
    {
        if (this.scroll_on == scroll_on)
            return;
        // Update button image
        if (scroll_on)
            icon.setIcon(Activator.getDefault().getImage(ON_ICON));
        else
            icon.setIcon(Activator.getDefault().getImage(OFF_ICON));
        this.scroll_on = scroll_on;
        // When invoked from undo/redo and not as result of user 'click',
        // need to update button selection state
        if (isSelected() == scroll_on)
            setSelected(!scroll_on);
    }
}
