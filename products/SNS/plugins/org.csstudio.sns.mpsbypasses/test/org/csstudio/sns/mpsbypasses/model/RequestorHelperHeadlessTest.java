package org.csstudio.sns.mpsbypasses.model;

import org.csstudio.platform.utility.rdb.RDBUtil;
import org.csstudio.sns.mpsbypasses.Preferences;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link RequestLookup}
 *  @author Kay Kasemir
 */
public class RequestorHelperHeadlessTest
{
	@Test
	public void testBypassModel() throws Exception
	{
		final RDBUtil rdb = RDBUtil.connect(Preferences.getRDB_URL(),
				Preferences.getRDB_User(), Preferences.getRDB_Password(), false);
		RequestLookup requests = new RequestLookup(new NullProgressMonitor(), rdb.getConnection());
		requests.dump(System.out);
		rdb.close();
	}
}
