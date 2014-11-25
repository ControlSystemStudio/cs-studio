/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.css.product;

import org.csstudio.iter.css.product.util.WorkbenchUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

public class EarlyStartup implements IStartup {
	@Override
	public void earlyStartup() {
	
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				WorkbenchUtil.unbindDebugLast();
			}
		});
		
		// Remove Perspectives coming with XML Editor
		WorkbenchUtil.removeUnWantedPerspectives();

		WorkbenchUtil.removeUnWantedLog();
	}

}
