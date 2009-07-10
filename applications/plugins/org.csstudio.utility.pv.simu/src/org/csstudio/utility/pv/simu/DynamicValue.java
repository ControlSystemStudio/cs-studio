package org.csstudio.utility.pv.simu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.IValue.Quality;

/** A value that changes periodically driven by its own background thread.
 *  <p>
 *  This value also provides status/severity changes based on the
 *  current value, and parses the value range as well as processing
 *  period from its name:
 *  <p>
 *  Names "something(-10, 10, 0.5, 0.2)" would result in a value range of -10 to 10,
 *  updating every 0.2 seconds on a step of 0.5.
 *  <p>
 *  By default, it processes at 1 Hz with a value range of -5 .. 5 on a step of 1.
 *  
 *  @author Kay Kasemir, Xihui Chen
 */
@SuppressWarnings("nls")
abstract public class DynamicValue extends Value implements Runnable
{
    /** Defaults */
    private static final int DEFAULT_MIN = -5,
                             DEFAULT_MAX = 5,
                             DEFAULT_UPDATE = 1000,
                             DEFAULT_STEP = 1,
                             MIN_UPDATE_PERIOD = 10;

    /** Value range: min...max */
    protected double min, max, step;

    /** Update period (millisec) */
    protected long update_period;

    /** Meta data */
    final private INumericMetaData meta;

    /** Reference counter.
     *  Update thread starts with first reference,
     *  stops when last reference is released.
     */
    private int references = 0;
    
    /** Thread that updates the PV.
     *  <p>
     *  Setting it to null will ask the thread to quit.
     */
    private volatile Thread update_thread = null;

    /** Initialize
     *  @param name Name, also used to parse "name(min, max, period)"
     */
    public DynamicValue(final String name)
    {
        super(name);

        // Parse "name(min, max, step, update_seconds)"
        Pattern name_pattern = Pattern.compile("\\w+\\(\\s*([-0-9.]+)\\s*,\\s*([-0-9.]+)\\s*,\\s*([-0-9.]+)\\s*,\\s*([0-9.]+)\\s*\\)");
        Matcher matcher = name_pattern.matcher(name);
        if (matcher.matches())
        {
            try
            {
                min = Double.parseDouble(matcher.group(1));
                max = Double.parseDouble(matcher.group(2));
                step = Double.parseDouble(matcher.group(3));
                update_period = Math.round(Double.parseDouble(matcher.group(4))*1000);
            }
            catch (Throwable ex)
            {   // Number parse error
            	useDefault();
            }
        }
        else
        {   // Parse "name(min, max, update_seconds)"
        	name_pattern = Pattern.compile("\\w+\\(\\s*([-0-9.]+)\\s*,\\s*([-0-9.]+)\\s*,\\s*([0-9.]+)\\s*\\)");
        	matcher = name_pattern.matcher(name);
        	 if (matcher.matches())
             {
                 try
                 {
                     min = Double.parseDouble(matcher.group(1));
                     max = Double.parseDouble(matcher.group(2));
                     update_period = Math.round(Double.parseDouble(matcher.group(3))*1000);
                     step = DEFAULT_STEP;
                 }
                 catch (Throwable ex)
                 {   // Number parse error
                     useDefault();
                 }
             }else{
	            useDefault();
             }            
        }
        // Enforce minimum period delay
        if (update_period < MIN_UPDATE_PERIOD)
            update_period = MIN_UPDATE_PERIOD;
        // Assert min <= max
        if (min > max)
        {
            final double tmp = max;
            max = min;
            min = tmp;
        }
        final double range = max - min;
        meta = ValueFactory.createNumericMetaData(min, max, min+0.3*range, min+0.7*range, min+0.1*range, min+0.9*range, 3, "a.u.");
    }

	/**
	 * use default settings.
	 */
	private void useDefault() {
		    min = DEFAULT_MIN;
		    max = DEFAULT_MAX;
		    step = DEFAULT_STEP;
		    update_period = DEFAULT_UPDATE;
	}
    
    /** {@inheritDoc} */
    public synchronized void start() throws Exception
    {
        ++references ;
        if (references == 1)
        {
            update_thread = new Thread(this, getName());
            update_thread.start();
        }
    }

    /** {@inheritDoc} */
    public synchronized void stop()
    {
        --references;
        if (references <= 0)
        {
            update_thread = null;
            notifyAll();
        }
    }
    
    /** Set a new value. To be called from <code>update</code>.
     *  Will adjust the alarm severity.
     *  @param time New time stamp
     *  @param number New numeric value
     */
    protected void setValue(final double number)
    {
        final ITimestamp time = TimestampFactory.now();
        final ISeverity severity;
        final String status;
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
        setValue(ValueFactory.createDoubleValue(time, severity, status, meta,
                Quality.Original, new double[] { number }));
    }

    /** To be implemented by derived class.
     *  Will be called by periodic thread.
     *  Must compute new number and call {@link #setValue(double)}
     */
    abstract protected void update();
    
    /** Runnable that updates the value and fires events */
    public void run()
    {
        while (true)
        {
            update();
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
