/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.eclipse.jface.action.Action;

/** Context menu wrapper for {@link OpenDisplayAction}.
 *
 *  <p>On a simple click, the underlying OpenDisplayAction
 *  is executed.
 *
 *  <p>When opening the context menu on a widget
 *  with actions, instances of this class will be listed,
 *  all referring to the same OpenDisplayAction, but allowing
 *  user to open the display in different ways.
 *
 *  @author Xihui Chen - Original author
 *  @author Kay Kasemir
 */
public class OpenRelatedDisplayAction extends Action
{
    public enum OpenDisplayTarget
    {
        DEFAULT("Open"),
        NEW_TAB("Open in Workbench Tab"),
        NEW_WINDOW("Open in New Workbench"),
        NEW_SHELL("Open in Standalone Window");

        final private String description;

        private OpenDisplayTarget(final String desc)
        {
            description = desc;
        }

        @Override
        public String toString()
        {
            return description;
        }
    }

    final private OpenDisplayAction openDisplayAction;

    final private OpenDisplayTarget  target;

    public OpenRelatedDisplayAction(final OpenDisplayAction openDisplayAction,
            final OpenDisplayTarget target)
    {
        this.openDisplayAction = openDisplayAction;
        this.target = target;
        setText(target.toString());
    }

    @Override
    public void run()
    {
        switch (target)
        {
        case NEW_TAB:
            openDisplayAction.runWithModifiers(true, false);
            break;
        case NEW_WINDOW:
            openDisplayAction.runWithModifiers(false, true);
            break;
        case NEW_SHELL:
            openDisplayAction.runWithModifiers(true, true);
            break;
        default:
            openDisplayAction.run();
            break;
        }
    }
}
