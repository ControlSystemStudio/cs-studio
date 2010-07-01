/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.waveformview;

import org.csstudio.platform.ui.workbench.OpenViewAction;
import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;

/** Action to open the WaveformView
 *  @author Kay Kasemir
 */
public class OpenWaveformAction extends OpenViewAction
{
    public OpenWaveformAction()
    {
        super(WaveformView.ID,
              Messages.OpenWaveformView,
              Activator.getDefault().getImageDescriptor("icons/wavesample.gif")); //$NON-NLS-1$
    }
}
