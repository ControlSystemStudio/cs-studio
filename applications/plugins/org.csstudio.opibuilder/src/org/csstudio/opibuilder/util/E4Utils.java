/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Utility methods for migrating BOY to E4.
 * 
 * @author Xihui Chen
 */
public class E4Utils {
	
	/**Open perspective.
	 * PlatformUI.getWorkbench().showPerspective doesn't work for e4, see 
	 * http://stackoverflow.com/questions/11523187/switch-perspective-in-a-rcp-application-since-eclipse-juno		
	 * @param id perspective ID.
	 * @param page active page.
	 */
	public static void showPerspective(String id, IWorkbenchPage page){
		IPerspectiveRegistry registry = PlatformUI.getWorkbench().getPerspectiveRegistry();
		page.setPerspective(registry.findPerspectiveWithId(id));
	}
}