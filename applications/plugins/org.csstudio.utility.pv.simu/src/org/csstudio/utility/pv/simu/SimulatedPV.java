package org.csstudio.utility.pv.simu;

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

/** Simulated PV.
 *  <p>
 *  By default, it processes at 1 Hz with a value range of -5 .. 5.
 *  <p>
 *  Names "something(-5, 5, 0.2)" would result in a value range from -5 to 5,
 *  updating every 0.2 seconds.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public abstract class SimulatedPV extends BasicPV implements Runnable
{
    /** Defaults */
    private static final int DEFAULT_MIN = -5,
                             DEFAULT_MAX = 5,
                             DEFAULT_UPDATE = 1000,
                             MIN_UPDATE_PERIOD = 10;

    /** Value range: min...max */
    protected double min, max;

    /** Update period (millisec) */
    protected long update_period;

    /** Meta data */
    final private INumericMetaData meta;

    /** Quality */
    final private Quality quality = Quality.Original;
    
    /** Most recent status */
    protected String status = "OK";

    /** Most recent value */
    protected ITimestamp time = TimestampFactory.now();

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
        super(name);
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
        meta = ValueFactory.createNumericMetaData(min, max, min+0.3*range, min+0.7*range, min+0.1*range, min+0.9*range, 3, "a.u.");
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
        running = true;
        update_thread = new Thread(this, name);
        update_thread.start();
    }

    /** {@inheritDoc} */
    public synchronized void stop()
    {
        running = false;
        update_thread = null;
        notifyAll();
    }

    /** Set a new value. To be called from <code>update</code>.
     *  Will adjust the alarm severity.
     *  @param time New time stamp
     *  @param number New numeric value
     */
    protected void setValue(final ITimestamp time, final double number)
    {
        this.time = time;
        ISeverity severity;
        if (number > meta.getWarnLow()  &&  number < meta.getWarnHigh())
        {
            severity = ValueFactory.createOKSeverity();
            status = severity.toString();
        }
        else if (number > meta.getAlarmLow()  &&  number < meta.getAlarmHigh())
        {
            severity = ValueFactory.createMinorSeverity();
            status = number <= meta.getWarnLow() ? "Low" : "High";
        }
        else
        {
            severity = ValueFactory.createMajorSeverity();
            status = number <= meta.getAlarmLow() ? "Way Low" : "Way High";
        }
        value = ValueFactory.createDoubleValue(time, severity, status, meta, quality, new double[] { number });
    }
    
    /** To be implemented by derived class.
     *  Will be called when simulated PV is 'processed',
     *  must compute new number and call {@link #setValue(ITimestamp, double)}
     */
    abstract protected void update();
    
    /** Runnable that updates the value and fires events */
    public void run()
    {
        while (true)
        {
            update();
            fireValueUpdate();
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
