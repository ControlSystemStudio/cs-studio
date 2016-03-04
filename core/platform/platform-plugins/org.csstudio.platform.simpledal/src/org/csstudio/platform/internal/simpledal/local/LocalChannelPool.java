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
package org.csstudio.platform.internal.simpledal.local;

import java.util.concurrent.ConcurrentHashMap;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;

public final class LocalChannelPool {
    /**
     * Contains all active channels.
     */
    private ConcurrentHashMap<IProcessVariableAddress, LocalChannel> _channels;

    /**
     * The singleton instance.
     */
    private static LocalChannelPool _instance;

    /**
     * Constructor.
     */
    private LocalChannelPool() {
        _channels = new ConcurrentHashMap<IProcessVariableAddress, LocalChannel>();
    }

    /**
     * Gets the singleton instance.
     *
     * @return the singleton instance
     */
    public static synchronized LocalChannelPool getInstance() {
        if (_instance == null) {
            _instance = new LocalChannelPool();
        }

        return _instance;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized LocalChannel getChannel(
            final IProcessVariableAddress processVariable, ValueType valueType) {
        assert processVariable != null;

        LocalChannel channel = _channels.get(processVariable);

        if (channel == null) {
            channel = new LocalChannel(processVariable);
            _channels.put(processVariable, channel);
        }

        return channel;
    }
}
