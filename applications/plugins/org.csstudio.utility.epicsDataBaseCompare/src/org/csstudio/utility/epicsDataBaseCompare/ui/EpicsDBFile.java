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
public class EpicsDBFile {

    private final SortedMap<String, EpicsRecord> _epicsRecords;
    private final String _fileName;

    public EpicsDBFile(@Nonnull final String fileName) {
        _fileName = fileName;
        _epicsRecords = new TreeMap<String, EpicsRecord>();
    }

    public final void add(@Nonnull final EpicsRecord epicsRecord) {
        _epicsRecords.put(epicsRecord.getRecordName(),epicsRecord);
    }

    @Nonnull
    public final SortedMap<String,EpicsRecord> getRecords(){
        return _epicsRecords;
    }

    @Nonnull
    public final String getFileName() {
        return _fileName;
    }

    @Override
    @Nonnull
    public final String toString() {
        final StringBuilder sb  = new StringBuilder(getFileName());
        sb.append("\r\n");
        for (final EpicsRecord epicsRecord : _epicsRecords.values()) {
            sb.append(epicsRecord);
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public final int size() {
        return _epicsRecords.size();
    }

    @CheckForNull
    public final EpicsRecord getRecord(@Nonnull final String recordName) {
        return _epicsRecords.get(recordName);

    }

    @Nonnull
    public final String getSortetText() {
        final StringBuilder sb  = new StringBuilder();
        for (final EpicsRecord epicsRecord : _epicsRecords.values()) {
            sb.append(epicsRecord.getSortetText());
            sb.append("\r\n");
        }
        return sb.toString();
    }

}
