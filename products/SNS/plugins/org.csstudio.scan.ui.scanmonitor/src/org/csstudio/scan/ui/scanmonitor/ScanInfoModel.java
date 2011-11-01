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
package org.csstudio.scan.ui.scanmonitor;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.client.ScanServerConnector;
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
 *  @author Kay Kasemir
 */
public class ScanInfoModel
{
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

    /** @param listener Listener to add */
    public void addListener(final ScanInfoModelListener listener)
    {
        listeners.add(listener);
    }

    /** Start model, i.e. connect to server, poll, ... */
    public void start() throws Exception
    {
        poller = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
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
        poller.start();
    }

    /** Stop model, i.e. disconnect, stop polling */
    public void stop()
    {
        poller = null;
    }

    /** (Re-) connect to the server
     *  @throws Exception on error
     */
    private synchronized void reconnect() throws Exception
    {
        // There really is no 'disconnect', but put that here
        // in case another implementation offers explicit disconnect...
        if (server != null)
            server = null;
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
            infos = getServer().getScanInfos();

            // TODO Check for changes?
//            System.out.println("----------------");
//            for (ScanInfo info : infos)
//            {
//                System.out.println(info);
//            }
            for (ScanInfoModelListener listener : listeners)
                listener.scanUpdate(infos);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Cannot poll ScanServer", ex);
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
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.FINE, "Cannot poll ScanServer", ex);
            for (ScanInfoModelListener listener : listeners)
                listener.connectionError();
        }
    }

    /** @return Most recent infos obtained from server */
    public List<ScanInfo> getInfos()
    {
        return infos;
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
