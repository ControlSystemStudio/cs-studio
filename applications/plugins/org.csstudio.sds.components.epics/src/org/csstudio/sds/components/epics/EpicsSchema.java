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
 package org.csstudio.sds.components.epics;

import java.util.HashSet;
import java.util.Set;

import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.model.initializers.AbstractControlSystemSchema;

/**
 * This initialization schema holds the property values of the EPICS control
 * system.
 *
 * @author Stefan Hofer
 * @version $Revision: 1.14 $
 *
 */
public final class EpicsSchema extends AbstractControlSystemSchema {

    @Override
    protected void initializeWidget() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<ConnectionState> getSupportedConnectionStates() {
        Set<ConnectionState> result = new HashSet<ConnectionState>();
        result.add(ConnectionState.CONNECTED);
        result.add(ConnectionState.CONNECTION_LOST);
        result.add(ConnectionState.INITIAL);
        result.add(ConnectionState.UNKNOWN);
        return result;
    }

    @Override
    protected String getDefaultBackgroundColor() {
        return "#E6E6E6";
    }

    @Override
    protected String getDefaultErrorColor() {
        return "#ff0000";
    }

    @Override
    protected String getDefaultForegroundColor() {
        return "#0000C0";
    }

    @Override
    protected String getDefaultRecordAlias() {
        return "channel";
    }



}
