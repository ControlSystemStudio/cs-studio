/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
 * MAY FIND A COPY AT {@link http://www.desy.de/legal/license.htm}
 */
package de.desy.language.editor.ui.editor.highlighting;

import java.util.List;

import org.eclipse.jface.text.rules.IRule;

import de.desy.language.libraries.utils.contract.Contract;

/**
 * This class specifies the minimum offers of a rule-provider for
 * source-code-highlighting to be used with the abstract class
 * {@link LanguageEditor}. Concrete this class to implement your own provider.
 *
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.2
 */
public abstract class AbstractRuleProvider {

//    /**
//     * Registered listeners.
//     *
//     * @deprecated Not more in use.
//     */
//    @Deprecated
//    private final List<IRuleProviderListener> _listeners = new LinkedList<IRuleProviderListener>();

//    /**
//     * Add a listener to react on events on the rule provider.
//     *
//     * @deprecated Not more in use.
//     */
//    @Deprecated
//    final public void addIRuleProviderListener(
//            final IRuleProviderListener listener) {
//        if (!this._listeners.contains(listener)) {
//            this._listeners.add(listener);
//        }
//    }

    /**
     * Creates the list of rules for parsing the source. This list and
     * containing rules should be recreated any time this method is called.
     * Rules will be ordered in kind of requirements.
     *
     * @return A list of rules in required order, may not null.
     */
    protected abstract List<IRule> doCreateCustomRules();

    /**
     * Creates the list of rules for parsing the source. This list and
     * containing rules should be recreated any time this method is called.
     * Rules will be proceed in order given in list.
     *
     * @return A list of rules in required order, may not null.
     */
    final public List<IRule> getCustomRules() {
        final List<IRule> createdCustomRules = this.doCreateCustomRules();

        Contract.ensureResultNotNull(createdCustomRules);
        return createdCustomRules;
    }

//    /**
//     * Informs all registered listeners.
//     *
//     * @deprecated Not more in use.
//     */
//    @Deprecated
//    public final void refresh() {
//        for (final IRuleProviderListener listener : this._listeners) {
//            listener.refreshOccurrs();
//        }
//    }

//    /**
//     * Removes a listener to react on events on the rule provider.
//     *
//     * @deprecated Not more in use.
//     */
//    @Deprecated
//    final public void removeIRuleProviderListener(
//            final IRuleProviderListener listener) {
//        this._listeners.remove(listener);
//    }
}
