package org.csstudio.utility.chat;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ChatGUIDemo
{
	public static void main(final String[] args) throws Exception
    {
	    final Display display = new Display();
	    final Shell parent = new Shell(display);
	    
	    new ChatGUI(parent).setFocus();
        
        parent.setSize(600, 400);
        parent.open();
        while (!parent.isDisposed())
        {
	        if (!display.readAndDispatch()) display.sleep();
        }
        
        display.dispose();
    }
}
