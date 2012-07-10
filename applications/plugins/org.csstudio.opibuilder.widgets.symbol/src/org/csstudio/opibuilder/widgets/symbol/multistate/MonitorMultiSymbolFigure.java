package org.csstudio.opibuilder.widgets.symbol.multistate;

import org.csstudio.opibuilder.widgets.symbol.image.AbstractSymbolImage;
import org.csstudio.opibuilder.widgets.symbol.image.MonitorSymbolImage;

public class MonitorMultiSymbolFigure extends CommonMultiSymbolFigure {

	@Override
	protected AbstractSymbolImage createSymbolImage(boolean runMode) {
		MonitorSymbolImage msi = new MonitorSymbolImage(runMode);
		if (symbolProperties != null) {
			symbolProperties.fillSymbolImage(msi);
		}
		return msi;
	}
	
	public MonitorMultiSymbolFigure(boolean runMode) {
		super(runMode);
	}

}
