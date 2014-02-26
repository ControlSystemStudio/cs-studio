package org.csstudio.archive.reader.appliance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * <code>ApplianceArchiveReaderTestSuite</code> is the test suite that combines
 * all junit tests of the appliance archive reader.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ 
		ApplianceArchiveReaderOptimizedTest.class,
		ApplianceArchiveReaderOptimizedStatisticsTest.class,
		ApplianceArchiveReaderRawTest.class,
		ApplianceArchiveReaderRawWaveformTest.class})
public class ApplianceArchiveReaderTestSuite {

}
