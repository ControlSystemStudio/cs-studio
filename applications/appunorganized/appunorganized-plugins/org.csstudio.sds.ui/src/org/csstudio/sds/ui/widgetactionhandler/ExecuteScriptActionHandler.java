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

import org.csstudio.sds.internal.model.logic.ScriptEngine;
import org.csstudio.sds.internal.rules.LogicException;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.IScript;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.csstudio.sds.model.properties.actions.ExecuteScriptActionModel;
import org.csstudio.sds.ui.scripting.RunnableScript;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * Executes a script.
 *
 * @author Kai Meyer
 */
public final class ExecuteScriptActionHandler implements IWidgetActionHandler {

    private Map<AbstractWidgetModel, ScriptEngine> _engineMap;

    public ExecuteScriptActionHandler() {
        _engineMap = new HashMap<AbstractWidgetModel, ScriptEngine>();
    }

    /**
     * {@inheritDoc}
     *
     * @required action instanceof ExecuteScriptActionModel
     */
    @Override
    public void executeAction(final AbstractWidgetModel widget,
            final AbstractWidgetActionModel action) {
        assert action instanceof ExecuteScriptActionModel : "Precondition violated: action instanceof ExecuteScriptActionModel";
        ExecuteScriptActionModel valueAction = (ExecuteScriptActionModel) action;
        IPath scriptPath = valueAction.getScriptPath();
        boolean keepScriptStatus = valueAction.getKeepScriptStatus();

        try {
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
                    scriptPath);
            final ScriptEngine scriptEngine = determineEngine(widget,
                    file, keepScriptStatus);
            if (scriptEngine != null) {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        scriptEngine.processScript();
                    }
                });
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ScriptEngine determineEngine(AbstractWidgetModel widget,
            IFile file, boolean keepScriptEngine) throws LogicException,
            CoreException {
        ScriptEngine scriptEngine = null;
        if (keepScriptEngine) {
            if (!_engineMap.containsKey(widget)) {
                IScript script = new RunnableScript(file.getName(), file
                        .getContents());
                _engineMap.put(widget, new ScriptEngine(script));
            }
            scriptEngine = _engineMap.get(widget);
        } else {
            IScript script = new RunnableScript(file.getName(), file
                    .getContents());
            scriptEngine = new ScriptEngine(script);
        }
        return scriptEngine;
    }

}
