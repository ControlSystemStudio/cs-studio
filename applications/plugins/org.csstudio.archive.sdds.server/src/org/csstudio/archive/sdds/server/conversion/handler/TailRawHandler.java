
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.archive.sdds.server.command.header.DataRequestHeader;
import org.csstudio.archive.sdds.server.data.EpicsRecordData;
import org.csstudio.archive.sdds.server.util.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class returns the raw data from the SDDS files. If it find more than <code>maxSamplesPerRequest</code>
 * samples it returns only the most recent <code>maxSamplesPerRequest</code> samples.
 * The default value of <code>maxSamplesPerRequest</code> is 10000.
 *
 * @author mmoeller
 * @version
 * @since 15.02.2011
 */
public class TailRawHandler extends AbstractAlgorithmHandler {

    /** The logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger(TailRawHandler.class);

    public TailRawHandler(final int maxSamples) {
        super(maxSamples);
        LOG.info("TailRawHandler created. Max. samples per request: {}", maxSamples);
    }

    /**
     * @see org.csstudio.archive.sdds.server.conversion.handler.AbstractAlgorithmHandler#handle(org.csstudio.archive.sdds.server.command.header.DataRequestHeader, org.csstudio.archive.sdds.server.data.EpicsRecordData[])
     */
    @Override
    @Nonnull
    public List<EpicsRecordData> handle(@Nonnull final DataRequestHeader header,
                                        @Nonnull final EpicsRecordData[] data)
    throws DataException, AlgorithmHandlerException, MethodNotImplementedException {

        if (data == null) {
            return Collections.emptyList();
        } else if (data.length == 0){
            return Collections.emptyList();
        }

        final long intervalStart = header.getFromSec();
        final long intervalEnd = header.getToSec();

        // Get the number of requested samples
        int resultLength = header.getMaxNumOfSamples();

        final int maxSamplesPerRequest = getMaxSamplesPerRequest();
        if(resultLength > maxSamplesPerRequest) {
            resultLength = maxSamplesPerRequest;
        }

        if(resultLength > data.length) {
            resultLength = data.length;
        }

        final List<EpicsRecordData> newData = new ArrayList<EpicsRecordData>(resultLength);

        // We start at the end of the data array
        int dataIndex = data.length - 1;

        do {

            if(data[dataIndex].getTime() >= intervalStart && data[dataIndex].getTime() <= intervalEnd) {

                final EpicsRecordData obj = new EpicsRecordData(data[dataIndex].getTime(),
                                                          data[dataIndex].getNanoSeconds(),
                                                          data[dataIndex].getStatus(),
                                                          Double.valueOf((Float) data[dataIndex].getValue()));

                newData.add(obj);
            }

            dataIndex--;

        } while(dataIndex >= 0 && newData.size() < resultLength);

        // The order has to be reversed
        Collections.reverse(newData);

        return newData;
    }
}
