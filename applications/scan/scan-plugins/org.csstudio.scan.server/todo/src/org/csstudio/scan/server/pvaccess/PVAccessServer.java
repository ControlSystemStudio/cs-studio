package org.csstudio.scan.server.pvaccess;

import org.csstudio.scan.server.internal.ScanServerImpl;
import org.epics.pvaccess.server.impl.remote.ServerContextImpl;
import org.epics.pvaccess.server.impl.remote.plugins.DefaultBeaconServerDataProvider;

/**
 * Created with IntelliJ IDEA.
 * User: berryman
 * Date: 5/20/13
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class PVAccessServer {

    private static ServerContextImpl serverContext;
    private final ScanServerImpl scan_server;


    public PVAccessServer(ScanServerImpl scan_server) {
        super();
        this.scan_server = scan_server;
    }

    public synchronized void initializeServerContext()
    {
        // already initialized
        if (serverContext != null)
            return;

        // Create a context with default configuration values.
                 /*final ServerContextImpl*/ serverContext = new ServerContextImpl();
        serverContext.setBeaconServerStatusProvider(new DefaultBeaconServerDataProvider(serverContext));

        try {
            serverContext.initialize(new ChannelProviderImpl(scan_server));
        } catch (Throwable th) {
            th.printStackTrace();
        }

        // Display basic information about the context.
        System.out.println(serverContext.getVersion().getVersionString());
        serverContext.printInfo(); System.out.println();

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    System.out.println("Running server...");
                    serverContext.run(0);
                    System.out.println("Done.");
                } catch (Throwable th) {
                    System.out.println("Failure:");
                    th.printStackTrace();
                }
            }
        }, "pvAccess server").start();
    }

    public synchronized void destroyServerContext()
    {
        // not yet initialized
        if (serverContext == null)
            return;

        serverContext.dispose();

        serverContext = null;
    }

}
