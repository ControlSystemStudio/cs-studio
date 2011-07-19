
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

package org.csstudio.archive.sdds.server.data;

import java.util.ArrayList;

import org.csstudio.archive.sdds.server.command.header.DataRequestHeader;
import org.csstudio.archive.sdds.server.conversion.ConversionExecutor;
import org.csstudio.archive.sdds.server.sdds.DataPathNotFoundException;
import org.csstudio.archive.sdds.server.sdds.SddsFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class DataCollector {
    
    /** The logger of this class */
    private static final Logger LOG = LoggerFactory.getLogger(DataCollector.class);
    
    /**  */
    private final ConversionExecutor conversionExecutor;
    
    /** */
    private SddsFileReader sddsReader;

    /**
     * 
     */
    public DataCollector() throws DataCollectorException {
        
        conversionExecutor = new ConversionExecutor();
        
        try {
            sddsReader = new SddsFileReader();
        } catch(DataPathNotFoundException dpnfe) {
            LOG.error("[*** DataPathNotFoundException ***]: {}", dpnfe);
            throw new DataCollectorException("DataCollector: Cannot instantiate the class SddsFileReader: " + dpnfe.getMessage());
        }
    }
    
    /**
     * 
     * @param recordName
     * @param startTime
     * @param endTime
     * @return
     */
    public RecordDataCollection readData(String recordName, DataRequestHeader header) {
        
        RecordDataCollection dataCollection = null;
        ArrayList<EpicsRecordData> data = null;
        
        dataCollection = sddsReader.readData(recordName, header.getFromSec(), header.getToSec());
        
        EpicsRecordData[] readData = new EpicsRecordData[dataCollection.getNumberOfData()];
        readData = dataCollection.getData().toArray(readData);
        
        data = (ArrayList<EpicsRecordData>) conversionExecutor.convertData(recordName, readData, header);
        dataCollection.setData(data);
        
        return dataCollection;
    }
}
