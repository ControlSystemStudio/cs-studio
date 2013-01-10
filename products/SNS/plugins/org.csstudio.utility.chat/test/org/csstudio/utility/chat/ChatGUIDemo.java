/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Standalone demo of the Chat view
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChatGUIDemo
{
	public static void main(final String[] args) throws Exception
    {
	    final Display display = new Display();
	    final Shell shell = new Shell(display);
	    
	    shell.setImage(new Image(display, "icons/group.png"));

	    GroupChatView view = new GroupChatView()
	    {
			@Override
            protected IndividualChatView createIndividualChatView()
            {
	    	    final Shell shell = new Shell(display);
	    	    shell.setImage(new Image(display, "icons/person.png"));
	    	    
	    	    final IndividualChatView view = new IndividualChatView();
	    	    view.createPartControl(shell);
	    	    shell.setSize(400, 300);
	    	    shell.setVisible(true);
	            return view;
            }
	    };
	    view.createPartControl(shell);
	    view.setFocus();
        
        shell.setSize(600, 400);
        shell.open();
        while (!shell.isDisposed())
        {
	        if (!display.readAndDispatch()) display.sleep();
        }
        
        display.dispose();
    }
}
