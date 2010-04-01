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
package org.csstudio.utility.ldap.model;

import java.io.Serializable;
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
public class IOC implements Serializable {

    public static final String NO_GROUP = "<no group>";

    public static final String DEFAULT_RESPONSIBLE_EMAILADDRESS = "klaus.valett@desy.de";

    private static final long serialVersionUID = 1L;


    /**
     * The name of this IOC.
     */
    private String _name;

    /**
     * The group of this IOC.
     */
    private String _group = NO_GROUP;

    /**
     * The physical name of this IOC.
     */
    private String _physicalName;

    /**
     * The date time of last change.
     */
    private GregorianCalendar _dateTime;
    /**
     * The email address of the responsible person for this IOC.
     */
    private String _responsible = DEFAULT_RESPONSIBLE_EMAILADDRESS;

    /**
     * Set of records in this IOC.
     */
    private final Map<String, Record> _records = new HashMap<String, Record>();


    public IOC(final String name, final GregorianCalendar dateTime) {
        this(name, "<no group>", "<no physicalname>", dateTime, DEFAULT_RESPONSIBLE_EMAILADDRESS);
    }

    public IOC(final String econ, final String efan) {
        this(econ, efan, "<no physicalname>", null, DEFAULT_RESPONSIBLE_EMAILADDRESS);
    }

    /**
     * Creates a new IOC information object.
     *
     * @param name the name of the IOC.
     * @param group the group of the IOC.
     * @param physicalName the physical name of the IOC.
     * @param dateTime
     */
    public IOC(final String name, final String group, final String physicalName, final GregorianCalendar dateTime, final String resp) {
        _name = name;
        _group = group;
        _physicalName = physicalName;
        _dateTime = dateTime;
        setResponsible(resp);
    }


    public GregorianCalendar getDateTime() {
        return _dateTime;
    }

    public void setDateTime(final GregorianCalendar date) {
        _dateTime = date;
    }

    /**
     * Returns the group of this IOC.
     * @return the group of this IOC.
     */
    public final String getGroup() {
        return _group;
    }

    public void setGroup(final String group) {
        _group = group;
    }

    /**
     * Returns the name of this IOC.
     * @return the name of this IOC.
     */
    public final String getName() {
        return _name;
    }

    public void setName(final String name) {
        _name = name;
    }

    /**
     * Returns the physical name of this IOC.
     * @return the physical name of this IOC.
     */
    public final String getPhysicalName() {
        return _physicalName;
    }

    public final void setPhysicalName(final String physicalName) {
        _physicalName = physicalName;
    }

    /**
     * Returns a copy of the set of records for this IOC.
     *
     * @return a copy of the records
     */
    public Set<Record> getRecordValues() {
        return new HashSet<Record>(_records.values());
    }

    public Map<String, Record> getRecords() {
        return new HashMap<String, Record>(_records);
    }

    public Record getRecord(final String eren) {
        return _records.get(eren.toUpperCase());
    }




    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return "IOC(name=" + _name + ", group=" + _group + ", phys=" + _physicalName + ")";
    }

    public void addRecord(final String eren) {
        final String erenKey = eren.toUpperCase();
        if (!_records.containsKey(erenKey)) {
            _records.put(erenKey, new Record(eren));
        }
    }

    public String getResponsible() {
        return _responsible;
    }

    public void setResponsible(final String responsible) {
        _responsible = responsible;
    }

}
