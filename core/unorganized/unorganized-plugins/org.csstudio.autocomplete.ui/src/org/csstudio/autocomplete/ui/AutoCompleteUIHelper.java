/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui;

import java.util.List;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Auto complete Widget helper to manage with special field editor.
 */
public class AutoCompleteUIHelper {

	public static TextCellEditor createAutoCompleteTextCellEditor(
			Composite parent, String type) {
		return new AutoCompleteTextCellEditor(parent, type);
	}

	public static TextCellEditor createAutoCompleteTextCellEditor(
			Composite parent, String type, List<Control> historyHandlers) {
		return new AutoCompleteTextCellEditor(parent, type, historyHandlers);
	}

	public static void handleSelectEvent(final Control control,
			final AutoCompleteWidget autocompleteWidget) {
		autocompleteWidget.getHistory().installListener(control);
	}

}
