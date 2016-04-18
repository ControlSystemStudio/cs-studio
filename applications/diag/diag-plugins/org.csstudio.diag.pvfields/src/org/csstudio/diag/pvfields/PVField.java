/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields;

import static org.diirt.datasource.ExpressionLanguage.latestValueOf;
import static org.diirt.datasource.vtype.ExpressionLanguage.vType;
import static org.diirt.util.time.TimeDuration.ofSeconds;

import java.time.Duration;

import org.csstudio.diag.pvfields.model.PVFieldListener;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVReader;
import org.diirt.datasource.PVReaderEvent;
import org.diirt.datasource.PVReaderListener;
import org.diirt.vtype.VType;

/** Field of a record, providing original and current value.
 *
 *  <p>Original value might be obtained from a {@link DataProvider}.
 *  Current value is fetched from control system.
 *
 *  @author Kay Kasemir
 */
public class PVField
{
    final private String name;
    final private String original_value;
    private volatile String current_value = "<disconnected>";
    private PVReader<VType> pv;

    /** Initialize
     *  @param name Full name of the field, i.e. record name.field
     *  @param original_value Original value
     */
    public PVField(final String name, final String original_value)
    {
        this.name = name;
        this.original_value = original_value;
    }

    /** Initialize
     *  @param name Full name of the field, i.e. record name.field
     */
    public PVField(final String name)
    {
        this(name, "");
    }

    /** @return Full field name */
    public String getName()
    {
        return name;
    }

    /** @return Original value */
    public String getOriginalValue()
    {
        return original_value;
    }

    /** @return Current value */
    public String getCurrentValue()
    {
        return current_value;
    }

    /** @return true if value has changed from original value */
    public boolean isChanged()
    {
        try
        {    // Attempt numerical comparison
            final double current = Double.parseDouble(current_value);
            final double original = Double.parseDouble(original_value);
            return original != current;
        }
        catch (Throwable ex)
        {
            // Not numeric? Ignore, compare as text
        }
        if (original_value == null  ||  original_value.isEmpty())
            return false;
        return !original_value.equals(current_value);
    }

    /** Start updates of current value, subscribe to control system
     *  @param listener
     */
    public void start(final PVFieldListener listener)
    {
        if (pv != null)
            throw new IllegalStateException("Already started");

        final PVReaderListener<VType> pv_listener = new PVReaderListener<VType>()
        {
            @Override
            public void pvChanged(final PVReaderEvent<VType> event)
            {
                final PVReader<VType> pv = event.getPvReader();
                final Exception ex = pv.lastException();
                if (ex != null)
                    current_value = "Error: " + ex.getMessage();
                else
                {
                    try
                    {
                        current_value = VTypeHelper.toString(pv.getValue());
                    }
                    catch (Throwable err)
                    {
                        System.err.println("PV " + pv.getName());
                        err.printStackTrace();
                    }
                }
                listener.updateField(PVField.this);
            }
        };
        pv = PVManager.read(latestValueOf(vType(name))).timeout(Duration.ofMillis(Preferences.getTimeout())).readListener(pv_listener).maxRate(ofSeconds(0.5));
    }

    /** Stop updates of current value, unsubscribe from control system */
    public void stop()
    {
        pv.close();
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return String.format(
                "%s: original '%s', current '%s'",
                name, original_value, current_value);
    }
}
