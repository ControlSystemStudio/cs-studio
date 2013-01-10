
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

package org.csstudio.archive.sdds.server.data;

import org.csstudio.archive.sdds.server.file.SddsType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link EpicsRecordData}.
 *
 * @author mmoeller
 * @version 1.0
 * @since 05.08.2011
 */
public class EpicsRecordDataUnitTest {

    @Test
    public final void testConstructor() {

        long statusAndSeverity = ArchiveStatus.READ_ALARM.getStatusValue() << 16;
        statusAndSeverity = statusAndSeverity | ArchiveSeverity.ARCHIVE_DISABLED.getSeverityValue();

        // This record is invalid because of the status and severity
        EpicsRecordData out = new EpicsRecordData(1293836400L, 1203L, statusAndSeverity, new Double(1234.890));
        Assert.assertEquals(1293836400L, out.getTime());
        Assert.assertEquals(1203L, out.getNanoSeconds());
        Assert.assertEquals(1234.890, out.getValue());
        Assert.assertEquals(SddsType.SDDS_DOUBLE, out.getSddsType());
        Assert.assertEquals(ArchiveSeverity.ARCHIVE_DISABLED.getSeverityValue(), out.getSeverity());
        Assert.assertEquals(ArchiveStatus.READ_ALARM.getStatusValue(), out.getStatus());
        Assert.assertFalse(out.isValueValid());

        statusAndSeverity = ArchiveStatus.UDF_ALARM.getStatusValue() << 16;
        statusAndSeverity = statusAndSeverity | ArchiveSeverity.DISCONNECTED.getSeverityValue();

        // This record is invalid because of the status and severity, too
        out = new EpicsRecordData(1234567890L, 1203L, statusAndSeverity, null);
        Assert.assertEquals(1234567890L, out.getTime());
        Assert.assertEquals(1203L, out.getNanoSeconds());
        Assert.assertEquals(Double.NaN, out.getValue());
        Assert.assertEquals(SddsType.SDDS_DOUBLE, out.getSddsType());
        Assert.assertEquals(ArchiveSeverity.DISCONNECTED.getSeverityValue(), out.getSeverity());
        Assert.assertEquals(ArchiveStatus.UDF_ALARM.getStatusValue(), out.getStatus());
        Assert.assertFalse(out.isValueValid());

        statusAndSeverity = ArchiveStatus.NO_ALARM.getStatusValue() << 16;
        statusAndSeverity = statusAndSeverity | ArchiveSeverity.NO_ALARM.getSeverityValue();

        // A nice and valid record
        out = new EpicsRecordData(1234567890L, 1203L, statusAndSeverity, new Double(90.23));
        Assert.assertEquals(1234567890L, out.getTime());
        Assert.assertEquals(1203L, out.getNanoSeconds());
        Assert.assertEquals(90.23, out.getValue());
        Assert.assertEquals(SddsType.SDDS_DOUBLE, out.getSddsType());
        Assert.assertEquals(ArchiveSeverity.NO_ALARM.getSeverityValue(), out.getSeverity());
        Assert.assertEquals(ArchiveStatus.NO_ALARM.getStatusValue(), out.getStatus());
        Assert.assertTrue(out.isValueValid());
    }
}
