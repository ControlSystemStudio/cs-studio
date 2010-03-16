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

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Stores information about an IOC.
 * 
 * @author $Author$
 * @version $Revision$
 * @since 30.04.2008
 */
public class IOC {
    
    public static final String NO_GROUP = "<no group>";
    
    /**
     * The name of this IOC.
     */
    private final String _name;
    
    /**
     * The group of this IOC.
     */
    private String _group = NO_GROUP;
    
    /**
     * The physical name of this IOC.
     */
    private final String _physicalName;
    
    /**
     * Set of records in this IOC.
     */
    private final Map<String, Record> _records = new HashMap<String, Record>();
    
    //    /**
    //     * room for record names of the current ioc read from file
    //     */
    //    private List<String> _iocRecordNamesFromFile = Collections.emptyList();
    //
    //    /**
    //     * room for record names of the current ioc read from ldap
    //     */
    //    private List<String> iocRecordNamesFromLDAP = new ArrayList<String>();
    
    /**
     * The date time of last change.
     */
    private final GregorianCalendar _dateTime;
    
    public IOC(final String name, final GregorianCalendar dateTime) {
        this(name, "<no group>", "<no physicalname>", dateTime);
    }
    
    public IOC(final String econ, final String efan) {
        this(econ, efan, "<no physicalname>", null);
    }
    
    /**
     * Creates a new IOC information object.
     * 
     * @param name the name of the IOC.
     * @param group the group of the IOC.
     * @param physicalName the physical name of the IOC.
     * @param dateTime
     */
    public IOC(final String name, final String group, final String physicalName, final GregorianCalendar dateTime) {
        _name = name;
        _group = group;
        _physicalName = physicalName;
        _dateTime = dateTime;
    }
    
    public void addRecord(final String eren) {
        if (!_records.containsKey(eren)) {
            _records.put(eren, new Record(eren));
        }
    }
    
    public GregorianCalendar getDateTime() {
        return _dateTime;
    }
    
    /**
     * Returns the group of this IOC.
     * @return the group of this IOC.
     */
    public final String getGroup() {
        return _group;
    }
    
    /**
     * Returns the name of this IOC.
     * @return the name of this IOC.
     */
    public final String getName() {
        return _name;
    }
    
    /**
     * Returns the physical name of this IOC.
     * @return the physical name of this IOC.
     */
    public final String getPhysicalName() {
        return _physicalName;
    }
    
    public Record getRecord(final String eren) {
        return _records.get(eren);
    }
    
    /**
     * Returns a copy of the set of records for this IOC.
     * 
     * @return a copy of the records
     */
    public Set<Record> getRecords() {
        return new HashSet<Record>(_records.values());
    }
    
    public void setGroup(final String group) {
        _group = group;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return "IOC(name=" + _name + ", group=" + _group + ", phys=" + _physicalName + ")";
    }
}
