/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.ui;

import org.csstudio.trends.sscan.Messages;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Label;

/** Button (Draw2d) for triggering start/end time configuration
 *  @author Kay Kasemir
 */
public class ConfigButton extends Button
{
    /** Listener to invoke on button presses */
    private PlotListener listener = null;

    /** Initialize */
    public ConfigButton()
    {
        Label text = new Label(Messages.StartEndDialogBtn);
        setContents(text);
        setToolTip(new Label(Messages.StartEndDialogTT));
        addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
               // if (listener != null)
                    //listener.ConfigRequested();
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
}
