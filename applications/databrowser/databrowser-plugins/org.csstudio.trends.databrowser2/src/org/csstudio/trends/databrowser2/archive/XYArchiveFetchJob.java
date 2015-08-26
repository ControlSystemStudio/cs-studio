/**
 * 
 */
package org.csstudio.trends.databrowser2.archive;

import java.time.Instant;

import org.csstudio.archive.reader.rdb.ConnectionCache;
import org.csstudio.trends.databrowser2.model.PVItem;

/**
 * @author lamberm
 *
 */
public class XYArchiveFetchJob extends ArchiveFetchJob {

	public XYArchiveFetchJob(PVItem item, Instant start, Instant end,
			ArchiveFetchJobListener listener) {
		super(item, start, end, listener);

		this.failedThrowExceptionGetData = false;
		this.displayUnknowChannelException = false;

		ConnectionCache.clean();
	}
	
}
