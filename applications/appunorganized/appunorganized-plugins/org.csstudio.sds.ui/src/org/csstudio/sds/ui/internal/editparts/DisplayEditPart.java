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
package org.csstudio.sds.ui.internal.editparts;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.ui.editparts.AbstractContainerEditPart;
import org.csstudio.sds.ui.editparts.ExecutionMode;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.csstudio.sds.ui.editparts.LayeredWidgetPane;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

/**
 * The EditPart implementation for synoptic display.
 *
 * @author Sven Wende
 *
 */
public final class DisplayEditPart extends AbstractContainerEditPart {

    /**
     * Constructor.
     */
    public DisplayEditPart() {
    }

    /**
     * Returns the {@link DisplayModel} of this DisplayEditPart.
     *
     * @return DisplayModel The {@link DisplayModel} of this DisplayEditPart
     */
    public DisplayModel getDisplayModel() {
        return (DisplayModel) getContainerModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        LayeredWidgetPane f = new LayeredWidgetPane(true);
        // mit setOpaque(true) wird der Hintergrund bunt aber die
        // Gitternetzlinien sind nicht mehr zu sehen
        // ist inzwischen im DisplayEditor integriert
        // f.setOpaque(true);
        f.setLayoutManager(new FreeformLayout());
        f.setBorder(new MarginBorder(5));

        f.setBackgroundColor(getModelColor(AbstractWidgetModel.PROP_COLOR_BACKGROUND));

        Point p = new Point(getWidgetModel().getX(), getWidgetModel().getY());
        f.setLocation(p);
        f.setSize(getWidgetModel().getWidth(), getWidgetModel().getHeight());
        f.setBorderBounds(getWidgetModel().getWidth(), getWidgetModel().getHeight());
        f.setShowBorder(getExecutionMode().equals(ExecutionMode.EDIT_MODE));
        return f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createEditPolicies() {
        super.createEditPolicies();

        // disallows the removal of this edit part from its parent
        installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        IWidgetPropertyChangeHandler boundsHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                if (figure instanceof LayeredWidgetPane) {
                    ((LayeredWidgetPane) figure).setBorderBounds(getCastedModel().getWidth(), getCastedModel().getHeight());
                }
                return true;
            }
        };
        setPropertyChangeHandler(DisplayModel.PROP_WIDTH, boundsHandler);
        setPropertyChangeHandler(DisplayModel.PROP_HEIGHT, boundsHandler);

        IWidgetPropertyChangeHandler displayBorderHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                if (figure instanceof LayeredWidgetPane) {
                    ((LayeredWidgetPane) figure).setShowBorder((Boolean) newValue);
                }
                return true;
            }
        };
        setPropertyChangeHandler(DisplayModel.PROP_DISPLAY_BORDER_VISIBILITY, displayBorderHandler);
    }

    @Override
    protected boolean determineChildrenSelectability() {
        return true;
    }

}
