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
package org.csstudio.sds.ui.scripting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.csstudio.sds.internal.rules.LogicException;
import org.csstudio.sds.model.IScript;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;

/**
 * This calls describes a script, which can be executed by a Timer-widget.
 * @author Kai Meyer
 *
 */
public final class RunnableScript implements IScript {

    /**
     * The name of the script function that will be executed.
     */
    private static final String SCRIPT_LOGIC_FUNCTION_NAME = "execute"; //$NON-NLS-1$
    /**
     * The name of the script field that contains the textual description of the
     * script.
     */
    private static final String SCRIPT_DESCRIPTION = "description"; //$NON-NLS-1$
    /**
     * A textual description of this rule.
     */
    private String _description;
    /**
     * The script function that will be executed.
     */
    private Function _scriptFunction;
    /**
     * The scripting scope.
     */
    private Scriptable _scriptScope;

    /**
     * Constructor.
     * @param name The name of the script.
     * @param scriptFileInputStream The {@link InputStream} for the script
     * @throws LogicException Thrown if an error occurs during parsing the script
     */
    public RunnableScript(final String name, final InputStream scriptFileInputStream)
        throws LogicException {
        _description = null;

        try {
            Context scriptContext = Context.enter();
            scriptContext.setApplicationClassLoader(RunnableScript.class.getClassLoader());

            _scriptScope = new ImporterTopLevel(scriptContext);

            _scriptFunction = parseScriptFile(scriptFileInputStream,
                    scriptContext);
        } catch (Exception e) {
            throw new LogicException(
                    "Script " + name + ": Error parsing script", e); //$NON-NLS-1$ //$NON-NLS-2$
        } finally {
            Context.exit();
        }

        if (_scriptFunction == null) {
            throw new LogicException(
                    "Script " + name + ": " + SCRIPT_LOGIC_FUNCTION_NAME + " is undefined!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        if (_description == null) {
            throw new LogicException("Script " + name //$NON-NLS-1$
                    + ": No textual description was defined!"); //$NON-NLS-1$
        }
    }

    /**
     * Parse the given script file and try to allocate the "doLogic" function.
     *
     * @param scriptFileInputStream
     *            The input stream that contains the script
     * @param scriptContext
     *            The script context that is used to initially parse the script
     *            file.
     * @return The "doLogic" function or null if it is not defined within the
     *         given script.
     * @throws IOException
     *             If an IO error occurs while the script file is parsed.
     */
    private Function parseScriptFile(final InputStream scriptFileInputStream,
            final Context scriptContext) throws IOException {
        Function result = null;

        String scriptString = ""; //$NON-NLS-1$

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                scriptFileInputStream));

        while (reader.ready()) {
            scriptString += reader.readLine();
        }

        reader.close();

        // Evaluate the script string. Ignore the result. This is needed to
        // allocate the proper function object.
        scriptContext.evaluateString(_scriptScope, scriptString,
                "script file", 1, null); //$NON-NLS-1$

        _description = fetchTextualDescription();

        // Try to allocate the function object.
        Object functionObject = _scriptScope.get(SCRIPT_LOGIC_FUNCTION_NAME,
                _scriptScope);

        if (functionObject instanceof Function) {
            result = (Function) functionObject;
        }

        return result;
    }

    /**
     * Try to fetch the textual description from the script.
     * @return The textual description for the script
     */
    private String fetchTextualDescription() {
        String result = "";
        Object descriptionObject = _scriptScope.get(SCRIPT_DESCRIPTION,
                _scriptScope);

        if (descriptionObject != Scriptable.NOT_FOUND) {
            result = Context.toString(descriptionObject);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        Context.call(new ContextAction() {
            /**
             * {@inheritDoc}
             */
            @Override
            public Object run(final Context cx) {
                _scriptFunction.call(cx, _scriptScope,
                        _scriptScope, new Object[] {});
                return null;
            }
        });
    }

}
