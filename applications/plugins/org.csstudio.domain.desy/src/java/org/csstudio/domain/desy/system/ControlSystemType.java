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
package org.csstudio.domain.desy.system;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * This is until the great awesome abstraction of the different 'control system' used at DESY.
 * Where, unfortunately, 'control system' is not defined. I just learned that TINE is 'not' a
 * control system in the sense EPICS is supposed to be... (ask S. Rettig-Labusga).
 *
 * Open questions:
 * see {@link org.csstudio.platform.model.pvs.ControlSystemEnum}
 *
 * @author bknerr
 * @since 09.02.2011
 */
public enum ControlSystemType {
    EPICS_V3(CSCommunicationProtocol.CA),
    DOOCS(CSCommunicationProtocol.TINE),
    TANGO(CSCommunicationProtocol.CORBA);

    private final ImmutableSet<CSCommunicationProtocol> _protocols;

    /**
     * Constructor.
     * @param prot at least one protocol has to be specified per control system type
     * @param prots further permitted protocols by this control system type
     */
    private ControlSystemType(@Nonnull final CSCommunicationProtocol prot,
                              @Nonnull final CSCommunicationProtocol... prots) {
        _protocols = Sets.immutableEnumSet(prot, prots);
    }

    @Nonnull
    public ImmutableSet<CSCommunicationProtocol> getProtocols() {
        return _protocols;
    }
}
