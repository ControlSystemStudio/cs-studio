/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.startup.module;

/**
 * <code>CSSExtensionPoint</code> is the default interface of the extension
 * points to be used by the CSS startup plug-in. It doesn't provide any
 * functionality. It serves only as an identifier that something is an extension
 * point to be handled at startup/shut down of the application
 * 
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * 
 */
public interface CSSStartupExtensionPoint
{
    /** ID of the extension point for adding CSS startup modules */
	public static final String NAME = "org.csstudio.startup.module"; //$NON-NLS-1$
}
