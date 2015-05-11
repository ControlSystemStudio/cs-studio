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
 package org.csstudio.sds.ui.editparts;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.feedback.IGraphicalFeedbackFactory;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * The EditPolicy for {@link DisplayModel}. It can be used with
 * <code>Figures</code> in {@link XYLayout}. The constraint for XYLayout is a
 * {@link org.eclipse.draw2d.geometry.Rectangle}.
 *
 * This policy is optimized for the runmode.
 *
 * @author Sven Wende
 */
final class RunModeXYLayoutEditPolicy extends XYLayoutEditPolicy {

    /**
     * Overriden, to provide a generic EditPolicy for children, which is aware
     * of different feedback and selection handles. {@inheritDoc}
     */
    @Override
    protected EditPolicy createChildEditPolicy(final EditPart child) {
        return new GenericChildEditPolicy(child);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createChangeConstraintCommand(EditPart child,
            Object constraint) {
        // Not supported in Run Mode
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command getCreateCommand(CreateRequest request) {
        // Not supported in Run Mode
        return null;
    }

    /**
     * Provides support for selecting, positioning, and resizing an editpart. By
     * default, selection is indicated via eight square handles along the
     * editpart's figure, and a rectangular handle that outlines the editpart
     * with a 1-pixel black line. The eight square handles will resize the
     * current selection in the eight primary directions. The rectangular handle
     * will drag the current selection using a {@link
     * org.eclipse.gef.tools.DragEditPartsTracker}.
     * <P>
     * By default, during feedback, a rectangle filled using XOR and outlined
     * with dashes is drawn. This feedback can be tailored by contributing a
     * {@link IGraphicalFeedbackFactory} via the extension point
     * org.csstudio.sds.graphicalFeedbackFactories.
     *
     * @author Sven Wende
     *
     */
    protected final class GenericChildEditPolicy extends ResizableEditPolicy {
        /**
         * The edit part.
         */
        private final EditPart _child;

        /**
         * Standard constructor.
         *
         * @param child
         *            An edit part.
         */
        protected GenericChildEditPolicy(final EditPart child) {
            _child = child;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        protected List createSelectionHandles() {
            // get default handles
            List handleList = super.createSelectionHandles();

            // add contributed handles
            assert _child.getModel() instanceof AbstractWidgetModel : "widget models must be derived from AbstractWidgetModel"; //$NON-NLS-1$"

            return new ArrayList();

        }
    }

}
