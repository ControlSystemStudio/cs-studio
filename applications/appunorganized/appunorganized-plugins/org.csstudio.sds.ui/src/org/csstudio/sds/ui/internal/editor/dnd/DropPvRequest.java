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
package org.csstudio.sds.ui.internal.editor.dnd;

import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.requests.DropRequest;

/**
 * A Request representing a drop of a PV.
 *
 * @author Sven Wende, Kai Meyer
 */
public final class DropPvRequest extends org.eclipse.gef.Request implements DropRequest {
    /**
     * The identifier of the type.
     */
    public static final String REQ_DROP_PV = "REQ_DROP_PV";

    /**
     * The drop location.
     */
    private Point _location;

    /**
     * The dropped process variables..
     */
    private List<IProcessVariableAddress> _droppedProcessVariables;

    /**
     * Constructor.
     */
    public DropPvRequest() {
        setType(REQ_DROP_PV);
    }

    /**
     * Sets the location of the drop.
     *
     * @param location
     *            the location of the drop
     */
    public void setLocation(final Point location) {
        _location = location;
    }

    /**
     * Returns the location of the drop.
     *
     * @return Point The location of the drop.
     */
    public Point getLocation() {
        return _location;
    }

    /**
     * Sets the dropped process variables.
     *
     * @param droppedProcessVariables
     *            the dropped process variables.
     */
    public void setDroppedProcessVariables(final List<IProcessVariableAddress> droppedProcessVariables) {
        _droppedProcessVariables = droppedProcessVariables;
    }

    /**
     * Returns the dropped process variables
     *
     * @return String The name of the PV.
     */
    public List<IProcessVariableAddress> getDroppedProcessVariables() {
        return _droppedProcessVariables;
    }

}
