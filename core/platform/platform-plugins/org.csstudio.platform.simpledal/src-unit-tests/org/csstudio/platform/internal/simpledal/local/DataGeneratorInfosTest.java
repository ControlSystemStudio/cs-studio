/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 /**
 *
 */
package org.csstudio.platform.internal.simpledal.local;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

/**
 * @author swende
 *
 */
public class DataGeneratorInfosTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.local.DataGeneratorInfos#getPattern()}.
     */
    @Test
    public void testGetPattern() {
        // tests for the SYSTEM_INFO pattern

        // match without period
        test(DataGeneratorInfos.SYSTEM_INFO, "SINFO:userId", true,
                new String[] { "userId", null, null });
        test(DataGeneratorInfos.SYSTEM_INFO, "SINFO:1111", true,
                new String[] { "1111", null, null });

        // match with period
        test(DataGeneratorInfos.SYSTEM_INFO, "SINFO:userId:1000", true,
                new String[] { "userId", ":1000", "1000" });
        test(DataGeneratorInfos.SYSTEM_INFO, "SINFO:2222:1000", true,
                new String[] { "2222", ":1000", "1000" });

        // no matches
        test(DataGeneratorInfos.SYSTEM_INFO, "SINFO:userId:abc:1000", false, null);
        test(DataGeneratorInfos.SYSTEM_INFO, "SINFO:userId:abc", false, null);
        test(DataGeneratorInfos.SYSTEM_INFO, "SINFO:userId:", false, null);
        test(DataGeneratorInfos.SYSTEM_INFO, "SINFO:", false, null);
        test(DataGeneratorInfos.SYSTEM_INFO, "SINFO:&1234", false, null);
        test(DataGeneratorInfos.SYSTEM_INFO, "SINFO:&12>4", false, null);
        test(DataGeneratorInfos.SYSTEM_INFO, "SINFO:a&b$c", false, null);
        test(DataGeneratorInfos.SYSTEM_INFO, "KINFO:userId", false, null);
        test(DataGeneratorInfos.SYSTEM_INFO, "KINFO:userId:1000", false, null);
        test(DataGeneratorInfos.SYSTEM_INFO, "SINFO::1000", false, null);


    }

    private void test(DataGeneratorInfos descriptor, String pvName,
            boolean shouldMatch, String[] expectedGroups) {
        Pattern p = descriptor.getPattern();
        Matcher m = p.matcher(pvName);

        boolean matches = m.matches();

        assertEquals(shouldMatch, matches);

        if (matches) {
            assertEquals(expectedGroups.length, m.groupCount());

            for (int i = 0; i < m.groupCount(); i++) {
                assertEquals(expectedGroups[i], m.group(i + 1));
            }
        }
    }
}
