/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.scan.diirt.datasource;

import static org.diirt.vtype.ValueFactory.alarmNone;
import static org.diirt.vtype.ValueFactory.newVStringArray;
import static org.diirt.vtype.ValueFactory.timeNow;
import static org.diirt.vtype.table.VTableFactory.column;
import static org.diirt.vtype.table.VTableFactory.newVTable;

import java.io.StringWriter;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.diirt.util.array.ArrayInt;
import org.diirt.vtype.VTable;
import org.diirt.vtype.table.Column;
import org.w3c.dom.Document;

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
        super(uri.toASCIIString().replaceFirst("^http://", ""));
        this.datasource = datasource;
        this.uri = uri;
        this.id = id;
        this.requestType = requestType;
        this.queryMap.putAll(getQueryMap(uri.getQuery()));
    }

    ScanChannelHandler(ScanDataSource datasource, URI uri, REQUEST_TYPE requestType) {
        super(uri.toASCIIString().replaceFirst("^http://", ""));
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
        processConnection(null);
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
            } else if (newValue instanceof Document && requestType == REQUEST_TYPE.SERVER_INFO) {
                submit(datasource.getConnection(uri.getHost()), (Document)newValue);
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
    private volatile VTable oldVTable = null;
    private static final Random random = new Random();

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
                if (shouldReadData(pollResult, newPollResult) || requestType == REQUEST_TYPE.SERVER_INFO) {
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
                            if (oldVTable == null){
                                processMessage(newServerInfo);
                                oldVTable = newServerInfo;
                            } else if (shouldReadData(((ArrayInt)(oldVTable.getColumnData(0))), ((ArrayInt)(newServerInfo.getColumnData(0)))) ||
                                    shouldReadData(pollResult, newPollResult) ) {
                                processMessage(newServerInfo);
                                oldVTable = newServerInfo;
                            }
                            break;
                        default:
                            break;
                    }
                    pollResult = newPollResult;
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
        if(requestType == REQUEST_TYPE.SERVER_INFO) {
            long runningCount = scanClient.getScanInfos().stream().filter(info -> info.getState()==ScanState.Running).count();
            long pausedCount = scanClient.getScanInfos().stream().filter(info -> info.getState()==ScanState.Paused).count();
            if(runningCount > 0 || pausedCount > 0){
                return random.nextInt();
            } else {
                return NO_POLL_DATA;
            }
        } else {
            long serial = scanClient.getLastScanDataSerial(id);
            if (serial == -1) {
                return NO_POLL_DATA;
            } else {
                return serial;
            }
        }
    }

    // This is awful
    final List<Class<?>> types_data = new ArrayList<Class<?>>();
    final List<String> names_data = new ArrayList<String>();
    final List<Object> values_data = new ArrayList<Object>();
    private VTable executeDataQuery(ScanClient scanClient) throws Exception{
        ScanData scanData = scanClient.getScanData(id);
        //final Comparator<ScanSample> comp = (p1, p2) -> p1.getTimestamp().compareTo(p2.getTimestamp());;
        types_data.clear();
        names_data.clear();
        values_data.clear();
        if(scanData.getDevices().length>0) {
            types_data.add(Instant.class);
            names_data.add("timestamp");
            List<Instant> timestamps = scanData.getSamples(scanData.getDevices()[0]).stream().map(sample-> sample.getTimestamp()).collect(Collectors.toList());
            values_data.add(timestamps);
        }
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
            types_data.add(Double.TYPE);
            names_data.add(device);
            values_data.add(new ArrayDouble(sampleData));
        }
        return org.diirt.vtype.ValueFactory.newVTable(types_data, names_data, values_data);
    }

    final List<Column> columns_device = new ArrayList<Column>();
    private VTable executeDevicesQuery(ScanClient scanClient) throws Exception{
        Collection<DeviceInfo> scanDevices = scanClient.getScanDevices(id);
        columns_device.clear();

        List<String> names = scanDevices.stream().map(deviceInfo -> deviceInfo.getName()).collect(Collectors.toList());
        columns_device.add(column("name", newVStringArray(names, alarmNone(),timeNow())));

        List<String> aliases = scanDevices.stream().map(deviceInfo -> deviceInfo.getAlias()).collect(Collectors.toList());
        columns_device.add(column("alias", newVStringArray(aliases, alarmNone(),timeNow())));

        List<String> status = scanDevices.stream().map(deviceInfo -> deviceInfo.getStatus()).collect(Collectors.toList());
        columns_device.add(column("status", newVStringArray(status, alarmNone(),timeNow())));

        return newVTable(columns_device.toArray(new Column[columns_device.size()]));
    }

    final List<Class<?>> types_info = new ArrayList<Class<?>>();
    final List<String> names_info = new ArrayList<String>();
    final List<Object> values_info = new ArrayList<Object>();
    private VTable executeScanInfoQuery(ScanClient scanClient) throws Exception{
        ScanInfo scanInfo = scanClient.getScanInfo(id);

        types_info.clear();
        names_info.clear();
        values_info.clear();

        // id - possible overflow from long
        types_info.add(Integer.TYPE);
        names_info.add(Messages.Id);
        values_info.add(new ArrayInt((int)scanInfo.getId()));

        // created
        types_info.add(Instant.class);
        names_info.add(Messages.Created);
        values_info.add(Arrays.asList(scanInfo.getCreated()));

        // name
        types_info.add(String.class);
        names_info.add(Messages.Name);
        values_info.add(Arrays.asList(scanInfo.getName()));

        // currentCommand
        types_info.add(String.class);
        names_info.add(Messages.CurrentCommand);
        values_info.add(Arrays.asList(scanInfo.getCurrentCommand()));

        // finishTime
        types_info.add(Instant.class);
        names_info.add(Messages.FinishTime);
        values_info.add(Arrays.asList(scanInfo.getFinishTime()));

        // percentage
        types_info.add(Integer.TYPE);
        names_info.add(Messages.Percentage);
        values_info.add(new ArrayInt(scanInfo.getPercentage()));

        // state
        types_info.add(String.class);
        names_info.add(Messages.State);
        values_info.add(Arrays.asList(scanInfo.getState().toString()));

        // error
        types_info.add(String.class);
        names_info.add(Messages.Error);
        values_info.add(Arrays.asList(scanInfo.getError().isPresent()?scanInfo.getError().get():""));

        return org.diirt.vtype.ValueFactory.newVTable(types_info, names_info, values_info);
    }

    final List<Class<?>> types_serverinfo = new ArrayList<Class<?>>();
    final List<String> names_serverinfo = new ArrayList<String>();
    final List<Object> values_serverinfo = new ArrayList<Object>();
    private VTable executeServerInfoQuery(ScanClient scanClient) throws Exception{
        List<ScanInfo> scanInfos = scanClient.getScanInfos();

        int maxSize = Integer.parseInt(queryMap.getOrDefault("max", "1000"));

        types_serverinfo.clear();
        names_serverinfo.clear();
        values_serverinfo.clear();

        // id - possible overflow from long
        types_serverinfo.add(Integer.TYPE);
        names_serverinfo.add(Messages.Id);
        int[] scanIds = scanInfos.stream().limit(maxSize).map(scanInfo -> (int)scanInfo.getId()).mapToInt(i -> i).toArray();
        values_serverinfo.add(new ArrayInt(scanIds));

        // created
        types_serverinfo.add(Instant.class);
        names_serverinfo.add(Messages.Created);
        List<Instant> createds = scanInfos.stream().limit(maxSize).map(scanInfo -> scanInfo.getCreated()).collect(Collectors.toList());
        values_serverinfo.add(createds);

        // name
        types_serverinfo.add(String.class);
        names_serverinfo.add(Messages.Name);
        List<String> scan_names = scanInfos.stream().limit(maxSize).map(scanInfo -> scanInfo.getName()).collect(Collectors.toList());
        values_serverinfo.add(scan_names);

        // currentCommand
        types_serverinfo.add(String.class);
        names_serverinfo.add(Messages.CurrentCommand);
        List<String> currentCommands = scanInfos.stream().limit(maxSize).map(scanInfo -> scanInfo.getCurrentCommand()).collect(Collectors.toList());
        values_serverinfo.add(currentCommands);

        // finishTime
        types_serverinfo.add(Instant.class);
        names_serverinfo.add(Messages.FinishTime);
        List<Instant> finishTimes = scanInfos.stream().limit(maxSize).map(scanInfo -> scanInfo.getFinishTime()).collect(Collectors.toList());
        values_serverinfo.add(finishTimes);

        // percentage
        types_serverinfo.add(Integer.TYPE);
        names_serverinfo.add(Messages.Percentage);
        int[] percentages = scanInfos.stream().limit(maxSize).map(scanInfo -> scanInfo.getPercentage()).mapToInt(i -> i).toArray();;
        values_serverinfo.add(new ArrayInt(percentages));

        // state
        types_serverinfo.add(String.class);
        names_serverinfo.add(Messages.State);
        List<String> states = scanInfos.stream().limit(maxSize).map(scanInfo -> scanInfo.getState().toString()).collect(Collectors.toList());
        values_serverinfo.add(states);

        // error
        types_serverinfo.add(String.class);
        names_serverinfo.add(Messages.Error);
        List<String> errors = scanInfos.stream().limit(maxSize).map(scanInfo -> scanInfo.getError().isPresent()?scanInfo.getError().get():"").collect(Collectors.toList());
        values_serverinfo.add(errors);

        return org.diirt.vtype.ValueFactory.newVTable(types_serverinfo, names_serverinfo, values_serverinfo);
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

    private void submit (ScanClient scanClient, Document document) throws Exception {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(document), new StreamResult(sw));
            scanClient.submitScan(document.getDocumentURI(), sw.toString(), true);
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }
}