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
package org.csstudio.dct.ui.graphicalviewer;

import org.csstudio.dct.ui.graphicalviewer.controller.ConnectionEditPart;
import org.csstudio.dct.ui.graphicalviewer.controller.ContainerNodeEditPart;
import org.csstudio.dct.ui.graphicalviewer.controller.GraphicalModelEditPart;
import org.csstudio.dct.ui.graphicalviewer.controller.RecordNodeEditPart;
import org.csstudio.dct.ui.graphicalviewer.model.Connection;
import org.csstudio.dct.ui.graphicalviewer.model.DctGraphicalModel;
import org.csstudio.dct.ui.graphicalviewer.model.InstanceNode;
import org.csstudio.dct.ui.graphicalviewer.model.PrototypeNode;
import org.csstudio.dct.ui.graphicalviewer.model.RecordNode;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * Factory which creates graphical controllers (aka GEF Editparts).
 *
 * @author Sven Wende
 *
 */
public final class DctEditPartFactory implements EditPartFactory {

    /**
     * Constructor.
     */
    public DctEditPartFactory() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EditPart createEditPart(final EditPart context, final Object modelElement) {
        EditPart part = getPartForModel(modelElement);

        // .. store widget model in EditPart
        if (part != null) {
            part.setModel(modelElement);
        }

        return part;
    }

    /**
     * Returns a controller for the specified model. If the model is unknown, no
     * controller is returned.
     *
     * @param model
     *            the model
     * @return a controller for the model
     */
    private EditPart getPartForModel(final Object model) {
        assert model != null;

        EditPart result = null;

        if (model instanceof DctGraphicalModel) {
            result = new GraphicalModelEditPart();
        } else if (model instanceof InstanceNode) {
            result = new ContainerNodeEditPart();
        } else if (model instanceof PrototypeNode) {
            result = new ContainerNodeEditPart();
        } else if (model instanceof RecordNode) {
            result = new RecordNodeEditPart();
        } else if (model instanceof Connection) {
            result = new ConnectionEditPart();
        }

        return result;
    }
}
