/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/** Base for action that initiates sending something
 *  to a {@link Person}
 *  @author Kay Kasemir
 */
abstract public class SendToPersonAction extends Action
{
	final private ISelectionProvider provider;

	/** Initialize
	 *  @param gui {@link GroupChatGUI}, to prevent sending stuff to ourself 
	 *  @param label Action label
	 *  @param icon_path Path to icon
	 *  @param provider Provider for {@link Person} selections
	 */
	public SendToPersonAction(final GroupChatGUI gui,
			final String label,
			final String icon_path,
			final ISelectionProvider provider)
	{
		super(label, Activator.getImage(icon_path));
		this.provider = provider;
		provider.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(final SelectionChangedEvent event)
			{
				boolean self = false;
				final IStructuredSelection selection =
					(IStructuredSelection) event.getSelection();
				if (selection != null)
				{
					final Person person = (Person)selection.getFirstElement();
					self = gui.isOurself(person);
				}
				setEnabled(! self);
			}
		});
	}
	
	@Override
    public void run()
    {
		final IStructuredSelection selection =
			(IStructuredSelection)provider.getSelection();
		final Person person = (Person)selection.getFirstElement();
		if (person != null)
			doSendToPerson(person);
    }

	/** To be implemented by derived class:
	 *  Send something to given person
	 *  @param person The receiver
	 */
	abstract protected void doSendToPerson(Person person);
}
