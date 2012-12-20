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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.model.service;

import javax.annotation.Nonnull;

/**
 * This exception was throw when a Node not found.
 * The State indicates the level to which that node was not found.
 * 
 * @author Rickens Helge
 * @author $Author: $
 * @since 13.01.2011

 */
public class NodeNotFoundException extends Exception {
    
    private static final long serialVersionUID = 4655380215354299426L;
    private final State _state;
    
    /**
     * 
     * The State indicates the level to which that node was not found.
     *  
     * @author Rickens Helge
     * @author $Author: $
     * @since 13.01.2011
     */
    public enum State {
        DCT, DeviceDB;
    }
    
    /**
     * Constructor.
     */
    public NodeNotFoundException(@Nonnull final State state) {
        _state = state;
        
    }
    
    @Nonnull
    public State getState() {
        return _state;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getMessage() {
        String msg;
        switch (getState()) {
            case DCT:
                msg = "Channel nicht in DCT DB";
                break;
            case DeviceDB:
                msg = "IOName nicht in der DeviceDB";
                break;
            default:
                msg = "UNKNOWN";
        }
        return msg;
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getLocalizedMessage() {
        return getMessage();
    }
    
}
