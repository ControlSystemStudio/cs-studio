/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.client;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.SystemSettings;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.ScanServerInfo;

/** Model of scan information on scan server
 *
 *  <p>The scan server has (at this time) a simple RMI
 *  interface that only offers polling access.
 *
 *  This model periodically polls the scan server for its
 *  state and sends updates to a (GUI) listener.
 *
 *  <p>Singleton to allow multiple views to monitor
 *  the scan server by using a single underlying
 *  network connection and poll thread.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanInfoModel
{
    /** Singleton instance */
    private static ScanInfoModel instance;

    /** Reference count */
    private int references = 0;

    /** Connection to remote scan server.
     *  Maintained by the <code>poller</code> thread.
     */
    private ScanServer server = null;

    /** Thread that polls the <code>server</code>.
     *  Set to <code>null</code> to stop.
     */
    private volatile Thread poller;

    /** Most recent infos from <code>server</code> */
    private volatile List<ScanInfo> infos = Collections.emptyList();

    /** Most recent server info from <code>server</code> */
    private volatile ScanServerInfo server_info = null;

    /** Are we currently connected? */
    private volatile boolean is_connected = false;

    /** Listeners */
    private List<ScanInfoModelListener> listeners = new CopyOnWriteArrayList<ScanInfoModelListener>();


    /** Obtain reference to the singleton instance.
     *  Must release when no longer used.
     *  @return {@link ScanInfoModel}
     *  @throws Exception on error creating the initial instance
     *  @see #release()
     */
    public static ScanInfoModel getInstance() throws Exception
    {
        synchronized (ScanInfoModel.class)
        {
            if (instance == null)
            {
                setInstance(new ScanInfoModel());
                instance.start();
            }
            ++instance.references;
            return instance;
        }
    }

    /** Set static (singleton) instance from static method to please FindBugs */
    private static void setInstance(final ScanInfoModel model)
    {
        instance = model;
    }

    /** Release
     *  When last reference to the model has been released,
     *  singleton instance is stopped and removed.
     */
    public void release()
    {
        synchronized (ScanInfoModel.class)
        {
            --references;
            if (references > 0)
                return;
            setInstance(null);
        }
        stop();
    }

    /** Prevent instantiation
     *  @see #getInstance()
     */
    private ScanInfoModel()
    {
        // NOP
    }

    /** @param listener Listener to add */
    public void addListener(final ScanInfoModelListener listener)
    {
        listeners.add(listener);
        // Initial update
        if (is_connected)
            listener.scanUpdate(getInfos());
        else
            listener.connectionError();
    }

    /** @param listener Listener to remove */
    public void removeListener(final ScanInfoModelListener listener)
    {
        listeners.remove(listener);
    }

    /** Start model, i.e. connect to server, poll, ... */
    private void start() throws Exception
    {
    	final long poll_period = ScanSystemPreferences.getScanClientPollPeriod();
        poller = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // Attempt initial connection.
                // Poll() loop will then handle errors and
                // re-connects
                try
                {
                    reconnect();
                }
                catch (Exception ex)
                {
                    // Ignore
                }
                while (poller != null)
                {
                    try
                    {
                        poll();
                        Thread.sleep(poll_period);
                    }
                    catch (InterruptedException ex)
                    {
                        Logger.getLogger(ScanInfoModel.class.getName()).
                            log(Level.WARNING, "Scan Server Poll thread error", ex);
                        return;
                    }
                }
            }
        }, "Scan Server Poll");
        poller.setDaemon(true);
        poller.start();
    }

    /** Stop model, i.e. disconnect, stop polling */
    private void stop()
    {
        poller = null;
    }

    /** (Re-) connect to the server
     *  @throws Exception on error
     */
    private void reconnect() throws Exception
    {
    	// Only briefly synchronize.
    	// Connection can take a long time, so do that outside of the sync block.
    	final ScanServer old_server;
    	synchronized (this)
        {
	        old_server = server;
	        server = null;
        }
        if (old_server != null)
            ScanServerConnector.disconnect(old_server);
        // Connect to server
        final ScanServer new_server = ScanServerConnector.connect();
        synchronized (this)
        {
	        server = new_server;
        }
    }

    /** @return Server
     *  @throws RemoteException when not connected to server
     */
    public synchronized ScanServer getServer() throws RemoteException
    {
        if (server == null)
            throw new RemoteException(
        		"Not connected to Scan Server " +
        				SystemSettings.getServerHost() +
        				":" + SystemSettings.getServerPort());
        return server;
    }

    /** Poll the server for info
     *  @throws InterruptedException
     */
    private void poll() throws InterruptedException
    {
        try
        {
            final ScanServer current_server = getServer();
            // General server info, always inform listeners
            server_info = current_server.getInfo();
            for (ScanInfoModelListener listener : listeners)
                listener.scanServerUpdate(server_info);

            // List of scans. Suppress updates if there is no change
			final List<ScanInfo> update = current_server.getScanInfos();
            if (update.equals(infos) && is_connected)
                return;

            // Received new information, remember and notify listeners
            is_connected = true;
            infos = update;
            for (ScanInfoModelListener listener : listeners)
                listener.scanUpdate(infos);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Cannot poll ScanServer", ex);
            infos = Collections.emptyList();
            if (is_connected)
            {   // Notify listeners once we get into the error state
                is_connected = false;
                for (ScanInfoModelListener listener : listeners)
                    listener.connectionError();
            }
            // Wait a little
            Thread.sleep(1000);
            try
            {
                reconnect();
            }
            catch (Exception ex2)
            {
                // Wait a lot longer
                Thread.sleep(5000);
            }
        }
        catch (Throwable ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Cannot poll ScanServer", ex);
            infos = Collections.emptyList();
            for (ScanInfoModelListener listener : listeners)
                listener.connectionError();
        }
    }

    /** @return Scan Server info or <code>null</code> */
    public ScanServerInfo getServerInfo()
    {
    	return server_info;
    }

    /** @return Scan Server info
	 *  @throws RemoteException on error in remote access
     */
    public String getServerInfoText() throws RemoteException
    {
    	final StringBuilder buf = new StringBuilder();
    	buf.append(server_info).append("\n");
    	buf.append("\n");
    	buf.append("Devices:\n");
    	final DeviceInfo[] devices = getServer().getDeviceInfos(-1);
    	for (DeviceInfo device : devices)
    		buf.append(device).append("\n");
    	return buf.toString();
    }

	/** @return Most recent infos obtained from server */
    public List<ScanInfo> getInfos()
    {
        return infos;
    }
}
