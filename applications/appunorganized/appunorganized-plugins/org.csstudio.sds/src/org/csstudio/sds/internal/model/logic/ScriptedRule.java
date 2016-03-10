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
package org.csstudio.sds.internal.model.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.internal.rules.LogicException;
import org.csstudio.sds.model.IRule;
import org.csstudio.sds.model.PropertyTypesEnum;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 * A rule that is based upon a script definition.
 *
 * @author Alexander Will
 * @version $Revision: 1.9 $
 *
 */
public class ScriptedRule implements IRule {
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
     * The name of the script field that contains the descriptions of the
     * script's parameters.
     */
    private static final String SCRIPT_PARAMETER_DESCRIPTIONS = "parameters"; //$NON-NLS-1$

    /**
     * The name of the script field that contains the descriptions of the
     * script's return value.
     */
    private static final String SCRIPT_PROPERTY_TYPES = "compatibleProperties";

    /**
     * The script function that will be executed.
     */
    private Function _scriptFunction;

    /**
     * A textual description of this rule.
     */
    private String _description;

    /**
     * The ID of this rule.
     */
    private String _id;

    /**
     * The descriptions of the parameters that are defined within the underlying
     * script.
     */
    private List<String> _parameterDescriptions;

    /**
     * The scripting scope.
     */
    private Scriptable _scriptScope;

    /**
     * The expected return type.
     */
    private PropertyTypesEnum[] _compatiblePropertyTypes;

    /**
     * Standard constructor.
     *
     * @param id
     *            The ID of this rule.
     * @param scriptFileInputStream
     *            An <code>InputStream</code> that contains the script that will
     *            be associated to rule.
     * @throws LogicException
     *             A <code>LogicException</code> is thrown an error occurs
     *             during the parsing of the script.
     */
    public ScriptedRule(final String id, final InputStream scriptFileInputStream)
            throws LogicException {
        _description = null;
        _parameterDescriptions = new ArrayList<String>();
        _id = id;

        try {
            Context scriptContext = Context.enter();
            _scriptScope = new ImporterTopLevel(scriptContext);
            _scriptFunction = parseScriptFile(scriptFileInputStream,
                    scriptContext);
        } catch (Exception e) {
            throw new LogicException(
                    "Script " + id + ": Error parsing script", e); //$NON-NLS-1$ //$NON-NLS-2$
        } finally {
            Context.exit();
        }

        if (_scriptFunction == null) {
            throw new LogicException(
                    "Script " + id + ": " + SCRIPT_LOGIC_FUNCTION_NAME + " is undefined!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        if (_description == null) {
            throw new LogicException("Script " + id //$NON-NLS-1$
                    + ": No textual description was defined!"); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized Object evaluate(final Object[] arguments) {
        Object result = Context.call(new ContextAction() {
            /**
             * {@inheritDoc}
             */
            @Override
            public Object run(final Context cx) {
                Object scriptResult = null;

                Object jsObject = Context.javaToJS(arguments, _scriptScope);

                Object callResult = _scriptFunction.call(cx, _scriptScope,
                        _scriptScope, new Object[] { jsObject });

                if (callResult instanceof NativeJavaObject) {
                    NativeJavaObject njo = (NativeJavaObject) callResult;

                    scriptResult = njo.unwrap();
                } else if (callResult != null) {
                    scriptResult = callResult;
                }

                return scriptResult;
            }
        });

        return result;
    }

    /**
     * Returns the expected return type.
     *
     * @return The expected return type
     */
    public PropertyTypesEnum[] getCompatiblePropertyTypes() {
        return _compatiblePropertyTypes;
    }

    /**
     * Return the textual description of this rule.
     *
     * @return The textual description of this rule.
     */
    @Override
    public final String getDescription() {
        return _description;
    }

    /**
     * Return the ID of this rule.
     *
     * @return The ID of this rule.
     */
    public final String getId() {
        return _id;
    }

    /**
     * Return the descriptions of the parameters that are defined within the
     * underlying script.
     *
     * @return The descriptions of the parameters that are defined within the
     *         underlying script.
     */
    public final String[] getParameterDescriptions() {
        return _parameterDescriptions.toArray(new String[_parameterDescriptions
                .size()]);
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

        _compatiblePropertyTypes = fetchCompatiblePropertyTypes();
        _description = fetchTextualDescription();
        _parameterDescriptions = fetchParameterDescriptions();

        // Try to allocate the function object.
        Object functionObject = _scriptScope.get(SCRIPT_LOGIC_FUNCTION_NAME,
                _scriptScope);

        if (functionObject instanceof Function) {
            result = (Function) functionObject;
        }

        return result;
    }

    /**
     * Try to fetch the return type from the script.
     */
    private PropertyTypesEnum[] fetchCompatiblePropertyTypes() {
        List<PropertyTypesEnum> result = new ArrayList<PropertyTypesEnum>();

        Object returnTypeObject = _scriptScope.get(SCRIPT_PROPERTY_TYPES,
                _scriptScope);

        if (returnTypeObject != Scriptable.NOT_FOUND) {
            String attribute = Context.toString(returnTypeObject);
            if (attribute != null) {
                String[] ids = attribute.split(",");
                for (String id : ids) {
                    try {
                        result.add(PropertyTypesEnum.createFromPortable(id
                                .trim()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return result.toArray(new PropertyTypesEnum[result.size()]);
    }

    /**
     * Try to fetch the textual description from the script.
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
     * Try to fetch the parameter descriptions from the script.
     */
    private List<String> fetchParameterDescriptions() {
        List<String> result = new ArrayList<String>();

        Object parameterDescriptionsObject = _scriptScope.get(
                SCRIPT_PARAMETER_DESCRIPTIONS, _scriptScope);

        if ((parameterDescriptionsObject instanceof NativeArray)) {
            NativeArray parameterDescriptions = (NativeArray) parameterDescriptionsObject;

            for (int i = 0; i < parameterDescriptions.getIds().length; i++) {
                Object o = parameterDescriptions.get(i, parameterDescriptions);
                if (o != null) {
                    result.add(o.toString());
                }
            }
        }

        return result;
    }

}
