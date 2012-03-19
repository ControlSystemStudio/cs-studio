/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.ui.scanmonitor.actions;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.ui.scanmonitor.Activator;
import org.csstudio.scan.ui.scanmonitor.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/** Action that shows devices of a scan
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ShowDevicesAction extends AbstractGUIAction
{
    /** Initialize
     *  @param shell Parent shell
     *  @param model
     *  @param info
     */
    public ShowDevicesAction(final Shell shell, final ScanInfoModel model, final ScanInfo info)
    {
        super(shell, model, info, Messages.ShowDevices, Activator.getImageDescriptior("icons/information.gif"));
    }

    /** {@inheritDoc} */
    @Override
    protected void runModelAction() throws Exception
    {
        final DeviceInfo[] devices = model.getServer().getDeviceInfos(info.getId());
        final StringBuilder buf = new StringBuilder();
        buf.append("Devices:\n");
        for (DeviceInfo info : devices)
            buf.append(info).append("\n");

        MessageDialog.openInformation(shell, Messages.InfoTitle,
                buf.toString());
    }
}
