package org.csstudio.utility.pv.simu;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.IValue.Quality;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.PlatformObject;

/** Simulated PV.
 *  <p>
 *  By default, it generates 1 Hz noise.
 *  <p>
 *  Names "noise(-5, 5, 0.2)" would result in noise from -5 to 5,
 *  updating every 0.2 seconds.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SimulatedPV extends PlatformObject implements PV, Runnable
{
    /** Defaults */
    private static final int DEFAULT_MIN = -5,
                             DEFAULT_MAX = 5,
                             DEFAULT_UPDATE = 1000,
                             MIN_UPDATE_PERIOD = 10;

    /** PV Name */
    final private String name;
    
    /** Value range: min...max */
    private double min, max;

    /** Update period (millisec) */
    private long update_period;

    /** PVListeners of this PV */
    final private CopyOnWriteArrayList<PVListener> listeners
                        = new CopyOnWriteArrayList<PVListener>();

    /** Meta data */
    final private INumericMetaData meta;

    /** Quality */
    final private Quality quality = Quality.Original;
    
    /** Most recent status */
    protected String status = "OK";

    /** Most recent value */
    protected ITimestamp time = TimestampFactory.now();

    /** Most recent value */
    protected IValue value = null;
    
    /** Thread that updates the PV.
     *  <p>
     *  When null, the PV is not 'running'.
     *  Setting it to null will ask the thread to quit.
     */
    private volatile Thread update_thread = null;

    /** Initialize
     *  @param name PV name
     */
    public SimulatedPV(final String name)
    {
        this.name = name;
        // Parse "name(min, max, update_seconds)"
        final Pattern name_pattern = Pattern.compile("\\w+\\(\\s*([-0-9.]+)\\s*,\\s*([-0-9.]+)\\s*,\\s*([0-9.]+)\\s*\\)");
        final Matcher matcher = name_pattern.matcher(name);
        if (matcher.matches())
        {
            try
            {
                min = Double.parseDouble(matcher.group(1));
                max = Double.parseDouble(matcher.group(2));
                update_period = Math.round(Double.parseDouble(matcher.group(3))*1000);
            }
            catch (Throwable ex)
            {   // Number parse error
                min = DEFAULT_MIN;
                max = DEFAULT_MAX;
                update_period = DEFAULT_UPDATE;
            }
        }
        else
        {
            min = DEFAULT_MIN;
            max = DEFAULT_MAX;
            update_period = DEFAULT_UPDATE;
        }
        // Enfore minimum
        if (update_period < MIN_UPDATE_PERIOD)
            update_period = MIN_UPDATE_PERIOD;
        final double range = max - min;
        meta = ValueFactory.createNumericMetaData(min, max, min+0.2*range, min+0.8*range, min+0.1*range, min+0.9*range, 3, "a.u.");
    }
    
    /** {@inheritDoc} */
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    public String getTypeId()
    {
        return IProcessVariable.TYPE_ID;
    }

    /** {@inheritDoc} */
    public void addListener(final PVListener listener)
    {
        listeners.add(listener);
    }

    /** {@inheritDoc} */
    public void removeListener(final PVListener listener)
    {
        listeners.remove(listener);
    }

    /** {@inheritDoc} */
    public String getStateInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public IValue getValue()
    {
        return value;
    }

    /** {@inheritDoc} */
    public IValue getValue(double timeoutSeconds) throws Exception
    {
        return getValue();
    }

    /** {@inheritDoc} */
    public boolean isConnected()
    {
        return update_thread != null;
    }

    /** {@inheritDoc} */
    public synchronized boolean isRunning()
    {
        return update_thread != null;
    }

    /** {@inheritDoc} */
    public boolean isWriteAllowed()
    {
        return false;
    }

    /** {@inheritDoc} */
    public void setValue(Object newValue) throws Exception
    {
        // NOP
    }

    /** {@inheritDoc} */
    public synchronized void start() throws Exception
    {
        update_thread = new Thread(this, name);
        update_thread.start();
    }

    /** {@inheritDoc} */
    public void stop()
    {
        synchronized (this)
        {
            update_thread = null;
            notifyAll();
        }
    }

    protected void setValue(final double number)
    {
        time = TimestampFactory.now();
        ISeverity severity;
        if (number > meta.getWarnLow()  &&  number < meta.getWarnHigh())
            severity = ValueFactory.createOKSeverity();
        else if (number > meta.getAlarmLow()  &&  number < meta.getAlarmHigh())
                severity = ValueFactory.createMinorSeverity();
        else
            severity = ValueFactory.createMajorSeverity();
        value = ValueFactory.createDoubleValue(time, severity, status, meta, quality, new double[] { number });
    }
    
    /** Runnable that updates the value and fires events */
    public void run()
    {
        while (true)
        {
            setValue(min + (Math.random() * (max - min)));
            
            for (PVListener listener : listeners)
                listener.pvValueUpdate(this);
            // Period delay, may be interrupted by stop()
            try
            {
                synchronized (this)
                {
                    wait(update_period);
                }
            }
            catch (InterruptedException e)
            {
                // Ignore
            }
            // stop() requested?
            synchronized (this)
            {
                if (update_thread == null)
                    return;
            }
        }
    }
}
