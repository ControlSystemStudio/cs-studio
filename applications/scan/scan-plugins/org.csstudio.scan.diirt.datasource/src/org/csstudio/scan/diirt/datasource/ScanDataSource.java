/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.scan.diirt.datasource;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.csstudio.scan.client.ScanClient;
import org.diirt.datasource.ChannelHandler;
import org.diirt.datasource.DataSource;
import org.diirt.datasource.vtype.DataTypeSupport;
import static org.diirt.util.concurrent.Executors.namedPool;

/**
 * @author Eric Berryman
 *
 */
public class ScanDataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    public enum REQUEST_TYPE {
        SCAN_INFO, SERVER_INFO, SCAN_DATA, SCAN_DEVICES
    }

    private static final Logger log = Logger.getLogger(ScanDataSource.class.getName());
    private static final AtomicInteger counter = new AtomicInteger();
    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(namedPool("diirt scan " + counter.getAndIncrement() + " worker "));
    private final ScanDataSourceConfiguration configuration;
    private final Map<String, ScanClient> scanSources = new ConcurrentHashMap<>();
    private final Pattern scanInfoPath;
    private final Pattern dataPath;
    private final Pattern devicesPath;

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    public ScanDataSource(ScanDataSourceConfiguration configuration) {
        super(false);
        this.configuration = configuration;
        exec.scheduleWithFixedDelay(this::poll, configuration.pollInterval, configuration.pollInterval, TimeUnit.SECONDS);
        scanInfoPath = Pattern.compile("\\/-?([0-9]+)\\/?");
        dataPath = Pattern.compile("\\/-?([0-9]+)\\/data");
        devicesPath = Pattern.compile("\\/-?([0-9]+)\\/devices");

    }

    @Override
    public boolean isWriteable() {
        return true;
    }

    @Override
    public void close() {
        exec.shutdownNow();
        super.close();
    }

    private void poll() {
        for (ChannelHandler channel : getChannels().values()) {
            ((ScanChannelHandler)channel).poll();
        }
    }

    @Override
    protected ChannelHandler createChannel(String channelName) {
        try {
            // adding a protocol to simplify parsing
            URI uri = new URI("http://"+channelName);

            if(configuration.connections.containsKey(uri.getHost())) {
                if( uri.getPath().isEmpty() ){
                    log.fine("Creating Channel: "+channelName);
                    // scan://server?max=1  Server Info (VTable)
                    return new ScanChannelHandler(this,uri, REQUEST_TYPE.SERVER_INFO);
                }
                Matcher dataPathMatcher = dataPath.matcher(uri.getPath());
                if(dataPathMatcher.matches()) {
                    // scan://server/1/data  Scan Data (VTable)
                    Long id = Long.valueOf(dataPathMatcher.group(1));
                    log.fine("Creating Channel: "+channelName+" with id: "+String.valueOf(id));
                    return new ScanChannelHandler(this,uri,id, REQUEST_TYPE.SCAN_DATA);
                }
                Matcher devicesPathMatcher = devicesPath.matcher(uri.getPath());
                if(devicesPathMatcher.matches()) {
                    // scan://server/1/devices  Devices used in scan (VTable)
                    Long id = Long.valueOf(devicesPathMatcher.group(1));
                    log.fine("Creating Channel: "+channelName+" with id: "+String.valueOf(id));
                    return new ScanChannelHandler(this,uri,id, REQUEST_TYPE.SCAN_DEVICES);
                }
                Matcher scanInfoPathMatcher = scanInfoPath.matcher(uri.getPath());
                if(scanInfoPathMatcher.matches()) {
                    // scan://server/1/ or scan://server/1  Scan Info (VTable)
                    Long id = Long.valueOf(scanInfoPathMatcher.group(1));
                    log.fine("Creating Channel: "+channelName+" with id: "+String.valueOf(id));
                    return new ScanChannelHandler(this,uri,id, REQUEST_TYPE.SCAN_INFO);
                }

                throw new RuntimeException("Malformed URI scan channel named " + channelName);
            }
            throw new RuntimeException("Couldn't find scan channel named " + channelName);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Malformed URI scan channel named " + channelName);
        }


    }

    ScanClient getConnection(String connectionName) {
        ScanClient scanSource = scanSources.get(connectionName);
        if (scanSource == null) {
            scanSource = createScanClientSource(configuration.connections.get(connectionName));
            if (scanSource == null) {
                throw new RuntimeException("Source for " + connectionName + " cannot be created");
            }
            scanSources.put(connectionName, scanSource);
        }
        return scanSource;
    }

    private ScanClient createScanClientSource(URL url) {
        return new ScanClient(url.getHost(), url.getPort());
    }
}
