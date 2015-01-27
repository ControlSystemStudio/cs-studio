/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.rdb;

import org.csstudio.alarm.beast.TreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.notifier.AlarmNotifier;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModelListener;

/**
 * Interface for alarm model wrapper.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public interface IAlarmRDBHandler extends AlarmClientModelListener {
	
	/** Initialize handler with {@link AlarmNotifier} */
	public void init(final AlarmNotifier notifier);
	
	/** Find item by path */
	public AlarmTreeItem findItem(String path);
	
	/** Get current {@link AlarmTreeRoot} */
	public TreeItem getAlarmTree();
	
	/** Release model */
	public void close();
}
