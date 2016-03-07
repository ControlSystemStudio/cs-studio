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

import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.internal.runmode.RunModeBoxInput;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.RuntimeContext;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.csstudio.sds.model.properties.actions.OpenDisplayActionModel;
import org.csstudio.sds.model.properties.actions.OpenDisplayActionTarget;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.eclipse.core.runtime.IPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Opens a display in a shell.
 *
 * @author Kai Meyer + Sven Wende
 */
public final class OpenDisplayActionHandler implements IWidgetActionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OpenDisplayActionHandler.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeAction(final AbstractWidgetModel widget,
            final AbstractWidgetActionModel action) {
        assert action instanceof OpenDisplayActionModel : "Precondition violated: action instanceof OpenDisplayWidgetAction";
        OpenDisplayActionModel displayAction = (OpenDisplayActionModel) action;
        IPath path = displayAction.getResource();

        // resolve the forwarded aliases

        // ... we take all aliases that are in the namespace of the widget that
        // was used to execute this action
        Map<String, String> allAliases = widget.getAllInheritedAliases();

        // ... the aliases that are forwarded to the new display are configured
        // on the action
        Map<String, String> forwardedAliases = displayAction.getAliases();

        // ... we resolve the forwarded aliases using all known information
        for (String key : forwardedAliases.keySet()) {
            String raw = forwardedAliases.get(key);

            String resolved;
            try {
                resolved = ChannelReferenceValidationUtil.createCanonicalName(
                        raw, allAliases);

                forwardedAliases.put(key, resolved);
            } catch (ChannelReferenceValidationException e) {
                // ignore
                LOG.info("Cannot resolve alias [" + raw + "]");
            }

        }

        // close the parent display if necessary

        boolean shouldClose = SdsUiPlugin.getCorePreferenceStore().getBoolean(
                PreferenceConstants.PROP_CLOSE_PARENT_DISPLAY);

        if (displayAction.getClose() || shouldClose) {
            RuntimeContext rtc = widget.getRoot().getRuntimeContext();

            if (rtc != null) {
                RunModeService.getInstance().closeRunModeBox(
                        rtc.getRunModeBoxInput());
            }
        }

        // open the new display in a shell or view
        if (displayAction.getTarget() == OpenDisplayActionTarget.SHELL) {
            RunModeService.getInstance().openDisplayShellInRunMode(
                    path,
                    forwardedAliases,
                    (RunModeBoxInput) widget.getRoot().getRuntimeContext()
                            .getRunModeBoxInput());
        } else if (displayAction.getTarget() == OpenDisplayActionTarget.VIEW) {
            RunModeService.getInstance().openDisplayViewInRunMode(path,
                    forwardedAliases);
        } else {
            throw new IllegalArgumentException("Not implemented yet.");
        }
    }

}
