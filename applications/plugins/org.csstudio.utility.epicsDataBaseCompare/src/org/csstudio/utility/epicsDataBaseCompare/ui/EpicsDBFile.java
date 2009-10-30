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

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 27.10.2009
 */
public class EpicsDBFile {

    private String _file;
    private SortedSet<EpicsRecord> _epicsRecords;

    public EpicsDBFile(String file) {
        setFile(file);
        _epicsRecords = new TreeSet<EpicsRecord>(new Comparator<EpicsRecord>() {
            @Override
            public int compare(EpicsRecord o1, EpicsRecord o2) {
                return o1.getRecordName().compareTo(o2.getRecordName());
            }
        }); 
    }

    public void add(EpicsRecord epicsRecord) {
        _epicsRecords.add(epicsRecord);
    }

    private void setFile(String file) {
        _file = file;
    }

    public String getFile() {
        return _file;
    }
    
    @Override
    public String toString() {
        StringBuilder sb  = new StringBuilder(getFile());
        sb.append("\r\n");
        for (EpicsRecord epicsRecord : _epicsRecords) {
            sb.append(epicsRecord);
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public int size() {
        return _epicsRecords.size();
    }

    public EpicsRecord get(int pos) {
        return _epicsRecords.toArray(new EpicsRecord[0])[pos];
    }

}
