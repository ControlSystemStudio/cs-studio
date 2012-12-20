/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartSite;

/** RCP View for an individual chat
 * 
 *  <p>The View is also the 'Controller'
 *  for an {@link IndividualChat} model and
 *  {@link IndividualChatGUI} display.
 *  
 *  @author Kay Kasemir
 */
public class IndividualChatView extends org.eclipse.ui.part.ViewPart
	implements IndividualChatListener, IndividualChatGUIListener
{
	/** View ID defined in plugin.xml */
	final public static String ID = "org.csstudio.utility.chat.individual"; //$NON-NLS-1$
	
	private Display display;
	private IndividualChat chat = null;
	private IndividualChatGUI gui;

	/** {@inheritDoc} */
	@Override
	public void createPartControl(final Composite parent)
	{
		display = parent.getDisplay();
		gui = new IndividualChatGUI(parent, this);
		
        parent.addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				if (chat != null)
				{
					chat.disconnect();
					chat = null;
				}
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public void setFocus()
	{
		gui.setFocus();
	}

	/** Connect the view to a chat
	 *  @param from Person who invited us
	 *  @param chat {@link IndividualChat} to handle in view
	 */
	public void setChat(String from, final IndividualChat chat)
	{
		if (this.chat != null)
			throw new Error("Already initialized"); //$NON-NLS-1$
		setPartName(NLS.bind(Messages.IndividualChatTitleFmt, from));
		this.chat = chat;
		chat.addListener(this);
		setFocus();
	}
	
	/** {@inheritDoc} */
    @Override
    public void doSend(final String message_text)
	{
		if (chat == null)
			return;
		try
		{
			chat.send(message_text);
		}
		catch (Exception ex)
		{
        	final String error = 
    			NLS.bind(Messages.SendErrorFmt,
   					 ex.getMessage());
        	gui.showError(error);
		}
	}
		
	/** {@inheritDoc} */
	@Override
    public void receive(final String from, final boolean is_self, final String text)
    {
		display.asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				gui.addMessage(from, is_self, text);
			}
		});
    }

	/** @return {@link IndividualChatGUI} handled by this view */
	public IndividualChatGUI getGUI()
    {
	    return gui;
    }

	/** Close the view */
	public void close()
    {
		final IWorkbenchPartSite site = getSite();
		if (site != null)
			site.getPage().hideView(this);
    }
}
