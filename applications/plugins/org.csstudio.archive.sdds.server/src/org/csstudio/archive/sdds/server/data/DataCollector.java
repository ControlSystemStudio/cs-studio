
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

import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.archive.sdds.server.command.header.DataRequestHeader;
import org.csstudio.archive.sdds.server.conversion.ConversionExecutor;
import org.csstudio.archive.sdds.server.file.DataPathNotFoundException;
import org.csstudio.archive.sdds.server.file.SddsFileLengthException;
import org.csstudio.archive.sdds.server.file.SddsFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.desy.aapi.AapiServerError;

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
            sddsReader = new SddsFileReader("./sdds_data_location.txt");
        } catch(final DataPathNotFoundException dpnfe) {
            LOG.error("[*** DataPathNotFoundException ***]: {}", dpnfe.getMessage());
            throw new DataCollectorException("DataCollector: Cannot instantiate the class SddsFileReader: " + dpnfe.getMessage());
        }
    }

    /**
     * @param recordName
     * @param header
     * @return The read data
     */
    @Nonnull
    public RecordDataCollection readData(@Nonnull final String recordName,
                                         @Nonnull final DataRequestHeader header) {

        RecordDataCollection dataCollection = null;
        List<EpicsRecordData> data = null;

        try {

            dataCollection = sddsReader.readData(recordName, header.getFromSec(), header.getToSec());
            EpicsRecordData[] readData = new EpicsRecordData[dataCollection.getNumberOfData()];
            readData = dataCollection.getData().toArray(readData);

            data = conversionExecutor.convertData(readData, header);
            dataCollection.setData(data);

        } catch (final SddsFileLengthException fle) {
            dataCollection = new RecordDataCollection(AapiServerError.CAN_T_OPEN_FILE);
            LOG.error("[*** SddsFileLengthException ***]: {}", fle.getMessage());
        }

        return dataCollection;
    }
}
