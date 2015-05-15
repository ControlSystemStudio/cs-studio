/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.csstudio.display.pvtable.Plugin;
import org.csstudio.display.pvtable.Preferences;
import org.csstudio.vtype.pv.PV;
import org.csstudio.vtype.pv.PVListener;
import org.csstudio.vtype.pv.PVPool;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VByteArray;
import org.epics.vtype.VEnum;
import org.epics.vtype.VEnumArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

/** One item (row) in the PV table.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVTableItem implements PVListener
{
    final private PVTableItemListener listener;

    private boolean selected = true;

    private String name;

    private volatile VType value;

    private volatile Optional<SavedValue> saved = Optional.empty();

    private volatile boolean has_changed;

    private double tolerance;

    final private AtomicReference<PV> pv = new AtomicReference<PV>(null);

    /** Initialize
     *
     *  @param name
     *  @param tolerance
     *  @param saved
     *  @param listener
     */
    public PVTableItem(final String name, final double tolerance, final SavedValue saved, final PVTableItemListener listener)
    {
        this(name, tolerance, saved, listener, ValueFactory.newVString("", ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "No PV"), ValueFactory.timeNow()));
    }

    /** Initialize
     *
     *  @param name
     *  @param tolerance
     *  @param saved
     *  @param listener
     */
    public PVTableItem(final String name, final double tolerance, final SavedValue saved, final PVTableItemListener listener, final VType initial_value)
    {
        this.listener = listener;
        this.tolerance = tolerance;
        this.saved = Optional.ofNullable(saved);
        this.value = initial_value;
        determineIfChanged();
        createPV(name);
    }

    /** Set PV name and create reader/writer
     *  @param name PV name
     */
    private void createPV(final String name)
    {
        this.name = name;
        if (! name.isEmpty())
        {
            try
            {
                PV new_pv = PVPool.getPV(name);
                new_pv.addListener(this);
                pv.set(new_pv);
            }
            catch (Exception ex)
            {
                Plugin.getLogger().log(Level.WARNING, "Cannot create PV " + name, ex);
                updateValue(ValueFactory.newVString("PV Error", ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "No PV"), ValueFactory.timeNow()));
            }
        }
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
        listener.tableItemSelectionChanged(this);
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
        dispose();
        saved = Optional.empty();
        value = null;
        has_changed = false;
        createPV(new_name);
        return true;
    }

    /** PVListener
     *  {@inheritDoc}
     */
    @Override
    public void permissionsChanged(final PV pv, final boolean readonly)
    {
        listener.tableItemChanged(this);
    }

    /** PVListener
     *  {@inheritDoc}
     */
    @Override
    public void valueChanged(final PV pv, final VType value)
    {
        updateValue(value);
    }

    /** PVListener
     *  {@inheritDoc}
     */
    @Override
    public void disconnected(final PV pv)
    {
        updateValue(ValueFactory.newVString("Disconnected", ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "Disconnected"), ValueFactory.timeNow())); //$NON-NLS-1$
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

    /** @return Options for current value, not <code>null</code> if not enumerated */
    public String[] getValueOptions()
    {
        final VType copy = value;
        if (! (copy instanceof VEnum))
            return null;
        final List<String> options = ((VEnum)copy).getLabels();
        return options.toArray(new String[options.size()]);
    }

    /** @return <code>true</code> when PV is writable */
    public boolean isWritable()
    {
        final PV the_pv = pv.get();
        return the_pv != null  &&  the_pv.isReadonly() == false;
    }

    /** @param new_value Value to write to the item's PV */
    public void setValue(String new_value)
    {
        new_value = new_value.trim();
        try
        {
            final PV the_pv = pv.get();
            if (the_pv == null)
                throw new Exception("Not connected");
            final VType pv_type = the_pv.read();
            if (pv_type instanceof VNumber)
                the_pv.write(Double.parseDouble(new_value));
            else if (pv_type instanceof VEnum)
            {   // Value is displayed as "6 = 1 second"
                // Locate the initial index, ignore following text
                final int end = new_value.indexOf(' ');
                final int index = end > 0
                    ? Integer.valueOf(new_value.substring(0, end))
                    : Integer.valueOf(new_value);
                    the_pv.write(index);
            }
            else if (pv_type instanceof VByteArray  &&  Preferences.treatByteArrayAsString())
            {   // Write string as byte array WITH '\0' TERMINATION!
                final byte[] bytes = new byte[new_value.length() + 1];
                System.arraycopy(new_value.getBytes(), 0, bytes, 0, new_value.length());
                bytes[new_value.length()] = '\0';
                the_pv.write(bytes);
            }
            else if (pv_type instanceof VNumberArray)
            {
                final String[] elements = new_value.split("\\s*,\\s*");
                final int N = elements.length;
                final double[] data = new double[N];
                for (int i=0; i<N; ++i)
                    data[i] = Double.parseDouble(elements[i]);
                the_pv.write(data);
            }
            else if (pv_type instanceof VEnumArray)
            {
                final String[] elements = new_value.split("\\s*,\\s*");
                final int N = elements.length;
                final int[] data = new int[N];
                for (int i=0; i<N; ++i)
                    data[i] = (int) Double.parseDouble(elements[i]);
                the_pv.write(data);
            }
            else // Write other types as string
                the_pv.write(new_value);
        }
        catch (Throwable ex)
        {
            Plugin.getLogger().log(Level.WARNING, "Cannot set " + getName() + " = " + new_value, ex);
        }
    }


    /** Save current value as saved value */
    public void save()
    {
        try
        {
            saved = Optional.of(SavedValue.forCurrentValue(value));
        }
        catch (Exception ex)
        {
            Plugin.getLogger().log(Level.WARNING, "Cannot save value of " + getName(), ex);
        }
        determineIfChanged();
    }

    /** Write saved value back to PV */
    public void restore()
    {
        final PV the_pv = pv.get();
        final SavedValue the_value = saved.orElse(null);
        if (the_pv == null  ||  ! isWritable()  || the_value == null)
            return;
        try
        {
            the_value.restore(the_pv);
        }
        catch (Exception ex)
        {
            Plugin.getLogger().log(Level.WARNING, "Error restoring " + getName(), ex);
        }
    }

    /** @return Returns the saved_value */
    public Optional<SavedValue> getSavedValue()
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
        final Optional<SavedValue> saved_value = saved;
        if (! saved_value.isPresent())
        {
            has_changed = false;
            return;
        }
        try
        {
            has_changed = ! saved_value.get().isEqualTo(value, tolerance);
        }
        catch (Exception ex)
        {
            Plugin.getLogger().log(Level.WARNING, "Change test failed for " + getName(), ex);
        }
    }

    /** Must be called to release resources when item no longer in use */
    public void dispose()
    {
        final PV the_pv = pv.getAndSet(null);
        if (the_pv != null)
        {
            the_pv.removeListener(this);
            PVPool.releasePV(the_pv);
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append(name);
        if (! isWritable())
            buf.append(" (read-only)");
        buf.append(" = ").append(VTypeHelper.toString(value));
        final Optional<SavedValue> saved_value = saved;
        if (saved_value.isPresent())
        {
            if (has_changed)
                buf.append(" ( != ");
            else
                buf.append(" ( == ");
            buf.append(saved_value.get().toString()).append(" +- ").append(tolerance).append(")");
        }
        return buf.toString();
    }
}
