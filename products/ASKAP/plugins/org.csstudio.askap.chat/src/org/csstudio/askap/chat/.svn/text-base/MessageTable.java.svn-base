/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.askap.chat;

import java.util.Date;

import org.csstudio.askap.utility.AskapHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/** Widget that displays chat messages
 *  @author Kay Kasemir
 */
public class MessageTable extends Composite
{
	final private StyleRange error_style = new StyleRange();
	final private StyleRange from_style = new StyleRange();
	final private StyleRange self_style = new StyleRange();
	final private StyledText messages;

	public MessageTable(final Composite parent, final int style)
    {
	    super(parent, style);

	    final Display display = parent.getDisplay();
        error_style.background = display.getSystemColor(SWT.COLOR_RED);
        from_style.foreground = display.getSystemColor(SWT.COLOR_DARK_GREEN);
        self_style.foreground = display.getSystemColor(SWT.COLOR_BLUE);

        setLayout(new FillLayout());
        messages = new StyledText(this, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
    }
	
	/** Clear message display, removing all messages */
	public void clear()
	{
		messages.setStyleRange(null);
    	messages.setText(""); //$NON-NLS-1$
	}
	
    /** Add a message to the display
     *  @param from
     *  @param is_self
     *  @param text
     */
    public void addMessage(final String from, final boolean is_self, final String text, final long timeStamp)
    {
    	String realFrom = is_self ? "me" : from;
    	
    	realFrom = realFrom + "(" + AskapHelper.getFormatedData(new Date(timeStamp), null) + ")";
    	
		// Style the 'from' section
		final StyleRange style = is_self ? self_style : from_style;
		final int orig_length = messages.getText().length();
		style.start = orig_length;
		style.length = realFrom.length() + 2;
		messages.append(realFrom + ": "); //$NON-NLS-1$
		messages.setStyleRange(style);
		
		messages.append(text + "\n"); //$NON-NLS-1$
		
		// Scroll to location of the newly added text
		messages.setSelection(orig_length);
    }

    /** Display error
     *  @param error Error text
     */
    public void showError(final String error)
    {
    	messages.setStyleRange(null);
    	messages.setText(error);
    	error_style.start = 0;
    	error_style.length = error.length();
    	messages.setStyleRange(error_style);
    }
}
