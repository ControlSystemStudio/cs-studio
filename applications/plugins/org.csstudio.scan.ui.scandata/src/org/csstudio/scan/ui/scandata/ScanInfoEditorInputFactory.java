/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scandata;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/** Factory for creating ScanInfoEditorInput from saved information
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanInfoEditorInputFactory implements IElementFactory
{
	/** Factory ID registered in plugin.xml */
    final public static String ID = "org.csstudio.scan.ui.scandata.inputfactory";

	@Override
    public IAdaptable createElement(final IMemento memento)
    {
		final long id = Long.valueOf(memento.getString(ScanInfoEditorInput.TAG_ID));
		final String name = memento.getString(ScanInfoEditorInput.TAG_NAME);
	    return new ScanInfoEditorInput(id, name);
    }
}
