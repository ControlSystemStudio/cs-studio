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
 */
package org.csstudio.testsuite;

import java.util.List;

import javax.annotation.Nonnull;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.csstudio.platform.util.StringUtil;
import org.csstudio.testsuite.util.TestDataProvider;
import org.csstudio.testsuite.util.TestProviderException;

/**
 * Factory for test suites that collect test classes over all existing bundles.
 *
 * @author bknerr
 * @since 21.07.2010
 */
// CHECKSTYLE:OFF due to bug in 5.3 - "This class should be declared 'abstract'" WTF?!
public final class TestSuiteFactory {
// CHECKSTYLE:ON

    /**
     * Don't instantiate.
     */
    private TestSuiteFactory() {
        // Empty
    }
    
    /**
     * The common suffix for all test classes.
     */
    public static final String COMMON_TEST_SUFFIX = "Test";

    // Get site specific test data provider
    private static TestDataProvider PROV = createTestDataProvider();
    @Nonnull
    private static TestDataProvider createTestDataProvider() {
        try {
            return TestDataProvider.getInstance(Activator.PLUGIN_ID);
        } catch (final TestProviderException e) {
            Assert.fail("Unexpected exception creating the test data provider for plugin " +
                        Activator.PLUGIN_ID + ".\n" + e.getMessage());
        }
        return TestDataProvider.EMPTY_PROVIDER;
    }

    private static final String BUNDLES = getPropertyOrEmptyString("bundles", PROV);
    private static final String BUNDLES_BLACKLIST  = getPropertyOrEmptyString("bundleBlacklist", PROV);
    private static final String PACKAGE_BLACKLIST = getPropertyOrEmptyString("packageBlacklist", PROV);
    
    @Nonnull
    private static String getPropertyOrEmptyString(@Nonnull final String property, 
                                                   @Nonnull final TestDataProvider prov) {
        String result = (String) prov.get(property);
        return result == null ? "" : result;
    }
    
    /**
     * The test suite provider.
     *
     * @param suiteName the name of the test suite
     * @param classFilter the string containing the comma-separated list of class filters
     * @param commonFilterSuffix the class name suffix common to all types of tests
     * @return the suite containing all tests that adhere to the given patterns
     */
    @Nonnull
    public static Test getSuite(@Nonnull final String suiteName,
                                @Nonnull final String classFilter,
                                @Nonnull final String commonFilterSuffix) {
        final TestSuite suite = new TestSuite(suiteName);

        final BundleTestCollector testCollector = new BundleTestCollector();

        final List<Test> tests = testCollector.collectTests(StringUtil.createListFrom(BUNDLES),
                                                            StringUtil.createListFrom(BUNDLES_BLACKLIST),
                                                            StringUtil.createListFrom(PACKAGE_BLACKLIST),
                                                            StringUtil.createListFrom(classFilter),
                                                            commonFilterSuffix);

        for (final Test test : tests) {
            suite.addTest(test);
        }

        return suite;
    }
}
