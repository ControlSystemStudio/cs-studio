/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.actions;

import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.alarm.beast.notifier.model.IActionProvider;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;

/** Automated action that executes an external command.
 *
 *  <p>plugin.xml registers this for the "cmd" scheme.
 *  @author Kay Kasemir
 */
public class AutomatedCommandAction implements IActionProvider
{
    /** {@inheritDoc} */
    @Override
    public IActionValidator getValidator()
    {
        // No validator
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public IAutomatedAction getNotifier()
    {
        return new CommandActionImpl();
    }
}
