package org.csstudio.utility.pv;

import org.csstudio.platform.util.ITimestamp;

/** A control system PV.
 *  <p>
 *  When 'start'ed, the PV will attempt to connect or
 *  do whatever is needed to obtain the meta information like
 *  units, precision, ... Then it will subscribe to updates
 *  of the current value.
 *  
 *  @author Kay Kasemir
 */
public interface PV
{
    /** @return Returns the name. */
    public String getName();
    
    /** Add a new listener. */
    public void addListener(PVListener listener);

    /** Remove a listener. */
    public void removeListener(PVListener listener);

    /** Start the PV: connect, ... */
    public void start() throws Exception;
    
    /** @return Returns <code>true</code> between <code>start()</code> and <code>stop()</code>. */
    public boolean isRunning();

    /** @return Returns <code>true</code> when connected.
     *  While <code>isRunning</code>, we are subscribed for value updates,
     *  but we might still be disconnected, at least temporarily.
     */
    public boolean isConnected();
    
    /** Stop the PV: disconnect, ... */
    public void stop();

    /** The engineering units description.
     *  <p>
     *  Part of the meta information obtained during the initial
     *  connection cycle.
     *  Should be valid when <code>isConnected</code>.
     *  Will stay at its last value on disconnect.
     *  @return The units string.
     *  @see #getPrecision()
     */
    public String getUnits();
    
    /** Display precision, i.e. number of digits after the decimal point.
     *  @return The suggested display precision.
     *  @see #getUnits()
     */
    public int getPrecision();
    
    /** Get the value.
     *  <p>
     *  This is the most recent value.
     *  Check isConnected() to see if this is valid,
     *  or use inside a PV listener's value update.
     *  <p>
     *  The value should be one of these types:
     *  <ul>
     *  <li><code>Double</code>
     *  <li><code>String</code>
     *  <li><code>Integer</code>
     *  <li><code>EnumValue</code>
     *  </ul>
     *  So except for the last one, ordinary Java types.
     *  <p>
     *  
     *  @see PVListener
     *  @see #isConnected()
     *  @return Returns the most recent value.
     */
    public Object getValue();

    /** Set PV to given value. */
    public void setValue(Object new_value);
    
    /** @return Returns the last time stamp. */
    public ITimestamp getTime();
    
    /** A severity code, where 0 means 'OK',
     *  higher numbers reflect a higher severity.
     *  @return Returns the severity code.
     *  @see #getSeverity(int)
     */
    public int getSeverityCode();

    /** @return Returns the severity text or <code>null</code>. */
    public String getSeverity();
    
    /** @return Returns the status string or <code>null</code>. */
    public String getStatus();
}