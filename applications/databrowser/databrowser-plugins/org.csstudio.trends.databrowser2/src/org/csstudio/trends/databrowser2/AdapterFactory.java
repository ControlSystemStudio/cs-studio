/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.trends.databrowser2.model.ChannelInfo;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.PlotSamples;
import org.diirt.vtype.VType;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapt Data Browser model items to CSS PV
 *
 *  <p>plugin.xml registers this for model.PVItem and archive.ChannelInfo
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AdapterFactory implements IAdapterFactory
{
    @Override
    public Class<?>[] getAdapterList()
    {
        return new Class<?>[] { String.class, ProcessVariable.class, ProcessVariableWithSamples.class };
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType)
    {
        if (adaptableObject instanceof ModelItem)
        {
            final ModelItem item = (ModelItem) adaptableObject;
            if (adapterType == String.class)
                return item.getName();
            else if (adapterType == ProcessVariable.class)
                return new ProcessVariable(item.getName());
            else if (adapterType == ProcessVariableWithSamples.class)
                return convertToPvWithSamples(item);
        }
        else if (adaptableObject instanceof ChannelInfo)
        {
            final ChannelInfo item = (ChannelInfo) adaptableObject;
            if (adapterType == String.class)
                return item.getProcessVariable().getName();
            else if (adapterType == ProcessVariable.class)
                return item.getProcessVariable();
        }
        return null;
    }

    /** Create copy of data array for use outside of the data browser
     *  @param item {@link ModelItem}
     *  @return {@link ProcessVariableWithSamples} for the model item
     */
    private ProcessVariableWithSamples convertToPvWithSamples(final ModelItem item)
    {
        final PlotSamples plot_samples = item.getSamples();

        try
        {
            if (! plot_samples.getLock().tryLock(10, TimeUnit.SECONDS))
                throw new TimeoutException("Cannot lock data for " + item + ": " + plot_samples);
            try
            {
                final int size = plot_samples.size();
                final VType[] samples = new VType[size];
                for (int i=0; i<size; ++i)
                    samples[i] = plot_samples.get(i).getVType();
                return new ProcessVariableWithSamples(new ProcessVariable(item.getName()), samples);
            }
            finally
            {
                plot_samples.getLock().unlock();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
}
