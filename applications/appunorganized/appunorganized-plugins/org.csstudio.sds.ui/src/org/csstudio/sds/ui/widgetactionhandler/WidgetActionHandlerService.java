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

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ActionType;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The service performs the action depending on the given {@link ActionType}.
 * @author Kai Meyer
 *
 */
public final class WidgetActionHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(WidgetActionHandlerService.class);

    /**
     * The instance of the service.
     */
    private static WidgetActionHandlerService _instance;
    /**
     * The map of known {@link IWidgetActionHandler}.
     */
    private Map<ActionType, IWidgetActionHandler> _handler;

    /**
     * Constructor.
     */
    private WidgetActionHandlerService() {
        initHandlers();
    }

    /**
     * Creates and registers {@link IWidgetActionHandler}.
     */
    private void initHandlers() {
        _handler = new HashMap<ActionType, IWidgetActionHandler>();
        _handler.put(ActionType.OPEN_DISPLAY, new OpenDisplayActionHandler());
        _handler.put(ActionType.COMMIT_VALUE, new CommitValueActionHandler());
        _handler.put(ActionType.EXECUTE_SCRIPT, new ExecuteScriptActionHandler());
        _handler.put(ActionType.OPEN_DATA_BROWSER, new OpenDataBrowserActionHandler());
    }

    /**
     * Returns the instance of the {@link WidgetActionHandlerService}.
     * @return The instance.
     */
    public static WidgetActionHandlerService getInstance() {
        if (_instance==null) {
            _instance = new WidgetActionHandlerService();
        }
        return _instance;
    }

    /**
     * Performs the action depending on the given {@link ActionType}.
     * @param widget The {@link AbstractWidgetModel}
     * @param action The type of the action
     */
    public void performAction(final AbstractWidgetModel widget, final AbstractWidgetActionModel action) {
        if (action.isEnabled() && _handler.containsKey(action.getType())) {
            _handler.get(action.getType()).executeAction(widget, action);
        } else {
            this.doUnknownAction();
        }
    }

    /**
     * Performs the unspecified  action.
     */
    private void doUnknownAction() {
        LOG.info("Unknown WidgetAction performed!");
    }

}
