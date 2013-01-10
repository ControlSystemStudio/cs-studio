
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

package org.csstudio.archive.sdds.server.file;

import org.csstudio.archive.sdds.server.data.RecordDataCollection;
import org.csstudio.domain.common.resource.CssResourceLocator;
import org.csstudio.domain.common.resource.CssResourceLocator.RepoDomain;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link SddsFileReader}.
 *
 * @author mmoeller
 * @version 1.0
 * @since 08.08.2011
 */
public class SddsFileReaderUnitTest {

    @Test
    public void testFileNotFound() {

        // This file does not exist
        final String sddsLocation = CssResourceLocator.
                               composeResourceLocationString(
                               RepoDomain.APPLICATIONS,
                               "org.csstudio.archive.sdds.server.test",
                               "res/blah.txt");

        try {
            @SuppressWarnings("unused")
            final SddsFileReader fileReader = new SddsFileReader(sddsLocation);
        } catch (final DataPathNotFoundException e) {
            Assert.assertTrue(e.getMessage().startsWith("File with the location paths cannot be found"));
        }
    }

    @Test
    public void testReadData() {

        // This file contains invalid paths
        final String sddsLocation =
            CssResourceLocator.composeResourceLocationString(RepoDomain.APPLICATIONS,
                                                             "org.csstudio.archive.sdds.server.test",
                                                             "res/sdds_data_valid_locations.txt");
        System.out.println(sddsLocation);
        try {
            final SddsFileReader fileReader = new SddsFileReader(sddsLocation);

            final long _1998_5_1 = new DateTime(0L).plusYears(28).plusMonths(4).getMillis();
            final long _1998_7_1 = new DateTime(0L).plusYears(28).plusMonths(6).getMillis();

            RecordDataCollection dataCollection = new RecordDataCollection();
            try {
                dataCollection = fileReader.readData("krykWeather_Temp_ai", _1998_5_1/1000, _1998_7_1/1000);
                Assert.assertTrue(dataCollection.getNumberOfData() > 0);
            } catch (final SddsFileLengthException e) {
                Assert.assertTrue(dataCollection.containsError());
            }


        } catch (final DataPathNotFoundException e) {
            Assert.assertTrue(e.getMessage().endsWith("cannot be found or is empty."));
        }
    }
}
