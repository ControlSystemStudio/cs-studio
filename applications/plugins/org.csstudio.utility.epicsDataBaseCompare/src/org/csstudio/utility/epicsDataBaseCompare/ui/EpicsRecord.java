/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.utility.epicsDataBaseCompare.ui;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 27.10.2009
 */
public class EpicsRecord implements Comparable<EpicsRecord> {

    private final String _recordName;
    private final SortedMap<String, Field> _fields = new TreeMap<String, Field>();
    private final EpicsDBFile _parent;
    private final String _recordType;

    public EpicsRecord(@Nonnull final EpicsDBFile parent, @Nonnull final String recordName, @Nonnull final String recordType) {
        _parent = parent;
        _recordName = recordName.trim();
        _recordType = recordType.trim();

    }
    @Nonnull
    public String getRecordName() {
        return _recordName;
    }

    public boolean isEmpty() {
        return _fields.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_fields == null ? 0 : _fields.hashCode());
        result = prime * result + (_recordName == null ? 0 : _recordName.hashCode());
        result = prime * result + (_recordType == null ? 0 : _recordType.hashCode());
        return result;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@CheckForNull final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EpicsRecord other = (EpicsRecord) obj;
        if (!_fields.equals(other._fields)) {
            return false;
        }
        if (!_recordName.equals(other._recordName)) {
            return false;
        }
        if (!_recordType.equals(other._recordType)) {
            return false;
        }
        return true;
    }

    @Override
    @Nonnull
    public String toString() {
        final StringBuilder sb = new StringBuilder(_recordName);
        sb.append("\t-");
        return sb.toString();
    }

    @Override
    public int compareTo(@Nonnull final EpicsRecord o) {
        final int compareTo = getRecordName().compareTo(o.getRecordName());
        if(compareTo!=0) {
            return compareTo;
        }
        final Collection<Field> filds1 = getFilds();
        final Collection<Field> filds2 = o.getFilds();
        if(filds1.size()!=filds2.size()) {
            return filds1.size()-filds2.size();
        }
        for (final Field field : filds2) {
            final Field field2 = o.getField(field.getField());
            if(field2==null||field.compareTo(field2)!=0) {
                return -1;
            }
        }
        return 0;
    }



    @CheckForNull
    public Field getField(@Nonnull final String field) {
        return _fields.get(field);
    }

    public void setField(@Nonnull final String field, @Nonnull final String value) {
        final String f = field.trim();
        _fields.put(f, new Field(this, f, value));
    }

    @Nonnull
    public Collection<Field> getFilds() {
        return _fields.values();
    }

    @Nonnull
    public EpicsDBFile getParent() {
        return _parent;
    }

    @Nonnull
    public String getSortetText() {
        final StringBuilder sb = new StringBuilder(_recordName);
        sb.append(" [").append(_recordType).append("]").append("\r\n");
        final Collection<Field> filds = getFilds();
        for (final Field field : filds) {
            sb.append("   ").append(field).append("\r\n");
        }
        return sb.toString();
    }

    @Nonnull
    public String getRecordType() {
        return _recordType;
    }

}
