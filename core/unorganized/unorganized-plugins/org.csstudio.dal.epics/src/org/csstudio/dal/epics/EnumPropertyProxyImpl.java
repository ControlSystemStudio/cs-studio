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

package org.csstudio.dal.epics;

import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;

import org.csstudio.dal.EnumPropertyCharacteristics;
import org.csstudio.dal.NumericPropertyCharacteristics;
import org.csstudio.dal.RemoteException;

/**
 * Enum property proxy implementation.
 * @author msekoranja
 */
public class EnumPropertyProxyImpl extends PropertyProxyImpl<Long> {

    /**
     * Constructor.
     * @param plug plug handling this property.
     * @param name property name.
     * @throws RemoteException
     */
    public EnumPropertyProxyImpl(final EPICSPlug plug, final String name)
            throws RemoteException {
        super(plug, name, Long.class, DBRType.ENUM);
    }

    @Override
    protected void createSpecificCharacteristics(final DBR dbr) {


        String[] names= (String[])getCharacteristics().get(EnumPropertyCharacteristics.C_ENUM_DESCRIPTIONS);

        if (names==null || names.length==0) {
            names= new String[16];
            final Object[] vals= new Object[16];
            for (int i = 0; i < names.length; i++) {
                names[i]="Value "+i;
                vals[i]= new Long(i);
            }
            getCharacteristics().put(EnumPropertyCharacteristics.C_ENUM_DESCRIPTIONS, names);
            getCharacteristics().put(EnumPropertyCharacteristics.C_ENUM_VALUES, vals);
        }

        getCharacteristics().put(NumericPropertyCharacteristics.C_MINIMUM, new Long(0));
        getCharacteristics().put(NumericPropertyCharacteristics.C_MAXIMUM, new Long(names.length));

        getCharacteristics().put(NumericPropertyCharacteristics.C_GRAPH_MIN, new Long(0));
        getCharacteristics().put(NumericPropertyCharacteristics.C_GRAPH_MAX, new Long(names.length));

    }
}
