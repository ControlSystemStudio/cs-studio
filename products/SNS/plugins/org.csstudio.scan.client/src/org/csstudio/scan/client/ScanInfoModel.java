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

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.server.DeviceInfo;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;

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
        listener.scanUpdate(getInfos());
    }

    /** @param listener Listener to remove */
    public void removeListener(final ScanInfoModelListener listener)
    {
        listeners.remove(listener);
    }
    
    /** Start model, i.e. connect to server, poll, ... */
    private void start() throws Exception
    {
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
                        Thread.sleep(1000);
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
    private synchronized void reconnect() throws Exception
    {
        if (server != null)
        {
            ScanServerConnector.disconnect(server);
            server = null;
        }
        // Connect to server
        server = ScanServerConnector.connect();
    }

    /** @return Server
     *  @throws RemoteException when not connected to server
     */
    private synchronized ScanServer getServer() throws RemoteException
    {
        if (server == null)
            throw new RemoteException("Not connected to Scan Server");
        return server;
    }

    /** Poll the server for info
     * @throws InterruptedException */
    private void poll() throws InterruptedException
    {
        try
        {
            final List<ScanInfo> update = getServer().getScanInfos();
            if (update.equals(infos))
                return;
            // Received new information, remember and notify listeners
            infos = update;
            for (ScanInfoModelListener listener : listeners)
                listener.scanUpdate(infos);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Cannot poll ScanServer", ex);
            infos = Collections.emptyList();
            for (ScanInfoModelListener listener : listeners)
                listener.connectionError();
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

    /** @return Scan Server info
	 *  @throws RemoteException on error in remote access
     */
    public String getServerInfo() throws RemoteException
    {
    	final StringBuilder buf = new StringBuilder();
    	buf.append(getServer().getInfo()).append("\n");
    	buf.append("\n");
    	buf.append("Devices:\n");
    	final DeviceInfo[] devices = getServer().getDeviceInfos();
    	for (DeviceInfo device : devices)
    		buf.append(device).append("\n");
    	return buf.toString();
    }

	/** @return Most recent infos obtained from server */
    public List<ScanInfo> getInfos()
    {
        return infos;
    }

    /** @param info Scan for which to get data
     *  @return ScanData or null
     */
    public ScanData getScanData(final ScanInfo info) throws RemoteException
    {
        if (info == null)
            return null;
        return getServer().getScanData(info.getId());
    }

    /** @param info Scan to pause (NOP if not running)
     *  @throws RemoteException on error in remote access
     */
    public void pause(final ScanInfo info) throws RemoteException
    {
        if (info == null)
            getServer().pause(-1);
        else
            getServer().pause(info.getId());
    }

    /** @param info Scan to resume (NOP if not paused)
     *  @throws RemoteException on error in remote access
     */
    public void resume(final ScanInfo info) throws RemoteException
    {
        if (info == null)
            getServer().resume(-1);
        else
            getServer().resume(info.getId());
    }

    /** @param info Scan to abort (NOP if not running or paused)
     *  @throws RemoteException on error in remote access
     */
    public void abort(final ScanInfo info) throws RemoteException
    {
        getServer().abort(info.getId());
    }

    /** @param info Scan to remove (NOP if has not ended)
     *  @throws RemoteException on error in remote access
     */
    public void remove(final ScanInfo info) throws RemoteException
    {
        getServer().remove(info.getId());
    }

    /** Remove completed scans (NOP if there aren't any)
     *  @throws RemoteException on error in remote access
     */
    public void removeCompletedScans() throws RemoteException
    {
        getServer().removeCompletedScans();
    }
}
