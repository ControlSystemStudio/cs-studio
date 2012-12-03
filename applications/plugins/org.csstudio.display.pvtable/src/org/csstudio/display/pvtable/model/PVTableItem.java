/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;

import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.data.ExpressionLanguage.*;
import org.epics.pvmanager.data.VType;

import static org.epics.util.time.TimeDuration.ofSeconds;

/** One item (row) in the PV table.
 *
 *  @author Kay Kasemir
 */
public class PVTableItem
{
	final public static double DEFAULT_TOLERANCE = 0.001;

	final private String name;
	
	final private PVTableItemListener listener;
	
	// TODO boolean selected;
	
	private volatile VType value = null;

	private volatile VType saved = null;
	
	private double tolerance;
	
	final private PVReader<VType> pv;

	public PVTableItem(final String name, final double tolerance, final PVTableItemListener listener)
	{
		this.name = name;
		this.listener = listener;
		this.tolerance = tolerance;
		PVReaderListener<VType> pv_listener = new PVReaderListener<VType>()
		{
			@Override
			public void pvChanged(final PVReaderEvent<VType> event)
			{
				final PVReader<VType> pv = event.getPvReader();
				
				final Exception error = pv.lastException();
				if (error != null)
				{
					Logger.getLogger(PVTableItem.class.getName()).log(Level.WARNING, "Error from " + name, error);
					updateValue(null);
					return;
				}
				updateValue(pv.getValue());
			}
		};
		pv = PVManager.read(latestValueOf(vType(name))).readListener(pv_listener).timeout(ofSeconds(30.0)).maxRate(ofSeconds(1.0));
	}

	/** @return Returns the name of the 'main' PV. */
    public String getName()
    {
    	return name;
    }

    /** @param new_value New value of item */
    protected void updateValue(final VType new_value)
    {
    	value = new_value;
    	listener.tableItemChanged(this);
    }
    
    /** @return Value */
    public VType getValue()
    {
    	return value;
    }
        
    /** Save current value as saved value */
    public void save()
    {
    	saved = value;
    }

    /** @return Returns the saved_value. */
    public VType getSavedValue()
    {
    	return saved;
    }

    /** @return Tolerance for comparing saved and current value */
    public double getTolerance()
    {
		return tolerance;
	}

    /** @param tolerance Tolerance for comparing saved and current value */
	public void setTolerance(final double tolerance)
	{
		this.tolerance = tolerance;
		listener.tableItemChanged(this);
	}

	/** @return <code>true</code> if value has changed from saved value */
    public boolean hasChanged()
    {
    	final VType saved_value = saved;
    	if (saved_value == null)
    		return false;
    	return ! VTypeHelper.equalValue(value, saved_value, tolerance);
    }
    
    /** Must be called to release resources when item no longer in use */
    public void dispose()
    {
    	pv.close();
    }
    
    @Override
    public String toString()
    {
    	final StringBuilder buf = new StringBuilder();
    	buf.append(name).append(" = ").append(VTypeHelper.toString(value));
    	if (saved != null)
    	{
    		if (hasChanged())
    			buf.append(" ( != ");
    		else
    			buf.append(" ( == ");
    		buf.append(VTypeHelper.toString(saved)).append(" +- ").append(tolerance).append(")");
    	}
    	return buf.toString();
    }
}
