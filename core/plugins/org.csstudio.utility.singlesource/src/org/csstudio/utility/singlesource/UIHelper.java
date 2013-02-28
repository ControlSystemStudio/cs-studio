/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.singlesource;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/** Helper for accessing UI.
 * 
 *  <p>This implementation provides the common support.
 *  Derived classes can add support that is specific to RCP or RAP.
 *  
 *  <p>Client code should obtain a {@link UIHelper} via the {@link SingleSourcePlugin}
 *  
 *  @author Kay Kasemir
 *  @author Xihui Chen - Similar code in BOY/WebOPI
 */
@SuppressWarnings("nls")
public class UIHelper
{
    /** Supported User Interface Toolkits */
    public enum UI
    {
        /** Rich Client Platform: SWT */
        RCP,
        
        /** Remote Application Platform: RWT */
        RAP
    };
    
    final private UI ui;

    /** Initialize */ 
    public UIHelper()
    {
        if (SWT.getPlatform().startsWith("rap"))
            ui = UI.RAP;
        else
            ui = UI.RCP;
    }

    /** @return {@link UI} */
    public UI getUI()
    {
        return ui;
    }
    
    /** Prompt for file name to save data
     * 
     *  @param shell Parent shell
     *  @param original Original file name, may be <code>null</code>
     *  @param extension Extension to enforce, without ".". May be <code>null</code>
     *  @return
     */
    public IPath openSaveDialog(final Shell shell, final IPath original, final String extension)
    {
        return null;
    }
}
