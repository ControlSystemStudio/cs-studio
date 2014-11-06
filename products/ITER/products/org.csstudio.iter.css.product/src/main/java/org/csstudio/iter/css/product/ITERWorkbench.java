/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.css.product;

import java.util.Map;

import org.csstudio.iter.css.product.util.WorkbenchUtil;
import org.csstudio.startup.application.OpenDocumentEventProcessor;
import org.csstudio.utility.product.Workbench;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.WorkbenchAdvisor;

/**
 * 
 * <code>ITERWorkbench</code> is a workbench that takes care of disabling some unneeded stuff.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ITERWorkbench extends Workbench {

    /*
     * (non-Javadoc)
     * @see org.csstudio.utility.product.Workbench#beforeWorkbenchCreation(org.eclipse.swt.widgets.Display, org.eclipse.equinox.app.IApplicationContext, java.util.Map)
     */
	@Override
	public Object beforeWorkbenchCreation(Display display,
			IApplicationContext context, Map<String, Object> parameters) {
		WorkbenchUtil.removeUnWantedLog();
		return super.beforeWorkbenchCreation(display, context, parameters);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.csstudio.utility.product.Workbench#createWorkbenchAdvisor(java.util.Map)
	 */
	@Override
	protected WorkbenchAdvisor createWorkbenchAdvisor(
			Map<String, Object> parameters) {
	    OpenDocumentEventProcessor openDocProcessor = (OpenDocumentEventProcessor) parameters.get(
		        OpenDocumentEventProcessor.OPEN_DOC_PROCESSOR);
		return new ITERWorkbenchAdvisor(openDocProcessor);
	}

}
