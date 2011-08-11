
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.archive.sdds.server.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import org.csstudio.archive.sdds.server.conversion.SampleParameter;
import org.csstudio.archive.sdds.server.data.EpicsRecordData;
import org.csstudio.archive.sdds.server.data.RecordDataCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class reads a specified SDDS file and offers a method to get the data from the file.
 * 
 * @author Markus Moeller
 * @version 1.0
 * 
 */
public class SddsFileReader {
    
    /** The logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger(SddsFileReader.class);

    /** The path to the data files */
    private final ArchiveLocation dataPath;
    
    /**
     * Constructor that gets a string containing the path to the data files.
     * 
     * @throws DataPathNotFoundException 
     */
    public SddsFileReader(String dataSourceFile) throws DataPathNotFoundException {
        dataPath = new ArchiveLocation();
        dataPath.loadLocationList(dataSourceFile);
    }
    
    /**
     * 
     * @param startTime
     * @param endTime
     * @return Returns array of String that contains all paths for the given time interval
     */
    public String[] getAllPaths(long startTime, long endTime) {
        return dataPath.getAllPaths(startTime, endTime);
    }
    
    /**
     * Reads the data of a specified SDDS data file.
     * 
     * @param recordName
     * @param startTime
     * @param endTime
     * @return Array of data objects
     */
    public RecordDataCollection readData(String recordName, long startTime, long endTime) {
        
        RecordDataCollection dataCollection = null;
        ThreadGroup threadGroup = null;
        Thread[] readerThread = null;
        SddsDataReader[] reader = null;
        String[] filePaths = null;
        
        // filePaths = dataPath.getAllPaths(getEndTimeOfPreviousMonth(startTime), getStartTimeOfNextMonth(endTime));
        filePaths = dataPath.getAllPaths(getEndTimeOfPreviousMonth(startTime), endTime);
        
        long st = System.currentTimeMillis();
        
        threadGroup = new ThreadGroup("DataReader");
        readerThread = new Thread[filePaths.length];
        reader = new SddsDataReader[filePaths.length];
        for(int i = 0;i < filePaths.length;i++)
        {
            filePaths[i] = getCorrectFilename(filePaths[i], recordName);
            if(filePaths[i] != null)
            {
                reader[i] = new SddsDataReader(filePaths[i], startTime, endTime);
                readerThread[i] = new Thread(threadGroup, reader[i]);
                readerThread[i].start();
            }
        }
        
        synchronized(threadGroup) {
            while(threadGroup.activeCount() > 0) {
                try {
                    threadGroup.wait(10);
                } catch(InterruptedException ie) {
                    LOG.warn("*** Interrupted ***");
                }
            }
        }
        
        long et = System.currentTimeMillis() - st;
        
        LOG.debug("Finished in " + et + " Millisekunden (" + (et / 1000) + " sec)");
        
        dataCollection = new RecordDataCollection();
        Vector<EpicsRecordData> allResults = new Vector<EpicsRecordData>();
        for(int i = 0;i < filePaths.length;i++) {
            if(reader[i] != null) {
                allResults.addAll(reader[i].getResult());
            }
        }

        List<EpicsRecordData> result = new ArrayList<EpicsRecordData>(allResults.size());       
        result.addAll(allResults);
        
        dataCollection.setData(result);
        if(reader[0] != null) {
            dataCollection.setSampleParameter(reader[0].getSampleCtrl());
        } else {
            dataCollection.setSampleParameter(new SampleParameter());
        }
        
        return dataCollection;
    }
        
    /**
     * 
     * @param path
     * @param filename
     * @return
     */
    private String getCorrectFilename(String path, String filename) {
        
        String result = null;
        File file = null;
        
        file = new File(path + filename);
        if(file.exists() == false) {
            file = null;
            file = new File(path + filename + ".gz");
            if(file.exists()) {
                result = path + filename + ".gz";
            }
        } else {
            result = path + filename;
        }
        
        return result;
    }
    
    /**
     * New start search time is '01-mmm-yyyy 00:00:00' - 1 sec .
     * Get year and month from current epoch, increase the month by one,
     * modify others and call 'mktime()' to get new start_time.
     * 
     * @param dwStartTime
     * @return The last timestamp of the month
     */
    public long getEndTimeOfPreviousMonth(long dwStartTime) { 
        
        GregorianCalendar cal = null;
        long epoch;

        cal = new GregorianCalendar();
        cal.setTimeInMillis(dwStartTime * 1000);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        
        epoch = (cal.getTimeInMillis() / 1000) - 1;
        
        return epoch; 
    }
    
    /**
     *  New start search time is '01-mmm-yyyy 00:00:00'.
     *  Get year and month from current epoch, increase the month by one,
     *  modify others and call 'mktime()' to get new start_time.
     *  
     * @param dwStartTime
     * @return The first timestamp of the next month
     */
    public long getStartTimeOfNextMonth(long dwStartTime) { 
        
        GregorianCalendar cal = null;
        long epoch;

        cal = new GregorianCalendar();
        cal.setTimeInMillis(dwStartTime * 1000);
        
        cal.add(Calendar.MONTH, 1);
        if(cal.get(Calendar.MONTH) > 10) {
            cal.set(Calendar.MONTH, 0);
            cal.add(Calendar.YEAR, 1);
        }
        
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        
        epoch = (cal.getTimeInMillis() / 1000);

        return epoch; 
    }
}
