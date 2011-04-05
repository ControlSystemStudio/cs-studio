
/* 
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 */

package org.csstudio.archive.sdds.server.sdds;

import java.util.TreeSet;
import org.csstudio.archive.sdds.server.Activator;
import org.csstudio.archive.sdds.server.conversion.SampleParameter;
import org.csstudio.archive.sdds.server.data.EpicsRecordData;
import org.csstudio.archive.sdds.server.internal.ServerPreferenceKey;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import SDDS.java.SDDS.SDDSFile;

/**
 * @author Markus Moeller
 *
 */
public class SddsDataReader implements Runnable {
    
    /** Results */
    // private Vector<EpicsRecordData> result;
    private TreeSet<EpicsRecordData> data;
    
    /** The SDDSFile object for reading the archive file */
    private SDDSFile sddsFile;

    /** Holds the parameter of a PV */
    private SampleParameter sampleParameter;
    
    /** Path of SDDS file to read */
    private String filePath;
    
    /** */
    @SuppressWarnings("unused")
	private long startTime;
    
    /** */
    @SuppressWarnings("unused")
	private long endTime;

    /**
     * 
     * @param path
     * @param startTime
     * @param endTime
     */
    public SddsDataReader(String path, long startTime, long endTime) {
        
        IPreferencesService pref = Platform.getPreferencesService();

        // Indicates if byte order is little endian
        boolean littleEndian = pref.getBoolean(Activator.PLUGIN_ID, ServerPreferenceKey.P_SDDS_LITTLE_ENDIAN, false, null);

        filePath = path;
        this.startTime = startTime;
        this.endTime = endTime;
        sddsFile = new SDDSFile();
        sampleParameter = new SampleParameter();
        sddsFile.setFileName(filePath);
        sddsFile.setEndian(littleEndian);
        // result = new Vector<EpicsRecordData>();
        data = new TreeSet<EpicsRecordData>(new TimeComperator());
    }
    
    /**
     * 
     * @return
     */
    public EpicsRecordData[] getResultAsArray() {
        
        EpicsRecordData[] r = null;
        
        if(data.isEmpty() == false) {
            r = new EpicsRecordData[data.size()];
            r = data.toArray(r);
        } else {
            r = new EpicsRecordData[0];
        }
        
        return r;
    }
    
    /**
     * 
     * @return
     */
    //public Vector<EpicsRecordData> getResult() {
    public TreeSet<EpicsRecordData> getResult() {
        return data;
    }

    /**
     * Returns the number of data sets.
     * 
     * @return
     */
    public int getResultCount() {
        return data.size();
    }
    
    /**
     * Returns the object that holds the parameter values of the PV.
     * 
     * @return
     */
    public SampleParameter getSampleCtrl() {
        return sampleParameter;
    }
    
    /**
     * 
     */
    @Override
	public void run() {
        
        @SuppressWarnings("unused")
		EpicsRecordData prevData = null;
        @SuppressWarnings("unused")
		EpicsRecordData lastData = null;
        String[] list = null;
        Object[] time = null;
        Object[] nanoSec = null;
        Object[] status = null;
        Object[] value = null;
        // int[] types = null;
        int count;
        
        sddsFile.readFile();
        
        list = sddsFile.getParameterNames();
        // types = sddsFile.getParameterTypes();
        sampleParameter = this.getParameters(list);
        
        time = sddsFile.getColumnValues(0, 1, false);
        nanoSec = sddsFile.getColumnValues(1, 1, false);
        status = sddsFile.getColumnValues(2, 1, false);
        value = sddsFile.getColumnValues(3, 1, false);
        
        count = time.length;
//        for(int i = 0;i < count;i++) {
//            if(((Long)time[i] >= startTime) && ((Long)time[i] <= endTime)) {
//                result.add(new EpicsRecordData((Long)time[i], (Long)nanoSec[i],
//                                               (Long)status[i], value[i]));
//            } else if((Long)time[i] < startTime) {
//                prevData = new EpicsRecordData((Long)time[i], (Long)nanoSec[i],
//                                               (Long)status[i], value[i]);
//            } else if(((Long)time[i] > endTime) && (lastData == null)) {
//                lastData = new EpicsRecordData((Long)time[i], (Long)nanoSec[i],
//                                               (Long)status[i], value[i]);
//                break;
//            }
//        }
        
        for(int i = 0;i < count;i++) {
            data.add(new EpicsRecordData((Long)time[i], (Long)nanoSec[i],
                                         (Long)status[i], value[i]));
        }
        
//        if(result.isEmpty()) {
//            
//            if(prevData != null) {
//                result.add(prevData);
//            }
//            
//            if(lastData != null) {
//                result.add(lastData);
//            }
//        }
    }
    
    /**
     * 
     * @param list
     * @return
     */
    private SampleParameter getParameters(String[] list) {
        
        SampleParameter result = new SampleParameter();
        String[] name = null;
        String value = null;
        int index;
        
        if(list != null) {
            
            for(String s : list) {
                
                if(s != null) {
                    
                    if(s.trim().startsWith("record.")) {
                        
                        name = s.split("\\.");
                        if(name.length == 2) {
                            
                            value = name[1].trim();
                            if(value.compareToIgnoreCase("PREC") == 0) {
                                
                                index = sddsFile.getParameterIndex(s);
                                result.setPrecision(((Long)sddsFile.getParameterValue(index, 1, true)).intValue());
                            } else if(value.compareToIgnoreCase("HOPR") == 0) {

                                index = sddsFile.getParameterIndex(s);
                                result.setDisplayHigh(((Float)sddsFile.getParameterValue(index, 1, true)).doubleValue());
                            } else if(value.compareToIgnoreCase("LOPR") == 0) {

                                index = sddsFile.getParameterIndex(s);
                                result.setDisplayLow(((Float)sddsFile.getParameterValue(index, 1, true)).doubleValue());
                            } else if(value.compareToIgnoreCase("HIHI") == 0) {

                                index = sddsFile.getParameterIndex(s);
                                result.setHighAlarm(((Float)sddsFile.getParameterValue(index, 1, true)).doubleValue());
                            } else if(value.compareToIgnoreCase("HIGH") == 0) {

                                index = sddsFile.getParameterIndex(s);
                                result.setHighWarning(((Float)sddsFile.getParameterValue(index, 1, true)).doubleValue());
                            } else if(value.compareToIgnoreCase("LOLO") == 0) {

                                index = sddsFile.getParameterIndex(s);
                                result.setLowAlarm(((Float)sddsFile.getParameterValue(index, 1, true)).doubleValue());
                            } else if(value.compareToIgnoreCase("LOW") == 0) {

                                index = sddsFile.getParameterIndex(s);
                                result.setLowWarning(((Float)sddsFile.getParameterValue(index, 1, true)).doubleValue());
                            } else if(value.compareToIgnoreCase("EGU") == 0) {

                                index = sddsFile.getParameterIndex(s);
                                String tmp = (String) sddsFile.getParameterValue(index, 1, true);
                                if (tmp != null) {
                                	if (tmp.compareTo("\"\"") == 0) {
                                		tmp = "N/A";
                                	}
                                } else {
                                	tmp = "N/A";
                                }
                                
                                result.setUnits(tmp);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }
}
