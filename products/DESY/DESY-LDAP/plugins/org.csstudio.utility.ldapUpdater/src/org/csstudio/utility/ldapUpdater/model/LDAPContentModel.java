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
package org.csstudio.utility.ldapUpdater.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.naming.directory.Attributes;


/**
 *
 * @author bknerr
 */
public class LDAPContentModel {

    private final Map<String, Facility> _facilities = new HashMap<String, Facility>();

    /**
     * Constructor.
     */
    public LDAPContentModel() {
        // Empty.
    }


    public Facility addFacility(final String efan) {
        final String efanKey = efan.toUpperCase();
        if (!_facilities.containsKey(efanKey)) {
            _facilities.put(efanKey, new Facility(efan));
        }
        return _facilities.get(efanKey);
    }


    public IOC addIOC(final String efan, final String econ) {
        return addIOC(efan, econ, null);
    }

    public IOC addIOC(final String efan, final String econ, final Attributes attributes) {
        final Facility facility = addFacility(efan);
        return facility.addIOC(efan, econ, attributes);
    }


    public void addRecord(final String efan, final String econ, final String eren) {
        final IOC ioc = addIOC(efan, econ);
        ioc.addRecord(eren);
    }


    private Facility getFacility(final String efan) {
        return _facilities.get(efan.toUpperCase());
    }

    public IOC getIOC(final String iocName) {
        for (final Entry<String, Facility> entry : _facilities.entrySet()) {
            final IOC ioc = getIOC(entry.getValue().getName(), iocName);
            if (ioc != null) {
                return ioc;
            }
        }
        return null;
    }

    public IOC getIOC(final String efan, final String iocName) {
        final Facility facility = getFacility(efan);
        if (facility != null) {
            return facility.getIOC(iocName);
        }
        return null;
    }

    /**
     * Returns a copy of the set of all IOC names (of all facilities).
     * @return a copy of the set of all IOC names.
     */
    public Set<String> getIOCNames() {
        final Set<String> names = new HashSet<String>();
        for (final Facility fac : _facilities.values()) {
            names.addAll(fac.getIocNames());
        }
        return names;
    }


    public Record getRecord(final String group, final String iocName, final String recordName) {
        final IOC ioc = getIOC(group, iocName);
        if (ioc != null) {
            return ioc.getRecord(recordName);
        }
        return null;
    }

    public Set<Record> getRecords(final String iocName) {
        final IOC ioc = getIOC(iocName);
        return ioc.getRecordValues();
    }


    public Set<IOC> getIOCs() {
        final Set<IOC> iocs = new HashSet<IOC>();
        for (final Facility fac : _facilities.values()) {
            iocs.addAll(fac.getIOCs());
        }
        return iocs;

    }


    /**
     * @return
     */
    public Set<Entry<String, Facility>> getFacilities() {
        return _facilities.entrySet();
    }

}
