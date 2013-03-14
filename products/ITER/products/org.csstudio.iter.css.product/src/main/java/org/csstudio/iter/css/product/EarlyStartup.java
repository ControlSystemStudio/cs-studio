package org.csstudio.iter.css.product;

import org.csstudio.iter.css.product.util.WorkbenchUtil;
import org.eclipse.ui.IStartup;

public class EarlyStartup implements IStartup {

	@Override
	public void earlyStartup() {
		WorkbenchUtil.removeUnWantedPerspectives();
	}

}
