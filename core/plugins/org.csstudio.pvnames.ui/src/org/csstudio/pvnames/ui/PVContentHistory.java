/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames.ui;

import java.util.LinkedList;

import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;

public class PVContentHistory {
	
	private static final int MAX_HISTORY_SIZE = 100;
	
	private final Control control;
	private final IControlContentAdapter controlContentAdapter;
	
	
	public PVContentHistory(Control control, IControlContentAdapter adapter) {
		this.control = control;
		this.controlContentAdapter = adapter;

		control.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String new_entry = PVContentHistory.this.controlContentAdapter
						.getControlContents(PVContentHistory.this.control);
				addEntry(new_entry);
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
			}
		});
		// TODO : handle <enter> key !
	}
	
	public synchronized void addEntry(final String newEntry) {
		// Avoid empty entries
		if (newEntry.trim().isEmpty())
			return;
		LinkedList<String> fifo = Activator.getDefault().getHistory();
		
		// Remove if present, so that is re-added on top
		int index = -1;
		while ((index = fifo.indexOf(newEntry)) >= 0)
			fifo.remove(index);

		// Maybe remove oldest, i.e. bottom-most, entry
		if (fifo.size() >= MAX_HISTORY_SIZE)
			fifo.removeLast();

		// Add at the top
		fifo.addFirst(newEntry);
	}
	
}
