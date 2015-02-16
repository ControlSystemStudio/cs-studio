package org.csstudio.dal.simple;

import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.RemoteException;

/** A control system PV.
 *  <p>
 *  When 'start'ed, the PV will attempt to connect or
 *  do whatever is needed to obtain the meta information like
 *  units, precision, ... Then it will subscribe to updates
 *  of the current value.
 *  <p>
 *  While the {@link ChannelListener} might receive events on a
 *  non-UI thread, all the calls to the PV should come from
 *  the UI thread to prevent possible deadlocks.
 *  (The JNI CA client has deadlocked when both UI and non-UI
 *   threads called into it at the 'same' time).
 *  
 *  @author Kay Kasemir, Igor Kriznar
 */
public interface AnyDataChannel
{	
    /** 
     * @return Returns the name.
     */
    public String getUniqueName();
    
    /**
     * Add a new channel listener.
     * 
     * @param listener listener to add
     * @see ChannelListener
     */
    public void addListener(ChannelListener listener);

    /** 
     * Remove a listener.
     * 
     * @param listener the listener to be removed
     */
    public void removeListener(ChannelListener listener);
    
    /**
     * Returns the array of all registered channel listeners.
     * 
     * @return registered channel listeners
     */
    public ChannelListener[] getListeners();

    /** Start the PV: connect, get meta data, subscribe to updates,
     *  invoke {@link ChannelListener} for incoming values, ...
     *  Calling on already running channel does nothing.
     *  @see #addListener(ChannelListener)
     *  @see #stop()
     */
    public void start() throws Exception;
    
    /** 
     * Start the channel: connect, get meta data, subscribe to updates,
     *  invoke {@link ChannelListener} for incoming values, ...
     *  Method will not return until channel is connected or connection has failed.
     *  Calling on already running channel does nothing.
     *  @see #stop()
     */
    public void startSync() throws Exception;

    /** @return Returns <code>true</code> between <code>start()</code> and <code>stop()</code>. */
    public boolean isRunning();

    /** @return Returns <code>true</code> when connected.
     *  While <code>isRunning</code>, we are subscribed for value updates,
     *  but we might still be disconnected, at least temporarily.
     */
    public boolean isConnected();
    
    /** @return <code>true</code> if we have write access to the PV */
    public boolean isWriteAllowed();
    
    /** Internal state information on the PV.
     *  <p>
     *  Especially when <code>isConnected()</code> is <code>false</code>,
     *  this information might help to diagnose the problem:
     *  Did the PV never connect?
     *  Was it once connected, but some error occured?
     *  @return Some human readable state info */
    public String getStateInfo();
    
    /** Stop the PV: disconnect, ...
     *  When the PV is no longer needed, one should 'stop' it
     *  to release resources.
     */
    public void stop();

    /** Get the value.
     *  <p>
     *  This is the most recent value.
     *  Check isConnected() to see if this is valid,
     *  or use inside a PV listener's value update.
     *  
     *  @see ChannelListener
     *  @see #isConnected()
     *  @return Returns the most recent value,
     *          or <code>null</code> if there is none.
     */
    public AnyData getData();

    /** Set PV to given value.
     *  Should accept <code>Double</code>, <code>Double[]</code>,
     *  <code>Integer</code>,
     *  <code>String</code>, maybe more.
     *  @param new_value Value to write to PV
     *  @throws Exception on error
     */
    public void setValueAsObject(Object new_value) throws RemoteException;
    
    /**
     * Gets the <code>DynamicValueProperty</code> this channel is associated with.
     * @return the <code>DynamicValueProperty</code>
     */
    public DynamicValueProperty<?> getProperty();
    
    /**
     * Returns <code>true</code> if meta data has been initialized and
     * <code>false</code> otherwise
     * @return <code>true</code> if meta data has been initialized and
     * <code>false</code> otherwise
     */
    public boolean isMetaDataInitialized();
}