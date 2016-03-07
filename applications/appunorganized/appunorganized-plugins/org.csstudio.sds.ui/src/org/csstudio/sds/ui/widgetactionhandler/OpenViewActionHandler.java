/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.sds.ui.widgetactionhandler;

import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.csstudio.sds.model.properties.actions.OpenDisplayActionModel;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.core.runtime.IPath;

/**
 * Opens a display in a view.
 *
 * @deprecated 8-7-2008: swende: {@link OpenShellActionHandler} und
 *             {@link OpenViewActionHandler} zusammenf�hren, wenn nur noch eine
 *             parametrisierbare Action-Type (Open Display) existiert
 * @author Kai Meyer
 *
 * @deprecated we use {@link OpenDisplayActionHandler} instead
 */
public final class OpenViewActionHandler implements IWidgetActionHandler {

    /**
     * {@inheritDoc}
     *
     * @required action instanceof OpenDisplayWidgetAction
     */
    @Override
    public void executeAction(final AbstractWidgetModel widget,
            final AbstractWidgetActionModel action) {
        assert action instanceof OpenDisplayActionModel : "Precondition violated: action instanceof OpenDisplayWidgetAction";
        OpenDisplayActionModel displayAction = (OpenDisplayActionModel) action;
        IPath path = displayAction.getResource();
        Map<String, String> newAlias = displayAction.getAliases();
        RunModeService.getInstance().openDisplayViewInRunMode(path, newAlias);
    }

}
