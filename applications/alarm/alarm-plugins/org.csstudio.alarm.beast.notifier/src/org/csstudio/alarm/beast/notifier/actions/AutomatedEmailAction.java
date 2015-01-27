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
import org.csstudio.alarm.beast.notifier.util.EMailCommandValidator;

/** Automated action that sends email.
 *
 *  <p>plugin.xml registers this for the "mailto" scheme.
 *  @author Kay Kasemir
 */
public class AutomatedEmailAction implements IActionProvider
{
    /** {@inheritDoc} */
    @Override
    public IActionValidator getValidator()
    {
        return new EMailCommandValidator();
    }

    /** {@inheritDoc} */
    @Override
    public IAutomatedAction getNotifier()
    {
        return new EmailActionImpl();
    }
}
