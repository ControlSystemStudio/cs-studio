/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.model;

import org.eclipse.equinox.app.IApplicationContext;

/**
 * Listener called when application is started.
 *
 * @author Fred Arnaud (Sopra Group) - ITER
 *
 */
@SuppressWarnings("nls")
public interface IApplicationListener {

    /** ID of the extension point, defined in plugin.xml */
    final public static String EXTENSION_POINT = "org.csstudio.alarm.beast.notifier.start";

    /** Called just before notifier startup. */
    public void applicationStarted(final IApplicationContext context);
}
