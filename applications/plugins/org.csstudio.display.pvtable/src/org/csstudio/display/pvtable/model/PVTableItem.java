/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.vtype.ExpressionLanguage.vType;
import static org.epics.util.time.TimeDuration.ofSeconds;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

/** One item (row) in the PV table.
 *
 *  @author Kay Kasemir
 */
public class PVTableItem implements PVReaderListener<VType>
{
    /** Period for throttling updates from individual PV, i.e. PV attached to this item */
    private static final double READ_PERIOD_SECS = 0.2;

    final private PVTableItemListener listener;

    private boolean selected = true;

    private String name;
    
    private volatile VType value = null;

    private volatile VType saved = null;
    
    private volatile boolean has_changed;
    
    private double tolerance;
    
    private PV<VType, Object> pv;

    /** Initialize
     * 
     *  @param name
     *  @param tolerance
     *  @param saved
     *  @param listener
     */
    public PVTableItem(final String name, final double tolerance, final VType saved, final PVTableItemListener listener)
    {
        this.listener = listener;
        this.tolerance = tolerance;
        this.saved = saved;
        determineIfChanged();
        createPV(name);
    }

    /** Set PV name and create reader/writer
     *  @param name PV name
     */
    private void createPV(final String name)
    {
        this.name = name;
        if (name.isEmpty())
            pv = null;
        else
            pv = PVManager.readAndWrite(latestValueOf(vType(name))).readListener(this).timeout(ofSeconds(30.0)).synchWriteAndMaxReadRate(ofSeconds(READ_PERIOD_SECS));
    }

    /** @return <code>true</code> if item is selected to be restored */
    public boolean isSelected()
    {
        return selected;
    }
    
    /** @param selected Should item be selected to be restored? */
    public void setSelected(final boolean selected)
    {
        this.selected = selected;
    }

    /** @return Returns the name of the 'main' PV. */
    public String getName()
    {
        return name;
    }

    /** Update PV name 
     * 
     *  <p>Also resets saved and current value,
     *  since it no longer applies to the new name.
     *  @param new_name PV Name
     *  @return <code>true</code> if name was indeed changed
     */
    public boolean updateName(final String new_name)
    {
        if (name.equals(new_name))
            return false;
        if (pv != null)
            pv.close();
        saved = null;
        value = null;
        has_changed = false;
        createPV(new_name);
        return true;
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
            Logger.getLogger(PVTableItem.class.getName()).log(Level.WARNING, "Error from " + name, error); //$NON-NLS-1$
            updateValue(ValueFactory.newVString(error.getMessage(), ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "PV Error"), ValueFactory.timeNow())); //$NON-NLS-1$
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
    
    @SuppressWarnings("nls")
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
