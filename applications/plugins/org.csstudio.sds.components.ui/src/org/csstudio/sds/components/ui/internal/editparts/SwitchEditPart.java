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

import org.csstudio.sds.components.common.SwitchPlugins;
import org.csstudio.sds.components.model.SwitchModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableSwitchFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * EditPart controller for the switch widget.
 *
 * @author jbercic
 *
 */
public final class SwitchEditPart extends AbstractWidgetEditPart {
    /**
     * Returns the casted model. This is just for convenience.
     *
     * @return the casted {@link SwitchModel}
     */
    protected SwitchModel getCastedModel() {
        return (SwitchModel) getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        SwitchModel model = getCastedModel();
        // create AND initialize the view properly
        final RefreshableSwitchFigure figure = new RefreshableSwitchFigure();

        figure.setTransparent(model.getTransparent());
        figure.setLineWidth(model.getLineWidth());
        figure.setType(model.getType());
        figure.setState(model.getState());
        figure.setRotation(model.getRotation());

        return figure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        // background transparency
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableSwitchFigure switchFigure = (RefreshableSwitchFigure) figure;
                switchFigure.setTransparent(((Boolean) newValue));
                return true;
            }
        };
        setPropertyChangeHandler(SwitchModel.PROP_TRANSPARENT, handle);

        // switch type
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableSwitchFigure switchFigure = (RefreshableSwitchFigure) figure;
                switchFigure.setType((Integer) newValue);
                return true;
            }
        };
        if (SwitchPlugins.names.length>0) {
            setPropertyChangeHandler(SwitchModel.PROP_TYPE, handle);
        }

        // switch state
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableSwitchFigure switchFigure = (RefreshableSwitchFigure) figure;
                switchFigure.setState((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(SwitchModel.PROP_STATE, handle);

        // rotation
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableSwitchFigure switchFigure = (RefreshableSwitchFigure) figure;
                switchFigure.setRotation((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(SwitchModel.PROP_ROTATE, handle);

        // line width
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableSwitchFigure switchFigure = (RefreshableSwitchFigure) figure;
                switchFigure.setLineWidth((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(SwitchModel.PROP_LINEWIDTH, handle);

        // widget width and height
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableSwitchFigure switchFigure = (RefreshableSwitchFigure) figure;
                switchFigure.resize();
                return true;
            }
        };
        setPropertyChangeHandler(SwitchModel.PROP_HEIGHT, handle);
        setPropertyChangeHandler(SwitchModel.PROP_WIDTH, handle);
    }
}
