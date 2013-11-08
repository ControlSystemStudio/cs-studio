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
package org.csstudio.scan.ui.scanmonitor;

import java.util.List;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.server.ScanInfo;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** Content provider for input of type {@link ScanInfoModel},
 *  elements are of type {@link ScanInfo}
 *
 *  @author Kay Kasemir
 */
public class ScanInfoModelContentProvider implements IStructuredContentProvider
{
    private ScanInfoModel model;

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
    {
        model = (ScanInfoModel) newInput;
    }

    @Override
    public void dispose()
    {
        model = null;
    }

    @Override
    public Object[] getElements(final Object inputElement)
    {
        if (model == null)
            return new ScanInfo[0];
        final List<ScanInfo> infos = model.getInfos();
        return infos.toArray(new ScanInfo[infos.size()]);
    }
}
