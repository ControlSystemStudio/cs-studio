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
package org.csstudio.sds.model.initializers;

import java.util.HashSet;
import java.util.Set;

import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.model.AbstractWidgetModel;

/**
 * An initialization schema defines default settings that are applied to
 * every widget in the course of its creation.
 *
 * @author Sven Wende
 * @version $Revision: 1.12 $
 *
 */
public abstract class AbstractControlSystemSchema extends AbstractInitializer {

    /**
     * Constructor.
     */
    public AbstractControlSystemSchema() {
        // Constructor.
    }

    /**
     * Returns the connection states that should be supported, when this schema
     * is active. This will effect the dynamics wizard which will offer only
     * supported states for selection. By default all known connection states
     * are supported. Inheriting class may override this method and return a
     * reduced set of connection states.
     *
     * @return the supported connection states
     */
    protected Set<ConnectionState> getSupportedConnectionStates() {
        // by default, all states are supported
        Set<ConnectionState> result = new HashSet<ConnectionState>();

        for (ConnectionState state : ConnectionState.values()) {
            result.add(state);
        }

        return result;
    }

    public void initialize() {
        initializeWidgetDefaults();
        initializeWidget();
    }

    private void initializeWidgetDefaults() {
        // background color
        initializeStaticProperty(AbstractWidgetModel.PROP_COLOR_BACKGROUND,
                getDefaultBackgroundColor());

        // foreground color
        initializeStaticProperty(AbstractWidgetModel.PROP_COLOR_FOREGROUND,
                getDefaultForegroundColor());

        if (getDefaultRecordAlias() != null) {
            // primary pv
            initializeStaticProperty(AbstractWidgetModel.PROP_PRIMARY_PV,
                    getDefaultRecordAliasAsVariable());

            // alias
            initializeAlias(getDefaultRecordAlias(), "");
        }
    }

    /**
     * Called for every widget that is initialized. Subclasses may apply
     * settings which are valid in general for all widgets.
     *
     */
    protected abstract void initializeWidget();

    protected abstract String getDefaultBackgroundColor();

    protected abstract String getDefaultForegroundColor();

    protected abstract String getDefaultErrorColor();

    protected abstract String getDefaultRecordAlias();

    protected String getDefaultRecordAliasAsVariable() {
        String result = null;

        String alias = getDefaultRecordAlias();

        if (alias != null) {
            result = "$" + alias + "$";
        }

        return result;
    }
}
