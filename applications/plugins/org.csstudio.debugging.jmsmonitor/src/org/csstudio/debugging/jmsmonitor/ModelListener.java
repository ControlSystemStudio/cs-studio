/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.debugging.jmsmonitor;

/** Interface for model notifications
 *  @author Kay Kasemir
 */
public interface ModelListener
{
    /** Invoked when something in the model changed: Server, new messages, ... */
    void modelChanged(Model model);
}
