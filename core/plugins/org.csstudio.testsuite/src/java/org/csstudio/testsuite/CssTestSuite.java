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
 * $Id: CssUiPluginsTestsSuite.java,v 1.1.2.3 2010/07/29 12:35:29 bknerr Exp $
 */
package org.csstudio.testsuite;

import static org.csstudio.testsuite.TestSuiteFactory.COMMON_TEST_SUFFIX;

import javax.annotation.Nonnull;

import junit.framework.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Class that collects all tests (class files that end on *&lt;testFilter&gt;.class) in the specified
 * bundles, creates and starts a test suite.
 *
 * Different launch configurations already exist - all of them start a JUnit Plugin Test (also for
 * simple unit tests), since only then the test suite has access to the unit tests in all bundles
 * selected in the launch configuration.
 *
 * Any class in any bundle is tried to be loaded (which triggers the bundle activation) and analysed
 * whether it extends a {@link junit.framework.TestCase} for JUnit < 4.0 or whether any of its
 * methods features the {@link org.junit.Test} annotation for JUnit >= 4.0.
 * If so, it is added to the suite, otherwise it's not, and a warning is generated when the class'
 * name is suggesting it being a test without being one (often observed for demo applications that
 * are named XXXApplicationTest).
 *
 * Set -DtestFilter=UnitTest|HeadlessTest|UiPluginTest in the jvm arguments of the launch
 * configuration.
 *
 * Default is 'Test' meaning any test is executed (hence you are required to set the launch
 * configuration's start application to any product (best is the common product) in main tab.
 *
 * @author bknerr
 * @author $Author: bknerr $
 * @version $Revision: 1.1.2.3 $
 * @since 21.07.2010
 */
public final class CssTestSuite {
    private static final Logger LOG = LoggerFactory.getLogger(CssTestSuite.class);

    private CssTestSuite() {
        // Empty
    }

    @Nonnull
    public static Test suite() {

        String filter = System.getProperty("testFilter");
        if (Strings.isNullOrEmpty(filter)) {
            LOG.info("No test class filter has been set in the jvm arguments of the lauch configuration.\nDefault is {} (for all classes *{}.java).",
                     TestSuiteFactory.COMMON_TEST_SUFFIX,
                     TestSuiteFactory.COMMON_TEST_SUFFIX);

            filter = TestSuiteFactory.COMMON_TEST_SUFFIX;
        }

        return TestSuiteFactory.getSuite("CssTestSuite_" + filter,
                                         filter,
                                         COMMON_TEST_SUFFIX);
    }
}
