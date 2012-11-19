package org.csstudio.opibuilder.widgets.symbol.image;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.Color;

public class ControlSymbolImage extends AbstractSymbolImage {

	public ControlSymbolImage(boolean runMode) {
		super(runMode);
	}
	
	public final static Color DISABLE_COLOR = CustomMediaFactory.getInstance().getColor(
			CustomMediaFactory.COLOR_GRAY);	
	
	/** The alpha (0 is transparency and 255 is opaque) for disabled paint */
	public static final int DISABLED_ALPHA = 100;
	
}
