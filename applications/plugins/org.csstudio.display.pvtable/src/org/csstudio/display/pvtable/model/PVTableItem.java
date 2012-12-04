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

import org.epics.pvmanager.PV;
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
public class PVTableItem implements PVReaderListener<VType>
{
	final public static double DEFAULT_TOLERANCE = 0.001;

	final private String name;
	
	final private PVTableItemListener listener;
	
	private boolean selected = true;
	
	private volatile VType value = null;

	private volatile VType saved = null;
	
	private volatile boolean has_changed;
	
	private double tolerance;
	
	final private PV<VType, Object> pv;

	public PVTableItem(final String name, final double tolerance, final VType saved, final PVTableItemListener listener)
	{
		this.name = name;
		this.listener = listener;
		this.tolerance = tolerance;
		this.saved = saved;
		determineIfChanged();
		
		if (name.isEmpty())
			pv = null;
		else
			pv = PVManager.readAndWrite(latestValueOf(vType(name))).readListener(this).timeout(ofSeconds(30.0)).asynchWriteAndMaxReadRate(ofSeconds(1.0));
	}

	public boolean isSelected()
	{
		return selected;
	}
	
	public void setSelected(final boolean selected)
	{
		this.selected = selected;
	}

	/** @return Returns the name of the 'main' PV. */
    public String getName()
    {
    	return name;
    }
    
    /** PVReaderListener
     *  {@inheritDoc}
     */
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

    /** @param new_value New value of item */
    protected void updateValue(final VType new_value)
    {
    	value = new_value;
		determineIfChanged();
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
		determineIfChanged();
    }

    /** Write saved value back to PV (if item is selected) */
    public void restore()
    {
    	if (! isSelected())
    		return;
		final Object basic_value = VTypeHelper.getValue(saved);
		if (basic_value == null)
			return;
		pv.write(basic_value);
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
		determineIfChanged();
		listener.tableItemChanged(this);
	}

	/** @return <code>true</code> if value has changed from saved value */
    public boolean isChanged()
    {
    	return has_changed;
    }
    
    /** Update <code>has_changed</code> based on current and saved value */
    private void determineIfChanged()
    {
    	final VType saved_value = saved;
    	if (saved_value == null)
    	{
    		has_changed = false;
    		return;
    	}
    	has_changed = ! VTypeHelper.equalValue(value, saved_value, tolerance);
    }
    
    /** Must be called to release resources when item no longer in use */
    public void dispose()
    {
    	if (pv != null)
    		pv.close();
    }
    
    @Override
    public String toString()
    {
    	final StringBuilder buf = new StringBuilder();
    	buf.append(name).append(" = ").append(VTypeHelper.toString(value));
    	if (saved != null)
    	{
    		if (has_changed)
    			buf.append(" ( != ");
    		else
    			buf.append(" ( == ");
    		buf.append(VTypeHelper.toString(saved)).append(" +- ").append(tolerance).append(")");
    	}
    	return buf.toString();
    }
}
