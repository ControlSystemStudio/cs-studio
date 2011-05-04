/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.service.mysqlimpl.persistengine;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

import org.csstudio.platform.util.StringUtil;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 08.02.2011
 */
public enum SqlStatementBatch {
    INSTANCE;

    @GuardedBy("this")
    private long _sizeInBytes = 0;

    private final BlockingQueue<String> _statements = new LinkedBlockingQueue<String>();

    private SqlStatementBatch() {
        // Empty
    }
    public void submitStatement(@Nonnull final String statement) {
        _statements.add(statement); // non blocking add
        synchronized (this) {
            _sizeInBytes += StringUtil.getSizeInBytes(statement);
        }
    }

    @CheckForNull
    public String peek() {
        return _statements.peek();
    }

    @CheckForNull
    public synchronized String poll() {
        final String polled = _statements.poll();
        if (polled == null) {
            return null;
        }
        _sizeInBytes -= StringUtil.getSizeInBytes(polled);
        return polled;
    }

    @Nonnull
    public BlockingQueue<String> getQueue() {
        return _statements;
    }
    public synchronized long sizeInBytes() {
        return _sizeInBytes;
    }


}
