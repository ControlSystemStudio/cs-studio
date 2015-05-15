/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.model;

import org.csstudio.sds.model.properties.actions.CommitValueActionModelFactory;
import org.csstudio.sds.model.properties.actions.ExecuteScriptActionModelFactory;
import org.csstudio.sds.model.properties.actions.IActionModelFactory;
import org.csstudio.sds.model.properties.actions.OpenDataBrowserActionModelFactory;
import org.csstudio.sds.model.properties.actions.OpenDisplayActionModelFactory;

/**
 * The types that can be used for the property ActionData.
 *
 * @author Kai Meyer
 *
 */
public enum ActionType {
    /**
     * Opens a display.
     */
    OPEN_DISPLAY("Open Display","icons/openshell2.gif", new OpenDisplayActionModelFactory()),

    /**
     *
     */
    OPEN_SHELL("Open Display (Deprecated)","icons/openshell2.gif", new OpenDisplayActionModelFactory()),

    /**
     * Commit a value.
     */
    COMMIT_VALUE("Send Channel Value", "icons/widgetaction.gif", new CommitValueActionModelFactory()),

    /**
     * Executes a script.
     */
    EXECUTE_SCRIPT("Execute Script", "icons/widgetaction.gif", new ExecuteScriptActionModelFactory()),

    OPEN_DATA_BROWSER("Open Data Browser", "icons/openshell2.gif", new OpenDataBrowserActionModelFactory());

    /**
     * The title of this {@link ActionType}.
     */
    private String _title;
    /**
     * The {@link IActionModelFactory} of this {@link ActionType}.
     */
    private IActionModelFactory _actionFactory;

    /**
     * Path to an icon used for workbench representations of the action type.
     */
    private String _icon;

    /**
     * Constructor.
     *
     * @param title
     *            The title of this {@link ActionType}
     * @param factory
     *            The {@link IActionModelFactory} for the WidgetAction.
     */
    private ActionType(final String title, String icon,
            final IActionModelFactory factory) {
        assert title != null;
        assert icon != null;
        assert factory != null;

        _title = title;
        _icon = icon;
        _actionFactory = factory;
    }

    /**
     * Returns the title of the {@link ActionType}.
     *
     * @return The title of the {@link ActionType}
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Returns the {@link IActionModelFactory}.
     *
     * @return The factory
     */
    public IActionModelFactory getActionFactory() {
        return _actionFactory;
    }

    /**
     * Returns the path to an icon used for workbench representations of the
     * action type.
     *
     * @return icon path
     */
    public String getIcon() {
        return _icon;
    }

}
