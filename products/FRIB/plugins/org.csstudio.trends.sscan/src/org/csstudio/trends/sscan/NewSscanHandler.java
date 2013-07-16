/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan;

import org.csstudio.trends.sscan.editor.SscanEditor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/** Handler connected to workbench menu for opening a new editor.
 *  @author Eric Berryman
 */
public class NewSscanHandler extends AbstractHandler
{
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        
        try
        {
        	Perspective.showPerspective();
        }
        catch (Exception ex)
        {
        	// never mind
        }
        SscanEditor.createInstance();
        return null;
    }
}
