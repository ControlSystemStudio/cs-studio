package org.csstudio.askap.logviewer.util;

import org.csstudio.askap.utility.icemanager.LogObject;

public interface LogMessageDataModel {
	public LogObject[] getMessages();

	public int getSize();
}
