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
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.csstudio.sds.model.properties.actions.OpenDisplayActionModel;
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
 * @author Kai Meyer
 *
 * @deprecated we use {@link OpenDisplayActionHandler} instead
 */
public final class OpenShellActionHandler implements IWidgetActionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OpenShellActionHandler.class);

    /**
     * {@inheritDoc}
     *
     * @required action instanceof OpenDisplayWidgetAction
     */
    @Override
    public void executeAction(final AbstractWidgetModel widget, final AbstractWidgetActionModel action) {
        assert action instanceof OpenDisplayActionModel : "Precondition violated: action instanceof OpenDisplayWidgetAction";
        OpenDisplayActionModel displayAction = (OpenDisplayActionModel) action;
        IPath path = displayAction.getResource();

        Map<String, String> allAliases = widget.getAllInheritedAliases();

        Map<String, String> forwardedAliases = displayAction.getAliases();

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

        RunModeService.getInstance().openDisplayShellInRunMode(
                path,
                forwardedAliases,
                (RunModeBoxInput) widget.getRoot().getRuntimeContext()
                        .getRunModeBoxInput());

        boolean shouldClose = SdsUiPlugin.getCorePreferenceStore().getBoolean(
                PreferenceConstants.PROP_CLOSE_PARENT_DISPLAY);
        if (shouldClose) {
            RunModeBoxInput runModeBoxInput = (RunModeBoxInput) widget.getRoot()
                    .getRuntimeContext().getRunModeBoxInput();
            RunModeService.getInstance().closeRunModeBox(runModeBoxInput);
        }
    }

}
