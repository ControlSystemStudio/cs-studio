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
package org.csstudio.testsuite;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 * Class that gathers all tests (class files that end on *Test.class) in the specified
 * bundles and creates a test suite.
 *
 * @author cweber
 * @author $Author$
 * @version $Revision$
 * @since 07.07.2010
 */
public class AllTestsSuite {

	//Comma separated list, if list is empty, every root will be chosen
	//private static final String bundleRoot = "org.csstudio";
	private static final String BUNDLE_ROOT = "org.,de.desy.";


	//Comma separated list, must contain one entry!
	//private static final String packageRoot = "org.csstudio";
	private static final String PACKAGE_ROOT = "org.csstudio,org.epics,org.remotercp,de.desy";


	private static final String TEST_CLASS_FILTER = "*Test";
	private static final String TEST_SUITE_NAME = "CSSTestsSuite";



	public static Test suite() {

	    final TestSuite suite = new TestSuite(TEST_SUITE_NAME);

	    final BundleTestCollector testCollector = new BundleTestCollector();
	    final List<Test> tests = testCollector.collectTests(suite, BUNDLE_ROOT, PACKAGE_ROOT, TEST_CLASS_FILTER);

	    for (final Test test : tests) {
            suite.addTest(test);
        }

	    return suite;

	}
}
