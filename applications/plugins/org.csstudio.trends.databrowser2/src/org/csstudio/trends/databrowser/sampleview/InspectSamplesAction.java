/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.sampleview;

import org.csstudio.platform.ui.workbench.OpenViewAction;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;

/** Action to open the SampleView
 *  @author Kay Kasemir
 */
public class InspectSamplesAction extends OpenViewAction
{
    public InspectSamplesAction()
    {
        super(SampleView.ID,
              Messages.InspectSamples,
              Activator.getDefault().getImageDescriptor("icons/inspect.gif")); //$NON-NLS-1$
    }
}
