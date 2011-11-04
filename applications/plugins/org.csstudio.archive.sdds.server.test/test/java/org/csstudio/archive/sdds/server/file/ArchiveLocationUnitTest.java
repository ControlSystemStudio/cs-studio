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
package org.csstudio.archive.sdds.server.file;

import org.csstudio.domain.common.resource.CssResourceLocator;
import org.csstudio.domain.common.resource.CssResourceLocator.RepoDomain;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link ArchiveLcoation}.
 *
 * @author bknerr
 * @since Nov 4, 2011
 */
public class ArchiveLocationUnitTest {

    final String SDDS_LOCATION =
        CssResourceLocator.composeResourceLocationString(RepoDomain.APPLICATIONS,
                                                         "org.csstudio.archive.sdds.server.test",
                                                         "res/sdds_data_valid_locations.txt");

    @Test
    public void testPathAssembly() throws DataPathNotFoundException {
        final ArchiveLocation al = new ArchiveLocation(SDDS_LOCATION);
        final long _1995_2_15 = new DateTime(0L).plusYears(25).plusMonths(1).plusDays(14).getMillis();
        final long _2000_8_5 = new DateTime(0L).plusYears(30).plusMonths(7).plusDays(4).getMillis();

        final String[] allPaths = al.getAllPaths(_1995_2_15, _2000_8_5);
        Assert.assertEquals(56, allPaths.length);
        // paths exist for 1995 - 2000, but without 1997

        Assert.assertEquals(".\\res\\data2\\SDDS\\1995\\01\\", allPaths[0]);
        Assert.assertEquals(".\\res\\data1\\SDDS\\2000\\08\\", allPaths[55]);
    }

    @Test
    public void testCornerCases() throws DataPathNotFoundException {
        final ArchiveLocation al = new ArchiveLocation(SDDS_LOCATION);
        String[] allPaths = al.getAllPaths(0, 0);
        Assert.assertTrue(allPaths.length == 0);

        final long _1994_3_3 = new DateTime(0L).plusYears(24).plusMonths(2).plusDays(2).getMillis();
        final long _1995_1_1 = new DateTime(0L).plusYears(25).getMillis();
        allPaths = al.getAllPaths(_1994_3_3, _1995_1_1);
        Assert.assertTrue(allPaths.length == 1);
        Assert.assertEquals(".\\res\\data2\\SDDS\\1995\\01\\", allPaths[0]);

        final long _2000_12_31 = new DateTime(0L).plusYears(30).plusMonths(11).plusDays(30).getMillis();
        allPaths = al.getAllPaths(_2000_12_31, _2000_12_31 + 1L);
        Assert.assertTrue(allPaths.length == 2);
        Assert.assertEquals(".\\res\\data1\\SDDS\\2000\\11\\", allPaths[0]);
        Assert.assertEquals(".\\res\\data1\\SDDS\\2000\\12\\", allPaths[1]);

        final long _1998_12_31 = new DateTime(0L).plusYears(28).plusMonths(11).plusDays(30).getMillis();
        allPaths = al.getAllPaths(_1998_12_31, _1998_12_31 + 1L);
        Assert.assertTrue(allPaths.length == 3);
        Assert.assertEquals(".\\res\\data2\\SDDS\\1998\\11\\", allPaths[0]);
        Assert.assertEquals(".\\res\\data2\\SDDS\\1998\\12\\", allPaths[1]);
        Assert.assertEquals(".\\res\\data2\\SDDS\\1999\\01\\", allPaths[2]);
    }
}
