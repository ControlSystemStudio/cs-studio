
package org.csstudio.nams.service.history.extensionPoint;

import org.csstudio.nams.service.history.declaration.HistoryService;

public interface HistoryServiceFactory {

	public HistoryService createService();

}
