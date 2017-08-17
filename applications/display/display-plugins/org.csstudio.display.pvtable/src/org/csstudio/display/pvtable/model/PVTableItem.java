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
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VByteArray;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VEnumArray;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VString;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

/** One item (row) in the PV table.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVTableItem
{
    final private PVTableItemListener listener;

    private boolean selected = true;

    /** Primary PV name */
    private String name = null;

    /** Last known value of the PV */
    private volatile VType value;

    /** Value of the PV's description */
    private volatile String desc_value = "";

    /** Saved (snapshot) value */
    private volatile Optional<SavedValue> saved = Optional.empty();

    /** TimeStamp Saved */
    private volatile String time_saved = "";

    /** Does current value differ from saved value? */
    private volatile boolean has_changed;

    /** Tolerance for comparing current and saved (if they're numeric) */
    private double tolerance;

    private volatile boolean use_completion = false;

    /** Primary PV */
    final private AtomicReference<PV> pv = new AtomicReference<PV>(null);

    /** Description PV */
    final private AtomicReference<PV> desc_pv = new AtomicReference<PV>(null);

    /** Listener to primary PV */
    final private PVListener pv_listener = new PVListener()
    {
        @Override
        public void permissionsChanged(final PV pv, final boolean readonly)
        {
            listener.tableItemChanged(PVTableItem.this);
        }

        @Override
        public void valueChanged(final PV pv, final VType value)
        {
            updateValue(value);
        }

        @Override
        public void disconnected(final PV pv)
        {
            updateValue(ValueFactory.newVString(
                    "Disconnected", ValueFactory
                            .newAlarm(AlarmSeverity.UNDEFINED, "Disconnected"),
                    ValueFactory.timeNow()));
        }
    };

    /** Listener to description PV */
    final private PVListener desc_pv_listener = new PVListener()
    {
        @Override
        public void permissionsChanged(final PV pv, final boolean readonly)
        {
        }

        @Override
        public void valueChanged(final PV pv, final VType value)
        {
            if (value instanceof VString)
            {
                desc_value = ((VString) value).getValue();
            }
            else
            {
                desc_value = "";
            }
            listener.tableItemChanged(PVTableItem.this);
        }

        @Override
        public void disconnected(final PV pv)
        {
            desc_value = "";
        }
    };

    private boolean conf = false;

    private Measure measure = null;

    /**
     * Initialize
     *
     * @param name
     * @param tolerance
     * @param saved
     * @param listener
     */
    public PVTableItem(final String name, final double tolerance,
            final SavedValue saved, final PVTableItemListener listener)
    {
        this(name, tolerance, saved, listener,
                ValueFactory.newVString("",
                        ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "No PV"),
                        ValueFactory.timeNow()));
    }

    /**
     * Initialize
     *
     * @param name
     * @param tolerance
     * @param saved
     * @param listener
     */
    public PVTableItem(final String name, final double tolerance,
            final SavedValue saved, final PVTableItemListener listener,
            final VType initial_value)
    {
        this.listener = listener;
        this.tolerance = tolerance;
        this.saved = Optional.ofNullable(saved);
        this.value = initial_value;
        determineIfChanged();
        createPVs(name);
    }

    /**
     * Initialize
     *
     * @param name
     * @param tolerance
     * @param saved
     * @param listener
     * @param time
     */
    public PVTableItem(final String name, String time, final double tolerance,
            final SavedValue saved, final PVTableItemListener listener)
    {
        this(name, time, tolerance, saved, listener,
                ValueFactory.newVString("",
                        ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "No PV"),
                        ValueFactory.timeNow()));
    }

    /**
     * Initialize
     *
     * @param name
     * @param time
     * @param conf
     * @param tolerance
     * @param saved
     * @param listener
     */
    public PVTableItem(final String name, String time, boolean conf,
            final double tolerance, final SavedValue saved,
            final PVTableItemListener listener)
    {
        this(name, time, conf, null, tolerance, saved, listener,
                ValueFactory.newVString("",
                        ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "No PV"),
                        ValueFactory.timeNow()));
    }

    /**
     * Initialize
     *
     * @param name
     * @param time
     * @param conf
     * @param measure
     * @param tolerance
     * @param saved
     * @param listener
     */
    public PVTableItem(final String name, String time, boolean conf,
            Measure measure, final double tolerance, final SavedValue saved,
            final PVTableItemListener listener)
    {
        this(name, time, conf, measure, tolerance, saved, listener,
                ValueFactory.newVString("",
                        ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "No PV"),
                        ValueFactory.timeNow()));
    }

    /**
     * Initialize
     *
     * @param name
     * @param tolerance
     * @param saved
     * @param listener
     * @param time
     * @param initial_value
     */
    public PVTableItem(final String name, String time, final double tolerance,
            final SavedValue saved, final PVTableItemListener listener,
            final VType initial_value)
    {
        this(name, time, false, null, tolerance, saved, listener,
                initial_value);
    }

    /**
     * Initialize
     *
     * @param name
     * @param time
     * @param conf
     * @param measure
     * @param tolerance
     * @param saved
     * @param listener
     * @param initial_value
     */
    public PVTableItem(final String name, String time, boolean conf,
            Measure measure, final double tolerance, final SavedValue saved,
            final PVTableItemListener listener, final VType initial_value)
    {
        this.listener = listener;
        this.time_saved = (time == null) ? "" : time;
        this.conf = conf;
        this.tolerance = tolerance;
        this.saved = Optional.ofNullable(saved);
        this.value = initial_value;
        this.measure = measure;
        determineIfChanged();
        createPVs(name);
    }

    /**
     * Set PV name and create reader/writer
     *
     * @param name
     *            Primary PV name
     */
    private void createPVs(final String name)
    {
        this.name = name;
        // Ignore empty PVs or comments
        if (name.isEmpty() || isComment() || isMeasure())
        {
            return;
        }
        try
        {
            final PV new_pv = PVPool.getPV(name);
            new_pv.addListener(pv_listener);
            pv.set(new_pv);
            if (Preferences.showDescription())
            {
                // Determine DESC field.
                // If name already includes a field,
                // replace it with DESC field.
                final int sep = name.lastIndexOf('.');
                final String desc_name = sep >= 0
                        ? name.substring(0, sep) + ".DESC" : name + ".DESC";
                final PV new_desc_pv = PVPool.getPV(desc_name);
                new_desc_pv.addListener(desc_pv_listener);
                desc_pv.set(new_desc_pv);
            }
        }
        catch (Exception ex)
        {
            Plugin.getLogger().log(Level.WARNING, "Cannot create PV " + name,
                    ex);
            updateValue(ValueFactory.newVString("PV Error",
                    ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, "No PV"),
                    ValueFactory.timeNow()));
        }
    }

    /** @return <code>true</code> if item is selected to be restored */
    public boolean isSelected()
    {
        return selected && !isComment() && !isMeasure();
    }

    /**
     * @param selected
     *            Should item be selected to be restored?
     */
    public void setSelected(final boolean selected)
    {
        this.selected = selected && !isComment();
        listener.tableItemSelectionChanged(this);
    }

    /** @return Returns the name of the 'main' PV. */
    public String getName()
    {
        return name;
    }

    /** @return Returns the comment. */
    public String getComment()
    {
        // Skip initial "#". Trim in case of another space from "# "
        return name.substring(1).trim();
    }

    /** @return Returns the conf header. */
    public String getConfHeader()
    {
        String lowName = name.toLowerCase();
        if (lowName.startsWith("#conf#"))
        {
            // If user don't add text after #conf#
            if (lowName.contentEquals("#conf#")
                    || lowName.contentEquals("#conf# "))
            {
                return lowName.substring(6).concat(" Config").trim();
            }
            return lowName.substring(6).trim();
        }
        if (lowName.startsWith("#configuration#"))
        {
            // If user don't add text after #conf#
            if (lowName.contentEquals("#conf#")
                    || lowName.contentEquals("#conf# "))
            {
                return lowName.substring(14).concat(" Config").trim();
            }
            return lowName.substring(14).trim();
        }
        return lowName;
    }

    /** @return Returns the measure header. */
    public String getMeasureHeader()
    {
        return name.substring(8).trim();
    }

    /** @return the measure which this item is. */
    public Measure getMeasure()
    {
        return measure;
    }

    /** @return Returns if the item is a conf, or not. */
    public boolean isConf()
    {
        return this.conf;
    }

    /** @return Returns if the item is a measure, or not. */
    public boolean isMeasure()
    {
        if (this.measure == null)
        {
            return false;
        }
        return true;
    }

    /** Set conf to true (This is a conf). */
    public void setConf(boolean b)
    {
        this.conf = b;
    }

    /** Set measure to true (This is a measure). */
    public void setMeasure(Measure measure)
    {
        this.measure = measure;
    }

    /**
     * Update PV name
     * <p>
     * Also resets saved and current value, since it no longer applies to the
     * new name.
     *
     * @param new_name
     *            PV Name
     * @return <code>true</code> if name was indeed changed
     */
    public boolean updateName(final String new_name)
    {
        if (name.equals(new_name))
        {
            return false;
        }
        dispose();
        saved = Optional.empty();
        time_saved = "";
        value = null;
        has_changed = false;
        createPVs(new_name);
        return true;
    }

    /**
     * @param new_value
     *            New value of item
     */
    protected void updateValue(final VType new_value)
    {
        value = new_value;
        determineIfChanged();
        listener.tableItemChanged(this);
    }

    /** @return Value */
    public VType getValue()
    {
        return !isMeasureHeader() && (isComment() || isMeasure()) ? null
                : value;
    }

    /** @return Description */
    public String getDescription()
    {
        return desc_value;
    }

    /**
     * @return Options for current value, not <code>null</code> if not
     *         enumerated
     */
    public String[] getValueOptions()
    {
        final VType copy = value;
        if (!(copy instanceof VEnum)) return null;
        final List<String> options = ((VEnum) copy).getLabels();
        return options.toArray(new String[options.size()]);
    }

    /** @return <code>true</code> when PV is writable */
    public boolean isWritable()
    {
        final PV the_pv = pv.get();
        return the_pv != null && the_pv.isReadonly() == false && !isComment()
                && !isMeasure();
    }

    /** @return Await completion when restoring value to PV? */
    public boolean isUsingCompletion()
    {
        return use_completion;
    }

    /** @param use_completion Await completion when restoring value to PV? */
    public void setUseCompletion(final boolean use_completion)
    {
        this.use_completion = use_completion;
    }

    /**
     * @param new_value
     *            Value to write to the item's PV
     */
    public void setValue(String new_value)
    {
        new_value = new_value.trim();
        try
        {
            final PV the_pv = pv.get();
            if (the_pv == null)
            {
                throw new Exception("Not connected");
            }
            final VType pv_type = the_pv.read();
            if (pv_type instanceof VNumber)
            {
                if (Preferences.showUnits())
                {   // Strip units so that only the number gets written
                    final String units = ((VNumber)pv_type).getUnits();
                    if (units.length() > 0  &&  new_value.endsWith(units))
                        new_value = new_value.substring(0, new_value.length() - units.length()).trim();
                }
                the_pv.write(Double.parseDouble(new_value));
            }
            else if (pv_type instanceof VEnum)
            { // Value is displayed as "6 =
              // 1 second"
                // Locate the initial index, ignore following text
                final int end = new_value.indexOf(' ');
                final int index = end > 0
                        ? Integer.valueOf(new_value.substring(0, end))
                        : Integer.valueOf(new_value);
                the_pv.write(index);
            }
            else if (pv_type instanceof VByteArray && Preferences.treatByteArrayAsString())
            {
                // Write string as byte array WITH '\0' TERMINATION!
                final byte[] bytes = new byte[new_value.length() + 1];
                System.arraycopy(new_value.getBytes(), 0, bytes, 0,
                        new_value.length());
                bytes[new_value.length()] = '\0';
                the_pv.write(bytes);
            }
            else if (pv_type instanceof VNumberArray)
            {
                final String[] elements = new_value.split("\\s*,\\s*");
                final int N = elements.length;
                final double[] data = new double[N];
                for (int i = 0; i < N; ++i)
                {
                    data[i] = Double.parseDouble(elements[i]);
                }
                the_pv.write(data);
            }
            else if (pv_type instanceof VEnumArray)
            {
                final String[] elements = new_value.split("\\s*,\\s*");
                final int N = elements.length;
                final int[] data = new int[N];
                for (int i = 0; i < N; ++i)
                {
                    data[i] = (int) Double.parseDouble(elements[i]);
                }
                the_pv.write(data);
            }
            else // Write other types as string
                the_pv.write(new_value);
        }
        catch (Throwable ex)
        {
            Plugin.getLogger().log(Level.WARNING,
                    "Cannot set " + getName() + " = " + new_value, ex);
        }
    }

    /**
     * Save current value as saved value And current timestamp as time saved
     */
    public void save()
    {
        if (isComment() && !isMeasureHeader())
        {
            return;
        }
        try
        {
            time_saved = (TimestampHelper
                    .format(VTypeHelper.getTimestamp(value)));
            saved = Optional.of(SavedValue.forCurrentValue(value));
        }
        catch (Exception ex)
        {
            Plugin.getLogger().log(Level.WARNING,
                    "Cannot save value of " + getName(), ex);
        }
        determineIfChanged();
    }

    /** @return time_saved, the timestamp saved */
    public String getTime_saved()
    {
        return this.time_saved;
    }

    /**
     * @param time_saved,
     *            the current value of timestamp
     */
    public void setTime_saved(String time_saved)
    {
        this.time_saved = time_saved;
    }

    /** Write saved value back to PV
     *  @param completion_timeout_seconds
     *  @throws Exception on error
     */
    public void restore(final long completion_timeout_seconds) throws Exception
    {
        if (isComment() || isMeasure()  ||  !isWritable())
            return;

        final PV the_pv = pv.get();
        final SavedValue the_value = saved.orElse(null);
        if (the_pv == null || the_value == null)
            return;

        the_value.restore(the_pv, isUsingCompletion() ? completion_timeout_seconds : 0);
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

    /**
     * @param tolerance Tolerance for comparing saved and current value
     */
    public void setTolerance(final double tolerance)
    {
        this.tolerance = tolerance;
        determineIfChanged();
        listener.tableItemChanged(this);
    }

    /** @return <code>true</code> if this item is a comment instead of a PV with
     *          name, value etc.
     */
    public boolean isComment()
    {
        return name.startsWith("#");
    }

    /** @return true if this item is a config header instead of a PV with name,
     *               value etc
     */
    public boolean isConfHeader()
    {
        String lowName = name.toLowerCase();
        return lowName.startsWith("#conf#")
                || lowName.startsWith("#configuration#");
    }

    /** @return true if this item is a measure header instead of a PV with name,
     *          value etc
     */
    public boolean isMeasureHeader()
    {
        String lowName = name.toLowerCase();
        return lowName.startsWith("#mesure#");
    }

    /** @return <code>true</code> if value has changed from saved value */
    public boolean isChanged()
    {
        return has_changed;
    }

    /** Update <code>has_changed</code> based on current and saved value */
    private void determineIfChanged()
    {
        if (isMeasure())
        {
            has_changed = false;
            return;
        }
        final Optional<SavedValue> saved_value = saved;
        if (!saved_value.isPresent())
        {
            has_changed = false;
            return;
        }
        try
        {
            has_changed = !saved_value.get().isEqualTo(value, tolerance);
        }
        catch (Exception ex)
        {
            Plugin.getLogger().log(Level.WARNING,
                    "Change test failed for " + getName(), ex);
        }
    }

    /** Must be called to release resources when item no longer in use */
    public void dispose()
    {
        PV the_pv = pv.getAndSet(null);
        if (the_pv != null)
        {
            the_pv.removeListener(pv_listener);
            PVPool.releasePV(the_pv);
        }

        the_pv = desc_pv.getAndSet(null);
        if (the_pv != null)
        {
            the_pv.removeListener(pv_listener);
            PVPool.releasePV(the_pv);
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append(name);
        if (!isWritable())
        {
            buf.append(" (read-only)");
        }
        buf.append(" = ").append(VTypeHelper.toString(value));
        final Optional<SavedValue> saved_value = saved;
        if (saved_value.isPresent())
        {
            if (has_changed)
            {
                buf.append(" ( != ");
            }
            else
            {
                buf.append(" ( == ");
            }
            buf.append(saved_value.get().toString()).append(" +- ")
                    .append(tolerance).append(")");
        }
        return buf.toString();
    }
}
