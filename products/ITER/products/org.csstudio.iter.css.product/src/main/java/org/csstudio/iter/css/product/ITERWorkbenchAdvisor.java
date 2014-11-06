/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.css.product;

import org.csstudio.iter.css.product.util.WorkbenchUtil;
import org.csstudio.startup.application.OpenDocumentEventProcessor;
import org.csstudio.utility.product.ApplicationWorkbenchAdvisor;
import org.eclipse.ui.application.IWorkbenchConfigurer;

/**
 * 
 * <code>ITERWorkbenchAdvisor</code> is an extension of default advisor that disables
 * certain perspectives, logs and bindings.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ITERWorkbenchAdvisor extends ApplicationWorkbenchAdvisor {

	public ITERWorkbenchAdvisor(OpenDocumentEventProcessor openDocProcessor) {
		super(openDocProcessor);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.csstudio.utility.product.ApplicationWorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
	 */
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		WorkbenchUtil.removeUnWantedPerspectives();
		WorkbenchUtil.unbindDuplicateBindings();
		
		
	}

}
