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
 */
package org.csstudio.common.trendplotter.archive;


import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveRepository;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.common.trendplotter.model.LiveSamples;
import org.csstudio.common.trendplotter.model.PVItem;
import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test for {@link LiveSamples}.
 *
 * @author jhatje
 * @since 09.09.2011
 */
public class ArchiverTest {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiverTest.class);

    private ArchiveReader _channelArchiveReader;
    private ArchiveReader _desyArchiveReader;
    private PVItem _pvItem;
    private ITimestamp _end;
    private ITimestamp _start;

    @Before
    public void setup() throws Exception {
        _channelArchiveReader = ArchiveRepository.getInstance().getArchiveReader("xnds://krynfs.desy.de/ArchiveDataServer.cgi");
        _desyArchiveReader = ArchiveRepository.getInstance().getArchiveReader("mysql://");
        _pvItem = new PVItem("12PI102_ai.VAL", 1.0);
        _pvItem = new PVItem("krykWeather:vWindBoe_ai.VAL", 1.0);
//        _pvItem = new PVItem("WK:K:16d:2:TDL_Desy_ai.VAL", 1.0);
//        _pvItem = new PVItem("krykWeather:Temp_ai.VAL", 1.0);
//        _end = TimestampFactory.fromDouble(1.317082937157E9);
        _end = TimestampFactory.fromDouble(1.322071380000E9); // 2011-11-23 18:03:00
//        _end = TimestampFactory.fromDouble(1.322071380000E9); // 2011-11-23 18:03:00
//        _start = TimestampFactory.fromDouble(1.317027377157E9);
        _start = TimestampFactory.fromDouble(1.321083987150E9); //2011-11-23 19:55:00
//        _start = TimestampFactory.fromDouble(1.322078100000E9); //2011-11-23 19:55:00
//        _start = TimestampFactory.fromDouble(_end.toDouble() - 20.0*60.0*60.0);
    }

    @Test
    public void testOptimizedData() throws Exception {

        int i = 0;
        final ValueIterator desyArchiverValues = _desyArchiveReader.getOptimizedValues(1, _pvItem.getName(), _start, _end, 15);
        LOG.info("Desy Archiver Optimized:");
        while (desyArchiverValues.hasNext()) {
            i++;
            final IValue value = desyArchiverValues.next();
            final IMinMaxDoubleValue minMaxValue = (IMinMaxDoubleValue) value;
            LOG.info("value " + i + ": " + value.getTime().seconds() + " - " + ValueUtil.getDouble(value) + " [     " + minMaxValue.getMinimum()
                     + "   ...    " + minMaxValue.getMaximum() + "   ]");
//            LOG.info("value " + i + ": " + value.getTime().toCalendar().getTime().toString() + " - " + ValueUtil.getDouble(value));
        }

        final ValueIterator channelArchiverValues = _channelArchiveReader.getOptimizedValues(1, _pvItem.getName(), _start, _end, 15);
        i = 0;
        LOG.info("Channel Archiver Optimized:");
        while (channelArchiverValues.hasNext()) {
            i++;
            final IValue value = channelArchiverValues.next();
            final double double1 = ValueUtil.getDouble(value);
            LOG.info("value " + i + ": " + value.getTime().seconds() + " - " + double1);
        }
    }

    @Test
    public void testRawData() throws Exception {

        final ValueIterator channelArchiverValues = _channelArchiveReader.getRawValues(1, _pvItem.getName(), _start, _end);
        int i = 0;
        LOG.info("Channel Archiver Raw:");
        while (channelArchiverValues.hasNext()) {
            i++;
            final IValue value = channelArchiverValues.next();
            LOG.info("value " + i + ": " + value.getTime().seconds() + " - " + ValueUtil.getDouble(value));
        }

        i = 0;
        final ValueIterator desyArchiverValues = _desyArchiveReader.getRawValues(1, _pvItem.getName(), _start, _end);
        LOG.info("Desy Archiver Raw:");
        while (desyArchiverValues.hasNext()) {
            i++;
            try {

                final IValue value = desyArchiverValues.next();
            LOG.info("value " + i + ": " + value.getTime().seconds() + " - " + ValueUtil.getDouble(value));
            } catch (final Exception e) {
                LOG.info(e.getMessage());
                LOG.info(e.toString());
            }
        }

    }
}
