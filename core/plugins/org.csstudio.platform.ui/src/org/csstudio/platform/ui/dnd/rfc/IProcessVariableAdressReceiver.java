/**
 * 
 */
package org.csstudio.platform.ui.dnd.rfc;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.eclipse.swt.dnd.DropTargetEvent;

public interface IProcessVariableAdressReceiver {
	void receive(IProcessVariableAddress[] pvs, DropTargetEvent event);
}