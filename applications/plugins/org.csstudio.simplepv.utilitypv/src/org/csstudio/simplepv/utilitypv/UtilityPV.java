/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.simplepv.utilitypv;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.logging.Level;

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IEnumeratedValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IStringValue;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.simplepv.ExceptionHandler;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.osgi.util.NLS;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ArrayLong;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.Time;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

/**An implementation of {@link IPV} on top of UtlityPV.
 * @author Xihui Chen
 *
 */
public class UtilityPV implements IPV {

    private String name;
    private boolean readOnly;
    private Executor notificationThread;
    private ExceptionHandler exceptionHandler;

    private PV pv;

    private Map<IPVListener, PVListener> listenerMap;
    private IValue lastIValue;
    private VType lastVTypeValue;

    final private static Map<Integer, NumberFormat> fmt_cache =
            new HashMap<Integer, NumberFormat>();


    public UtilityPV(String name, boolean readOnly,
            Executor notificationThread, ExceptionHandler exceptionHandler) throws Exception {
        this.name = convertPMPVToUtilityPVName(name);
        this.readOnly = readOnly;
        this.notificationThread = notificationThread;
        this.exceptionHandler = exceptionHandler;
        pv = PVFactory.createPV(this.name);
        listenerMap = new LinkedHashMap<IPVListener, PVListener>(4);
    }

    @Override
    public void addListener(final IPVListener listener) {

        PVListener pvListener = new PVListener() {
            private Boolean isWriteAllowed;
            private boolean connected;

            @Override
            public void pvValueUpdate(final PV pv) {
                notificationThread.execute( new Runnable() {

                    @Override
                    public void run() {
                        if(!connected){
                            connected = true;
                            listener.connectionChanged(UtilityPV.this);
                        }
                        if(isWriteAllowed == null ||
                                (!readOnly && pv.isWriteAllowed() != isWriteAllowed)){
                            listener.writePermissionChanged(UtilityPV.this);
                            isWriteAllowed = pv.isWriteAllowed();
                        }
                        listener.valueChanged(UtilityPV.this);
                    }
                });

            }

            @Override
            public void pvDisconnected(PV pv) {
                notificationThread.execute(new Runnable() {

                    @Override
                    public void run() {
                        connected =false;
                        listener.connectionChanged(UtilityPV.this);
                    }
                });

            }
        };
        listenerMap.put(listener, pvListener);
        pv.addListener(pvListener);
    }

    @Override
    public List<VType> getAllBufferedValues() {
        return Arrays.asList(getValue());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public synchronized VType getValue() {
        IValue iValue = pv.getValue();
        if(iValue!=null){
            if(iValue == lastIValue)
                return lastVTypeValue;
            lastVTypeValue = iValueToVType(iValue);
            lastIValue = iValue;
            return lastVTypeValue;
        }
        return null;
    }

    private VType iValueToVType(IValue iValue) {
        Alarm alarm = ValueFactory.newAlarm(convertSeverity(iValue.getSeverity()), iValue.getStatus());
        Time time = convertTimeStamp(iValue.getTime());
        Display display =null;
        if(iValue.getMetaData() instanceof INumericMetaData)
            display = convertNumericMetaToDisplay((INumericMetaData) iValue.getMetaData());

        if(iValue instanceof IDoubleValue){
            double[] values = ((IDoubleValue)iValue).getValues();
            if(values.length >1)
                return ValueFactory.newVDoubleArray(new ArrayDouble(values), alarm, time, display);
            return ValueFactory.newVDouble(
                    ((IDoubleValue)iValue).getValue(), alarm, time, display);
        }
        if(iValue instanceof ILongValue){
            final long[] values = ((ILongValue)iValue).getValues();
            if(values.length > 1)
                return ValueFactory.newVLongArray(new ArrayLong(values), alarm, time, display);
            return ValueFactory.newVDouble(
                    (double) ((ILongValue)iValue).getValue(), alarm, time, display);
        }
        if(iValue instanceof IEnumeratedValue){
            int[] values = ((IEnumeratedValue)iValue).getValues();
            List<String> lables = Arrays.asList(((IEnumeratedMetaData)iValue.getMetaData()).getStates());
            if(values.length>1)
                return ValueFactory.newVEnumArray(new ArrayInt(values), lables, alarm, time);
            return ValueFactory.newVEnum(((IEnumeratedValue)iValue).getValue(), lables, alarm, time);
        }
        if(iValue instanceof IStringValue){
            String[] values = ((IStringValue)iValue).getValues();
            if(values.length>1)
                return ValueFactory.newVStringArray(Arrays.asList(values), alarm, time);
            return ValueFactory.newVString(((IStringValue)iValue).getValue(), alarm, time);

        }
        return null;
    }

    private static AlarmSeverity convertSeverity(ISeverity severity){
        if(severity.isOK())
            return AlarmSeverity.NONE;
        if(severity.isMajor())
            return AlarmSeverity.MAJOR;
        if(severity.isMinor())
            return AlarmSeverity.MINOR;
        if(severity.isInvalid())
            return AlarmSeverity.INVALID;
        return AlarmSeverity.UNDEFINED;
    }

    private static Time convertTimeStamp(ITimestamp time){
        return ValueFactory.newTime(Timestamp.of(time.seconds(), (int) time.nanoseconds()));
    }

    private static Display convertNumericMetaToDisplay(INumericMetaData meta){
        int precision = meta.getPrecision();
        NumberFormat fmt = fmt_cache.get(-precision);
        if (fmt == null)
        {    fmt = new DecimalFormat("0"); //$NON-NLS-1$
            fmt.setMinimumFractionDigits(precision);
            fmt.setMaximumFractionDigits(precision);
            fmt_cache.put(-precision, fmt);
        }
        return ValueFactory.newDisplay(
                meta.getDisplayLow(), meta.getAlarmLow(), meta.getWarnLow(), meta.getUnits(),
                fmt, meta.getWarnHigh(), meta.getAlarmHigh(), meta.getDisplayHigh(),
                meta.getDisplayLow(),meta.getDisplayHigh());
    }


    @Override
    public boolean isBufferingValues() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return pv.isConnected();
    }

    @Override
    public boolean isPaused() {
        return !pv.isRunning();
    }

    @Override
    public boolean isStarted() {
        return pv.isRunning();
    }

    @Override
    public boolean isWriteAllowed() {
        if(readOnly)
            return false;
        return pv.isWriteAllowed();
    }

    @Override
    public void removeListener(IPVListener listener) {
        pv.removeListener(listenerMap.get(listener));
        listenerMap.remove(listener);
    }

    @Override
    public void setPaused(boolean paused) {
        if(paused && pv.isRunning())
            pv.stop();
        if(!paused && !pv.isRunning())
            try {
                pv.start();
            } catch (Exception e) {
                fireExceptionChanged(e);
            }
    }

    private void fireExceptionChanged(Exception e){
        if(exceptionHandler!=null)
            exceptionHandler.handleException(e);
        for(IPVListener listener : listenerMap.keySet()){
            listener.exceptionOccurred(this, e);
        }
    }

    @Override
    public void setValue(Object value) throws Exception {
        if(!readOnly)
            pv.setValue(value);
        else
            throw new Exception(NLS.bind("The PV {0} was created for read only.", getName()));
    }

    @Override
    public boolean setValue(Object value, int timeout) throws Exception {
        if(!pv.isRunning())
            pv.start();
        long startTime = System.currentTimeMillis();
        while ((Calendar.getInstance().getTimeInMillis() - startTime) < timeout
                && !pv.isConnected()) {
            Thread.sleep(100);
        }

        if (!pv.isConnected()) {
            throw new Exception(
                    NLS.bind("Connection Timeout! Failed to connect to the PV {0}.", name));
        }
        if (!pv.isWriteAllowed())
            throw new Exception("The PV is not allowed to write");
        pv.setValue(value);
        return true;
    }

    @Override
    public void start() throws Exception {
        if(pv.isRunning())
            throw new IllegalStateException(
                    NLS.bind("PV {0} has already been started.", getName()));
        pv.start();
    }

    @Override
    public void stop() {
        if(!pv.isRunning()){
            Activator.getLogger().log(Level.WARNING,
                    NLS.bind("PV {0} has already been stopped or was not started yet.", getName()));
            return;
        }
        pv.stop();
    }

    /**Convert PVManager PV name to Utility PV name if the pv name is supported by Utility pv.
     * @param pvName
     * @return the converted name.
     */
    public static String convertPMPVToUtilityPVName(String pvName){
        //convert =123 to 123
        //conver ="fred" to "fred'
        if(pvName.startsWith("=")){//$NON-NLS-1$
            return pvName.substring(1);
        }

        //convert sim://const(1.23, 34, 34) to const://array(1.23, 34, 34)
        if(pvName.startsWith("sim://const")){ //$NON-NLS-1$
            return pvName.replace("sim://const", "const://array"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return pvName;

    }

}
