/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.logbook;

import org.csstudio.alarm.beast.notifier.model.IActionProvider;
import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;

/**
 * Automated action that creates a log.
 * <p>
 * plugin.xml registers this for the "log" scheme.
 * </p>
 *
 * @author Fred Arnaud (Sopra Group) - ITER
 *
 */
public class AutomatedLogAction implements IActionProvider {

    /** {@inheritDoc} */
    @Override
    public IActionValidator getValidator() {
        return new LogCommandValidator();
    }

    /** {@inheritDoc} */
    @Override
    public IAutomatedAction getNotifier() {
        return new LogActionImpl();
    }
}
