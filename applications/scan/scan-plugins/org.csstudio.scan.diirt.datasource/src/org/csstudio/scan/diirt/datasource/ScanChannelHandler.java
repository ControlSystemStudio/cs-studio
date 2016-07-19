/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.scan.diirt.datasource;

import static org.diirt.vtype.ValueFactory.alarmNone;
import static org.diirt.vtype.ValueFactory.displayNone;
import static org.diirt.vtype.ValueFactory.newVDoubleArray;
import static org.diirt.vtype.ValueFactory.newVStringArray;
import static org.diirt.vtype.ValueFactory.timeNow;
import static org.diirt.vtype.table.VTableFactory.column;
import static org.diirt.vtype.table.VTableFactory.newVTable;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.csstudio.java.time.TimestampFormats;
import org.csstudio.scan.client.ScanClient;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFormatter;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.diirt.datasource.ScanDataSource.REQUEST_TYPE;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanState;
import org.diirt.datasource.ChannelWriteCallback;
import org.diirt.datasource.MultiplexedChannelHandler;
import org.diirt.util.array.ArrayDouble;
import org.diirt.vtype.VTable;
import org.diirt.vtype.table.Column;

/**
 * Implementation for channels of a {@link ScanDataSource}.
 *
 * @author Eric Berryman
 */
class ScanChannelHandler extends MultiplexedChannelHandler<ScanChannelHandler.ConnectionPayload, Object> {
    
    private final ScanDataSource datasource;
    private final URI uri;
    private final Map<String,String> queryMap = new ConcurrentHashMap<String,String>(); 
    private final Long id;
    private final REQUEST_TYPE requestType;
    
    ScanChannelHandler(ScanDataSource datasource, URI uri, Long id, REQUEST_TYPE requestType) {
        super(uri.toASCIIString());
        this.datasource = datasource;
        this.uri = uri;
        this.id = id;
        this.requestType = requestType;
        this.queryMap.putAll(getQueryMap(uri.getQuery()));
    }
    
    ScanChannelHandler(ScanDataSource datasource, URI uri, REQUEST_TYPE requestType) {
        super(uri.toASCIIString());
        this.datasource = datasource;
        this.uri = uri;
        this.id = null;
        this.requestType = requestType;
        this.queryMap.putAll(getQueryMap(uri.getQuery()));
    }
    
    @Override
    public void connect() {
        poll();
    }

    @Override
    public void disconnect() {
        pollResult = null;
    }

    @Override
    protected boolean isConnected(ScanChannelHandler.ConnectionPayload payload) {
        return payload != null && payload.connected;
    }
    
    @Override
    protected boolean isWriteConnected(ScanChannelHandler.ConnectionPayload payload) {
        return requestType == REQUEST_TYPE.SCAN_INFO;
    }

    @Override
    protected void write(Object newValue, ChannelWriteCallback callback) {
        try {
            if (newValue instanceof String && requestType == REQUEST_TYPE.SCAN_INFO) {
                switch ((String) newValue) {
                case "pause":
                    pause(datasource.getConnection(uri.getHost()));
                    break;
                case "resume":
                    resume(datasource.getConnection(uri.getHost()));
                    break;
                case "abort":
                    abort(datasource.getConnection(uri.getHost()));
                    break;
                case "remove":
                    remove(datasource.getConnection(uri.getHost()));
                    break;
                default:
                    break;
                }
            }
        } catch (Exception e) {
            callback.channelWritten(e);
        }
        callback.channelWritten(null);
    }
    
    static class ConnectionPayload {
        final boolean connected;
        final boolean pollQuerySuccessful;
        final boolean dataQuerySuccessful;

        public ConnectionPayload(boolean connected, boolean pollQuerySuccessful, boolean dataQuerySuccessful) {
            this.connected = connected;
            this.pollQuerySuccessful = pollQuerySuccessful;
            this.dataQuerySuccessful = dataQuerySuccessful;
        }
    }
    
    private static final Object NO_POLL_DATA = new Object();
    private volatile Object pollResult;
    
    void poll() {
        // Skip poll if channel is no usage on the channel
        if (getUsageCounter() > 0) {
            boolean connected = false;
            boolean pollQuerySuccessful = false;
            boolean dataQuerySuccessful = false;
            
            // Retrieve scan connection
            try {
                ScanClient scanClient = datasource.getConnection(uri.getHost());
                connected = true;
                
                // Execute the poll and compare with the old value
                Object newPollResult = executePollQuery(scanClient);
                if (shouldReadData(pollResult, newPollResult)) {
                    pollResult = newPollResult;
                    pollQuerySuccessful = true;
                    switch(requestType){
                        case SCAN_DATA:
                            VTable newData = executeDataQuery(scanClient);
                            dataQuerySuccessful = true;
                            processConnection(new ConnectionPayload(connected, pollQuerySuccessful, dataQuerySuccessful));
                            processMessage(newData);
                            break;
                        case SCAN_DEVICES:
                            VTable newDevices = executeDevicesQuery(scanClient);
                            dataQuerySuccessful = true;
                            processConnection(new ConnectionPayload(connected, pollQuerySuccessful, dataQuerySuccessful));
                            processMessage(newDevices);
                            break;
                        case SCAN_INFO:
                            VTable newScanInfo = executeScanInfoQuery(scanClient);
                            dataQuerySuccessful = true;
                            processConnection(new ConnectionPayload(connected, pollQuerySuccessful, dataQuerySuccessful));
                            processMessage(newScanInfo);
                            break;
                        case SERVER_INFO:
                            VTable newServerInfo = executeServerInfoQuery(scanClient);
                            dataQuerySuccessful = true;
                            processConnection(new ConnectionPayload(connected, pollQuerySuccessful, dataQuerySuccessful));
                            processMessage(newServerInfo);
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception ex) {
                processConnection(new ConnectionPayload(connected, pollQuerySuccessful, dataQuerySuccessful));
                reportExceptionToAllReadersAndWriters(ex);
            }
        }
    }
    
    private static Map<String, String> getQueryMap(String query) {
        if(query == null){
            return new ConcurrentHashMap<String, String>();
        }
        String[] params = query.split("&");
        Map<String, String> map = new ConcurrentHashMap<String, String>();
        for (String param : params){
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }
    
    private boolean shouldReadData(Object oldPollResult, Object newPollResult) {
        return !Objects.equals(oldPollResult, newPollResult);
    }
    
    private Object executePollQuery(ScanClient scanClient) throws Exception {
        if(requestType == REQUEST_TYPE.SERVER_INFO){
            return 1;
        } else {
            long serial = scanClient.getLastScanDataSerial(id);
            if (serial == -1) {
                return NO_POLL_DATA;
            } else {
                return serial;
            }
        }
    }
    
    private VTable executeDataQuery(ScanClient scanClient) throws Exception{
        ScanData scanData = scanClient.getScanData(id);
        List<Column> columns = new ArrayList<Column>();
        int min = 0;
        for(String device : scanData.getDevices()){
            List<ScanSample> samples = scanData.getSamples(device);
            double[] sampleData = new double[samples.size()];
            int i = 0;
            for(ScanSample sample : samples){
                sampleData[i]=ScanSampleFormatter.asDouble(sample);
                i++;
            }
            min = min==0?sampleData.length:min;
            if(min!=sampleData.length){
                sampleData = Arrays.copyOf(sampleData, min);
            }
            columns.add(column(device, newVDoubleArray(new ArrayDouble(sampleData), alarmNone(), timeNow(),displayNone())));
        }
        return newVTable(columns.toArray(new Column[columns.size()]));
    }
    
    private VTable executeDevicesQuery(ScanClient scanClient) throws Exception{
        Collection<DeviceInfo> scanDevices = scanClient.getScanDevices(id);
        List<Column> columns = new ArrayList<Column>();
        
        List<String> names = scanDevices.stream().map(deviceInfo -> deviceInfo.getName()).collect(Collectors.toList());
        columns.add(column("name", newVStringArray(names, alarmNone(),timeNow())));
        
        List<String> aliases = scanDevices.stream().map(deviceInfo -> deviceInfo.getAlias()).collect(Collectors.toList());
        columns.add(column("alias", newVStringArray(aliases, alarmNone(),timeNow())));
        
        List<String> status = scanDevices.stream().map(deviceInfo -> deviceInfo.getStatus()).collect(Collectors.toList());
        columns.add(column("status", newVStringArray(status, alarmNone(),timeNow())));
        
        return newVTable(columns.toArray(new Column[columns.size()]));
    }
    
    private VTable executeScanInfoQuery(ScanClient scanClient) throws Exception{
        ScanInfo scanInfo = scanClient.getScanInfo(id);
        List<Column> columns = new ArrayList<Column>();
        
        long scanId = scanInfo.getId();
        Instant created = scanInfo.getCreated();
        String name = scanInfo.getName();
        String currentCommand = scanInfo.getCurrentCommand();
        Instant finishTime = scanInfo.getFinishTime();
        int percentage = scanInfo.getPercentage();
        ScanState state = scanInfo.getState();
        Optional<String> error = scanInfo.getError();
        
        columns.add(column("id", newVStringArray(Arrays.asList(String.valueOf(scanId)), alarmNone(),timeNow())));
        columns.add(column("created", newVStringArray(Arrays.asList(TimestampFormats.SECONDS_FORMAT.format(created)), alarmNone(),timeNow())));
        columns.add(column("name", newVStringArray(Arrays.asList(name), alarmNone(),timeNow())));
        columns.add(column("currentCommand", newVStringArray(Arrays.asList(currentCommand), alarmNone(),timeNow())));
        columns.add(column("finishTime", newVStringArray(Arrays.asList(finishTime==null?"":TimestampFormats.SECONDS_FORMAT.format(finishTime)), alarmNone(),timeNow())));
        columns.add(column("percentage", newVStringArray(Arrays.asList(String.valueOf(percentage)), alarmNone(),timeNow())));
        columns.add(column("state", newVStringArray(Arrays.asList(state.toString()), alarmNone(),timeNow())));
        columns.add(column("error", newVStringArray(Arrays.asList(error.isPresent()?error.get():""), alarmNone(),timeNow())));
        
        return newVTable(columns.toArray(new Column[columns.size()]));
    }
    
    private VTable executeServerInfoQuery(ScanClient scanClient) throws Exception{
        List<ScanInfo> scanInfos = scanClient.getScanInfos();
        List<Column> columns = new ArrayList<Column>();
        

        int maxSize = Integer.parseInt(queryMap.getOrDefault("max", "1000"));
        
        List<String> scanIds = scanInfos.stream().limit(maxSize).map(scanInfo -> String.valueOf(scanInfo.getId())).collect(Collectors.toList());
        columns.add(column("id", newVStringArray(scanIds, alarmNone(),timeNow())));
        
        List<String> createds = scanInfos.stream().limit(maxSize).map(scanInfo -> TimestampFormats.SECONDS_FORMAT.format(scanInfo.getCreated())).collect(Collectors.toList());   
        columns.add(column("created", newVStringArray(createds, alarmNone(),timeNow())));
        
        List<String> names = scanInfos.stream().limit(maxSize).map(scanInfo -> scanInfo.getName()).collect(Collectors.toList());
        columns.add(column("name", newVStringArray(names, alarmNone(),timeNow())));
        
        List<String> currentCommands = scanInfos.stream().limit(maxSize).map(scanInfo -> scanInfo.getCurrentCommand()).collect(Collectors.toList());
        columns.add(column("currentCommand", newVStringArray(currentCommands, alarmNone(),timeNow())));
        
        List<String> finishTimes = scanInfos.stream().limit(maxSize).map(scanInfo -> scanInfo.getFinishTime()==null?"":TimestampFormats.SECONDS_FORMAT.format(scanInfo.getFinishTime())).collect(Collectors.toList());
        columns.add(column("finishTime", newVStringArray(finishTimes, alarmNone(),timeNow())));
        
        List<String> percentages = scanInfos.stream().limit(maxSize).map(scanInfo -> String.valueOf(scanInfo.getPercentage())).collect(Collectors.toList());
        columns.add(column("percentage", newVStringArray(percentages, alarmNone(),timeNow())));
        
        List<String> states = scanInfos.stream().limit(maxSize).map(scanInfo -> scanInfo.getState().toString()).collect(Collectors.toList());
        columns.add(column("state", newVStringArray(states, alarmNone(),timeNow())));
        
        List<String> errors = scanInfos.stream().limit(maxSize).map(scanInfo -> scanInfo.getError().isPresent()?scanInfo.getError().get():"").collect(Collectors.toList());
        columns.add(column("error", newVStringArray(errors, alarmNone(),timeNow())));
        
        return newVTable(columns.toArray(new Column[columns.size()]));
    }
    
    private void pause (ScanClient scanClient) throws Exception {
        scanClient.pauseScan(id);
    }
    
    private void resume (ScanClient scanClient) throws Exception {
        scanClient.resumeScan(id);
    }
    
    private void abort (ScanClient scanClient) throws Exception {
        scanClient.abortScan(id);
    }
    
    private void remove (ScanClient scanClient) throws Exception {
        scanClient.removeScan(id);
    }
}