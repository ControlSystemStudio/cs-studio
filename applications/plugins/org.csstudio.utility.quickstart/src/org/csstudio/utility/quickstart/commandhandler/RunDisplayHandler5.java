package org.csstudio.utility.quickstart.commandhandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class RunDisplayHandler5 extends AbstractRunDisplayHandler {

	/**
	 * Call methods for sds file i from inherited class to open sds file.
	 * (This is the class for the 'handler' extension point.)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String[] sdsFileList = getFileList();
		openDisplay(sdsFileList, 5);
		return null;
	}
}