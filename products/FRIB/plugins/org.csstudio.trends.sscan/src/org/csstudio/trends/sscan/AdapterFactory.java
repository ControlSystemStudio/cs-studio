/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan;

import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.data.values.IValue;
import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.trends.sscan.model.ChannelInfo;
import org.csstudio.trends.sscan.model.ModelItem;
import org.eclipse.core.runtime.IAdapterFactory;

/** Adapt Sscan model items to CSS PV
 *
 *  <p>plugin.xml registers this for model.ModelItem
 *
 *  @author Kay Kasemir
 */
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
            else if (adapterType == ProcessVariable.class){
            	String detectorNumber = item.getDetector().getDetectorIndex().toString();
                return new ProcessVariable(item.getName()+".D0"+detectorNumber+"DA");
            }
            else if (adapterType == ProcessVariableWithSamples.class)
                return new ProcessVariable[item.getLiveSamples().getSize()];
            else if (adapterType == ProcessVariable[].class){
            	return new ProcessVariable[item.getLiveSamples().getSize()];
            }
				
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
        final IDataProvider  plot_samples = (IDataProvider ) item.getLiveSamples();
        final IValue[] samples;
        synchronized (plot_samples)
        {
            final int size = plot_samples.getSize();
            samples = new IValue[size];
            //for (int i=0; i<size; ++i)
            	//TODO: fix this IValue mismatch 
                //samples[i] = plot_samples.getSample(i).getYValue();
        }
        return new ProcessVariableWithSamples(new ProcessVariable(item.getName()), samples);
    }
}
