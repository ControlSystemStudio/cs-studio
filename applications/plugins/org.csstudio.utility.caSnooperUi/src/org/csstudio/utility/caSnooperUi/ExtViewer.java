package org.csstudio.utility.caSnooperUi;

import org.csstudio.platform.libs.dcf.ui.IOpenViewer;
import org.csstudio.platform.libs.dcf.ui.ViewCreator;

public class ExtViewer implements IOpenViewer {

	public ExtViewer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ViewCreator getCreator() {
		ViewCreator creator = new SnooperViewCreator();
		return creator;
	}

}
