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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.net.IpAddress;
import org.csstudio.domain.desy.time.TimeInstant;


/**
 * Stores information about an IOC that has been retrieved from the file system.
 *
 * @author $Author$
 * @version $Revision$
 * @since 30.04.2008
 */
public class IOC implements Serializable {

    public static final String NO_GROUP = "<no group>";

    public static final String DEFAULT_RESPONSIBLE_PERSON = "bastian.knerr@desy.de";

    private static final long serialVersionUID = 1L;


    /**
     * The name of this IOC.
     */
    private final String _name;
    
    private IpAddress _ipAddress;

    /**
     * The group of this IOC.
     */
    private String _group = NO_GROUP;

    /**
     * The date time of last change.
     */
    private TimeInstant _lastUpdated;
    /**
     * The email address of the responsible person for this IOC.
     */
    private String _responsible = DEFAULT_RESPONSIBLE_PERSON;

    /**
     * Set of records in this IOC.
     */
    private final Map<String, Record> _records = new HashMap<String, Record>();


    /**
     * Constructor.
     * @param name .
     * @param dateTime time of last record update from file
     */
    public IOC(@Nonnull final String name, @Nonnull final TimeInstant dateTime) {
        this(name, NO_GROUP, dateTime, DEFAULT_RESPONSIBLE_PERSON);
    }

    /**
     * Constructor.
     * @param econ the IOC name
     * @param efan the facility name
     */
    public IOC(@Nonnull final String econ, @Nonnull final String efan) {
        this(econ, efan, null, DEFAULT_RESPONSIBLE_PERSON);
    }

    /**
     * Creates a new IOC information object.
     *
     * @param name the name of the IOC.
     * @param group the group of the IOC.
     * @param physicalName the physical name of the IOC.
     * @param dateTime time stamp of last update
     * @param resp responsible person for this IOC
     */
    public IOC(@Nonnull final String name,
               @Nullable final String group,
               @Nullable final TimeInstant dateTime,
               @Nullable final String resp) {
        _name = name;
        _group = group;
        _lastUpdated = dateTime;
        setResponsible(resp);
    }

    @CheckForNull
    public TimeInstant getLastBootTime() {
        return _lastUpdated;
    }

    public void setLastUpdated(@Nonnull final TimeInstant date) {
        _lastUpdated = date;
    }

    /**
     * Returns the group of this IOC.
     * @return the group of this IOC.
     */
    @Nonnull
    public final String getGroup() {
        return _group;
    }

    public void setGroup(@Nonnull final String group) {
        _group = group;
    }

    /**
     * Returns the name of this IOC.
     * @return the name of this IOC.
     */
    @Nonnull
    public final String getName() {
        return _name;
    }

    /**
     * Returns a copy of the set of records for this IOC.
     *
     * @return a copy of the records
     */
    @Nonnull
    public Set<Record> getRecordValues() {
        return new HashSet<Record>(_records.values());
    }

    @Nonnull
    public Map<String, Record> getRecords() {
        return new HashMap<String, Record>(_records);
    }

    @CheckForNull
    public Record getRecord(@Nonnull final String eren) {
        return _records.get(eren.toUpperCase());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final String toString() {
        return "IOC(name=" + _name +
                    ", group=" + _group +
                    ", responsible=" + _responsible +
                    ", last update=" + _lastUpdated + ")";
    }

    public void addRecord(@Nonnull final String eren) {
        final String erenKey = eren.toUpperCase();
        if (!_records.containsKey(erenKey)) {
            _records.put(erenKey, new Record(eren));
        }
    }

    @Nonnull
    public String getResponsible() {
        return _responsible;
    }

    public void setResponsible(@Nonnull final String responsible) {
        _responsible = responsible;
    }

    public void setIpAddress(@Nonnull final IpAddress ipAddress) {
        _ipAddress = ipAddress;
    }

    @CheckForNull
    public IpAddress getIpAddress() {
        return _ipAddress;
    }

}
