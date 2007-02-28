package org.csstudio.utility.managementactions.actions;

import org.csstudio.platform.libs.dcf.actions.IAction;

public class ArrayStringAction implements IAction {

	public Object run(Object param) {
		return new String[] { "a", "B", "c", "d" };
	}

}
