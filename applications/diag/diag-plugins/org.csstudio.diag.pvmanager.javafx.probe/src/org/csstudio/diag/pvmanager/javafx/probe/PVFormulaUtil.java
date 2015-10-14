package org.csstudio.diag.pvmanager.javafx.probe;

import org.diirt.datasource.CompositeDataSource;
import org.diirt.datasource.DataSource;
import org.diirt.datasource.PVManager;

public class PVFormulaUtil {

	public static String channelWithDataSource(String channel) {
		if (channel == null) {
			return null;
		}
		
		DataSource defaultDS = PVManager.getDefaultDataSource();
		String pvName = channel;
		if (defaultDS instanceof CompositeDataSource) {
			CompositeDataSource composite = (CompositeDataSource) defaultDS;
			if (!pvName.contains(composite.getConfiguration().getDelimiter())) {
				pvName = composite.getConfiguration().getDefaultDataSource()
						+ composite.getConfiguration().getDelimiter() + pvName;
			}
		}

		return pvName;
	}


}
