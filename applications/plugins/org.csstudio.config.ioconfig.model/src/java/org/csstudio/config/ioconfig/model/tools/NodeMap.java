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
 * $Id: NodeMap.java,v 1.3 2010/08/20 13:33:08 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.tools;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
 * @since 22.10.2009
 */
public final class NodeMap {
    
    private static Map<Integer, AbstractNodeDBO<?,?>> _NODE_MAP = new HashMap<Integer, AbstractNodeDBO<?,?>>();
    private static int _COUNT_ASSEMBLE_EPICS_ADDRESS_STRING;
    private static int _LOCAL_UPDATE;
    private static int _CHANNEL_CONFIG_COMPOSITE;

    private NodeMap() {
        // Constructor
    }

    public static void countAssembleEpicsAddressString() {
        _COUNT_ASSEMBLE_EPICS_ADDRESS_STRING++;
    }
    
    public static void countChannelConfigComposite() {
        _CHANNEL_CONFIG_COMPOSITE++;
    }
    
    public static void countlocalUpdate() {
        _LOCAL_UPDATE++;
    }
    
    @CheckForNull
    public static AbstractNodeDBO<?,?> get(@Nonnull final Integer key) {
        return _NODE_MAP.get(key);
    }
    
    public static int getChannelConfigComposite() {
        return _CHANNEL_CONFIG_COMPOSITE;
    }
    
    public static int getCountAssembleEpicsAddressString() {
        return _COUNT_ASSEMBLE_EPICS_ADDRESS_STRING;
    }
    
    public static int getLocalUpdate() {
        return _LOCAL_UPDATE;
    }
    
    public static int getNumberOfNodes() {
        return _NODE_MAP.size();
    }
    
    public static void put(@Nonnull final Integer key, @Nonnull final AbstractNodeDBO<?,?> value) {
        _NODE_MAP.put(key, value);
    }
}
