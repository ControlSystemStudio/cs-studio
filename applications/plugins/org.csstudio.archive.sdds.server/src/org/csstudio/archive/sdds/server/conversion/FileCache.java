/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 */
package org.csstudio.archive.sdds.server.conversion;

import javax.annotation.Nonnull;

/**
 * @author Markus Moeller
 *
 */
public class FileCache {

    /*
    static struct FILECATCH {
        unsigned long status;
        simpleData *pstHistBuf;         // start position
        unsigned long   nRec;           // Number of records
        unsigned long   RecCoun;        // read records counter
      } fileCache;
    */

    private long status;
    private SimpleData historyBuffer;
    private long numberOfRecords;
    private long readCounter;

    public FileCache() {
        reset();
    }

    public void reset() {
        status = 0;
        historyBuffer = null;
        numberOfRecords = 0;
        readCounter = 0;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(final long status) {
        this.status = status;
    }

    @Nonnull
    public SimpleData getHistoryBuffer() {
        return historyBuffer;
    }

    public void setHistoryBuffer(@Nonnull final SimpleData historyBuffer) {
        this.historyBuffer = historyBuffer;
    }

    public long getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(final long numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public long getReadCounter() {
        return readCounter;
    }

    public void setReadCounter(final long readCounter) {
        this.readCounter = readCounter;
    }
}
