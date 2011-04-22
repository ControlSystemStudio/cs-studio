
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

package org.csstudio.archive.sdds.server.conversion.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.csstudio.archive.sdds.server.command.header.DataRequestHeader;
import org.csstudio.archive.sdds.server.data.EpicsRecordData;
import org.csstudio.archive.sdds.server.sdds.SDDSType;
import org.csstudio.archive.sdds.server.util.DataException;
import org.csstudio.platform.logging.CentralLogger;

/**
 * This class returns the real raw data. No filtering methods will be used and the number of samples will
 * be ignored. BEWARE: The number of returned data samples may be really large. 
 * 
 * @author mmoeller
 * @version 
 * @since 06.10.2010
 */
public class NoFilterHandler extends AlgorithmHandler {
    
    /** The logger for this class */
    private Logger logger;

    /**
     * The standard constructor
     */
    public NoFilterHandler(int maxSamples) {
        super(maxSamples);
        logger = CentralLogger.getInstance().getLogger(this);
        logger.info("NoFilterHandler created. Max. samples per request: " + maxSamples);
    }
    
    /**
     * @see org.csstudio.archive.sdds.server.conversion.handler.AlgorithmHandler#handle(org.csstudio.archive.sdds.server.command.header.DataRequestHeader, org.csstudio.archive.sdds.server.data.EpicsRecordData[])
     */
    @Override
    public Iterable<EpicsRecordData> handle(DataRequestHeader header, EpicsRecordData[] data)
    throws DataException, AlgorithmHandlerException, MethodNotImplementedException {

        if (data == null) {
            return new ArrayList<EpicsRecordData>(0);
        } else if (data.length == 0){
            return new ArrayList<EpicsRecordData>(0);
        }

        long intervalStart = header.getFromSec();
        long intervalEnd = header.getToSec();

        List<EpicsRecordData> newData = new ArrayList<EpicsRecordData>(data.length);
        
        for(int i = 0;i < data.length;i++) {
            if((data[i].getTime() >= intervalStart) && (data[i].getTime() <= intervalEnd)) {
                newData.add(new EpicsRecordData(data[i].getTime(), data[i].getNanoSeconds(),
                                                 data[i].getStatus(), new Double((Float)data[i].getValue()),
                                                 SDDSType.SDDS_DOUBLE));
            }
        }
        
        return newData;
    }
}
