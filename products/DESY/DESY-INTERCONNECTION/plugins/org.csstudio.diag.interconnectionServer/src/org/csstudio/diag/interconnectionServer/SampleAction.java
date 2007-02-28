package org.csstudio.diag.interconnectionServer;

import java.util.Map;

import org.csstudio.platform.libs.dcf.actions.IAction;

public class SampleAction implements IAction {

	public Object run(Object param) {
		if(!(param instanceof Map))
			return null;
		
		Map m = (Map)param;
		String p1 = m.get("param1").toString();
		String p2 = m.get("param2").toString();
		
		return p1.concat(p2);
	}

}
