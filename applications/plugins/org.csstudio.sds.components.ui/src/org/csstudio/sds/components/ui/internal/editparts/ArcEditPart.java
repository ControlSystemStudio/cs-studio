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
 package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.ArcModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableArcFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;

/**
 * EditPart controller for the arc widget.
 *
 * @author jbercic
 *
 */
public final class ArcEditPart extends AbstractWidgetEditPart {

    /**
     * Returns the casted model. This is just for convenience.
     *
     * @return the casted {@link ArcModel}
     */
    protected ArcModel getCastedModel() {
        return (ArcModel) getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        ArcModel model = getCastedModel();
        // create AND initialize the view properly
        final RefreshableArcFigure figure = new RefreshableArcFigure();

        figure.setTransparent(model.getTransparent());
        figure.setBorderWidth(model.getBorderWidth());
        figure.setStartAngle(model.getStartAngle());
        figure.setAngle(model.getAngle());
        figure.setLineWidth(model.getLineWidth());
        figure.setFill(model.getFill());
        figure.setFillColor(getModelColor(ArcModel.PROP_FILLCOLOR));

        return figure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        // changes to the transparency property
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
                arcFigure.setTransparent((Boolean)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ArcModel.PROP_TRANSPARENT, handle);

        // changes to the border width property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
                arcFigure.setBorderWidth((Integer)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ArcModel.PROP_BORDER_WIDTH, handle);

        // changes to the start angle property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
                arcFigure.setStartAngle((Integer)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ArcModel.PROP_STARTANGLE, handle);

        // changes to the angle property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
                arcFigure.setAngle((Integer)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ArcModel.PROP_ANGLE, handle);

        // changes to the line width property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
                arcFigure.setLineWidth((Integer)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ArcModel.PROP_LINEWIDTH, handle);

        // changes to the filled property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableArcFigure arcFigure = (RefreshableArcFigure) figure;
                arcFigure.setFill((Boolean)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ArcModel.PROP_FILLED, handle);

        // fill
        setPropertyChangeHandler(ArcModel.PROP_FILLCOLOR, new ColorChangeHandler<RefreshableArcFigure>(){
            @Override
            protected void doHandle(RefreshableArcFigure figure, Color color) {
                figure.setFillColor(color);
            }
        });
    }
}
