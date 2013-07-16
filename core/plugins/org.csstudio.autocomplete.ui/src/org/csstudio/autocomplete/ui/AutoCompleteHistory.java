/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui;

import java.util.LinkedList;

import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class AutoCompleteHistory {

	private static final int MAX_HISTORY_SIZE = 100;

	private final Control control;
	private final String type;
	private final IControlContentAdapter controlContentAdapter;

	public AutoCompleteHistory(Control control, String type,
			IControlContentAdapter adapter) {
		this.control = control;
		this.type = type;
		this.controlContentAdapter = adapter;

		installListener(control);
	}
	
	public void installListener(final Control control) {
		if (control == null || control.isDisposed())
			return;
		if (control instanceof Combo) {
			((Combo) control).addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					handleSelection();
				}
			});
		} else if (control instanceof Button) {
			((Button) control).addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					handleSelection();
				}
			});
		} else {
			control.addListener(SWT.DefaultSelection, new Listener() {
				public void handleEvent(Event e) {
					handleSelection();
				}
			});
		}
	}

	private void handleSelection() {
		String new_entry = controlContentAdapter.getControlContents(control);
		addEntry(new_entry);
	}

	public synchronized void addEntry(final String newEntry) {
		// Avoid empty entries
		if (newEntry == null || newEntry.trim().isEmpty())
			return;
		LinkedList<String> fifo = Activator.getDefault().getHistory(type);
		if (fifo == null)
			return;

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
