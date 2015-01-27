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
import org.csstudio.alarm.beast.notifier.util.SmsCommandValidator;

/** Automated action that sends SMS.
 *
 *  <p>plugin.xml registers this for the "smsto" scheme.
 *  @author Kay Kasemir
 */
public class AutomatedSmsAction implements IActionProvider
{
    /** {@inheritDoc} */
    @Override
	public IActionValidator getValidator() 
    {
		return new SmsCommandValidator();
	}

    /** {@inheritDoc} */
    @Override
    public IAutomatedAction getNotifier()
    {
        return new SmsActionImpl();
    }
}
