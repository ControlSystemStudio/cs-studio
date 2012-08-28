package org.csstudio.utility.toolbox.common;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

public final class Constant {
	
	private Constant() {};
	
    public static final String PLUGIN_ID = "org.csstudio.utility.toolbox";
    public static final String NAME = "Toolbox";
    public static final String VERSION = " 1.0.0";
    public static final Color TEXT_PROPOSAL_INDICATOR_COLOR;

	static {
		Device device = Display.getCurrent();
		TEXT_PROPOSAL_INDICATOR_COLOR = new Color (device, 170, 225, 206);	
	}

}
