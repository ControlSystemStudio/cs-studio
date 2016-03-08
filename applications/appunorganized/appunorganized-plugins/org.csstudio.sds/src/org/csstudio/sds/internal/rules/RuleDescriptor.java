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
package org.csstudio.sds.internal.rules;

import org.csstudio.sds.model.IRule;
import org.csstudio.sds.model.PropertyTypesEnum;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * Descriptor for rules.
 *
 * @author Alexander Will
 * @version $Revision: 1.2 $
 *
 */
public final class RuleDescriptor implements IAdaptable {
    /**
     * The ID of the rule.
     */
    private String _ruleId;

    /**
     * The textual description of the rule.
     */
    private String _description;

    /**
     * The textual descriptions of the expected parameters.
     */
    private String[] _parameterDescriptions;

    /**
     * The return type of the rule output.
     */
    private PropertyTypesEnum[] _returnType;

    /**
     * The described rule.
     */
    private IRule _rule;

    /**
     * Flag that indicates if the rule is scripted (the opposite is hard-coded
     * in java).
     */
    private boolean _isScriptedRule;

    /**
     * Standard constructor.
     *
     * @param ruleId
     *            The ID of the rule.
     * @param description
     *            The textual description of the rule.
     * @param parameterDescriptions
     *            The textual descriptions of the expected parameters.
     * @param parameterTypes
     *            The types of the expected parameters.
     * @param compatiblePropertyTypes
     *            The type of the return value.
     * @param rule
     *            The described rule.
     * @param isScriptedRule
     *            Flag that indicates if the rule is scripted (the opposite is
     *            hard-coded in java).
     */
    public RuleDescriptor(final String ruleId, final String description,
            final String[] parameterDescriptions,
            final PropertyTypesEnum[] compatiblePropertyTypes,
            final IRule rule, final boolean isScriptedRule) {
        assert ruleId != null;
        assert description != null;
        assert parameterDescriptions != null;
        assert rule != null;
        _ruleId = ruleId;
        _description = description;
        _parameterDescriptions = parameterDescriptions;
        _returnType = compatiblePropertyTypes;
        _rule = rule;
        _isScriptedRule = isScriptedRule;
    }

    /**
     * Return the textual description of the rule.
     *
     * @return The textual description of the rule.
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Return the textual descriptions of the expected parameters.
     *
     * @return The textual descriptions of the expected parameters.
     */
    public String[] getParameterDescriptions() {
        return _parameterDescriptions;
    }

    /**
     * Returns the return type of the rule.
     *
     * @return the rule´s return type
     */
    public PropertyTypesEnum[] getCompatiblePropertyTypes() {
        return _returnType;
    }

    /**
     * Return the ID of the rule.
     *
     * @return The ID of the rule.
     */
    public String getRuleId() {
        return _ruleId;
    }

    /**
     * Return the described rule.
     *
     * @return The described rule.
     */
    public IRule getRule() {
        return _rule;
    }

    /**
     * Return whether the rule is scripted or hard-coded in java.
     *
     * @return True, if the rule is scripted or false if it's hard-coded in
     *         java.
     */
    public boolean isScriptedRule() {
        return _isScriptedRule;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(getDescription());
        sb.append("\n\nExpected parameters: "); //$NON-NLS-1$
        sb.append(_parameterDescriptions.length);
        sb.append("\n"); //$NON-NLS-1$

        for (int i = 0; i < _parameterDescriptions.length; i++) {
            sb
                    .append("\tParameter " + i + ": " + _parameterDescriptions[i] + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

}
