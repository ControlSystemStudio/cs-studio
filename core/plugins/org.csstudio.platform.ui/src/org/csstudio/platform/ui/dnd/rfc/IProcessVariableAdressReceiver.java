/**
 * 
 */
package org.csstudio.platform.ui.dnd.rfc;

import org.csstudio.platform.model.pvs.IProcessVariableAdress;
import org.eclipse.swt.dnd.DropTargetEvent;

public interface IProcessVariableAdressReceiver {
	void receive(IProcessVariableAdress[] pvs, DropTargetEvent event);
}