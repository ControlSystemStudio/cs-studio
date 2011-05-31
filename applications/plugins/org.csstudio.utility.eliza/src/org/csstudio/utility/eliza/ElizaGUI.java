/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.eliza;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Eliza in Java. Adapted from a BASIC program I found floating on the net.
 * Eliza was originally written by Joseph Weizenbaum. This version is an
 * adaption of the program as it appeared in the memorable magazine Create
 * Computing around 1981. <br>
 * @author Jesper Juul - jj@pobox.com. Copenhagen, February 24th, 1999.
 * @author Kay Kasemir - Adapted to SWT
 */
public class ElizaGUI
{
    final private Text messages;
    final private Text textentry;

    final private ElizaParse eliza = new ElizaParse();

    public ElizaGUI(final Composite parent)
    {
        final GridLayout layout = new GridLayout();
        parent.setLayout(layout);

        messages = new Text(parent,
                SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        messages.setLayoutData(gd);
        
        textentry = new Text(parent, SWT.BORDER);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        textentry.setLayoutData(gd);
        textentry.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                handleLine(textentry.getText());
                textentry.setText(""); //$NON-NLS-1$
            }
        });
        textentry.setText("<Enter your sorrows here>"); //$NON-NLS-1$
        textentry.selectAll();
        
        addText(eliza.getIntroMsg());
        printAccumulatedMessages();
    }

    private void handleLine(String s)
    {
        if (s == null)
            return;
        s = s.trim();
        if (s.length() < 1)
            return;
        addText(" >" + s); //$NON-NLS-1$
        eliza.handleLine(s);
        printAccumulatedMessages();
    }

    private void printAccumulatedMessages()
    {
        while (eliza.msg.size() > 0)
        {
            addText((String) eliza.msg.elementAt(0));
            eliza.msg.removeElementAt(0);
        }
    }

    private void addText(final String s[])
    {
        for (String line : s)
            addText(line);
    }

    private void addText(final String s)
    {
        messages.append(s + "\n"); //$NON-NLS-1$
    }

    public void setFocus()
    {
        textentry.setFocus();
    }
}
