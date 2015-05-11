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

import org.csstudio.sds.components.model.MeterModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableMeterFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * The controller.
 *
 * @author jbercic
 *
 */
public final class MeterEditPart extends AbstractWidgetEditPart {
    /**
     * Returns the casted model. This is just for convenience.
     *
     * @return the casted {@link MeterModel}
     */
    protected MeterModel getCastedModel() {
        return (MeterModel) getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        MeterModel model = getCastedModel();
        // create AND initialize the view properly
        final RefreshableMeterFigure figure = new RefreshableMeterFigure();
        figure.setAngle(model.getAngle());
        figure.setAliases(model.getAliases());
        figure.setInnerAngle(model.getInnerAngle());
        figure.setNeedleColor(getModelColor(MeterModel.PROP_NEEDLECOLOR));
        figure.setVisibleRadius(model.getVisibleRadius());
        figure.setScaleRadius(model.getScaleRadius());
        figure.setMinorStep(model.getMinorStep());
        figure.setMajorStep(model.getMajorStep());
        figure.setMinValue(model.getMinValue());
        figure.setMaxValue(model.getMaxValue());
        figure.setValue(model.getValue());
        figure.setScaleColor(getModelColor(MeterModel.PROP_SCALECOLOR));
        figure.setScaleWidth(model.getScaleWidth());
        figure.setTextRadius(model.getTextRadius());
        figure.setTransparent(model.getTransparent());

        figure.setMColor(getModelColor(MeterModel.PROP_MCOLOR));
        figure.setLOLOColor(getModelColor(MeterModel.PROP_LOLOCOLOR));
        figure.setLOColor(getModelColor(MeterModel.PROP_LOCOLOR));
        figure.setHIColor(getModelColor(MeterModel.PROP_HICOLOR));
        figure.setHIHIColor(getModelColor(MeterModel.PROP_HIHICOLOR));

        figure.setMBound(model.getMBound());
        figure.setLOLOBound(model.getLOLOBound());
        figure.setLOBound(model.getLOBound());
        figure.setHIBound(model.getHIBound());
        figure.setHIHIBound(model.getHIHIBound());

        figure.setValuesFont(getModelFont(MeterModel.PROP_VALFONT));
        figure.setChannelFont(getModelFont(MeterModel.PROP_CHANFONT));

        figure.setDecimalPlaces(model.getPrecision());

        figure.setChannelName(model.getPrimaryPV());
        return figure;
    }

    /**
     * Registers color property change handlers.
     */
    protected void registerColorPropertyHandlers() {
        // needle
        setPropertyChangeHandler(MeterModel.PROP_NEEDLECOLOR, new ColorChangeHandler<RefreshableMeterFigure>(){
            @Override
            protected void doHandle(RefreshableMeterFigure figure, Color color) {
                figure.setNeedleColor(color);
            }
        });

        // scale
        setPropertyChangeHandler(MeterModel.PROP_SCALECOLOR, new ColorChangeHandler<RefreshableMeterFigure>(){
            @Override
            protected void doHandle(RefreshableMeterFigure figure, Color color) {
                figure.setScaleColor(color);
            }
        });

        //M area
        setPropertyChangeHandler(MeterModel.PROP_MCOLOR,  new ColorChangeHandler<RefreshableMeterFigure>(){
            @Override
            protected void doHandle(RefreshableMeterFigure figure, Color color) {
                figure.setMColor(color);
            }
        });

        //LOLO area
        setPropertyChangeHandler(MeterModel.PROP_LOLOCOLOR,  new ColorChangeHandler<RefreshableMeterFigure>(){
            @Override
            protected void doHandle(RefreshableMeterFigure figure, Color color) {
                figure.setLOLOColor(color);
            }
        });

        //LO area
        setPropertyChangeHandler(MeterModel.PROP_LOCOLOR,  new ColorChangeHandler<RefreshableMeterFigure>(){
            @Override
            protected void doHandle(RefreshableMeterFigure figure, Color color) {
                figure.setLOColor(color);
            }
        });

        //HI area
        setPropertyChangeHandler(MeterModel.PROP_HICOLOR,  new ColorChangeHandler<RefreshableMeterFigure>(){
            @Override
            protected void doHandle(RefreshableMeterFigure figure, Color color) {
                figure.setHIColor(color);
            }
        });

        //HIHI area
        setPropertyChangeHandler(MeterModel.PROP_HIHICOLOR,  new ColorChangeHandler<RefreshableMeterFigure>(){
            @Override
            protected void doHandle(RefreshableMeterFigure figure, Color color) {
                figure.setHIHIColor(color);
            }
        });

        // precision
        IWidgetPropertyChangeHandler precisionHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableMeterFigure meter = (RefreshableMeterFigure) refreshableFigure;
                meter.setDecimalPlaces((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_PRECISION, precisionHandler);

        // precision
        IWidgetPropertyChangeHandler channelHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                RefreshableMeterFigure meter = (RefreshableMeterFigure) refreshableFigure;
                meter.setChannelName((String) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_PRIMARY_PV, channelHandler);
    }

    /**
     * Registers boundary property change handlers for the five levels.
     */
    protected void registerBoundaryPropertyHandlers() {
        //M
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setMBound((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_MBOUND, handle);

        //LOLO
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setLOLOBound((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_LOLOBOUND, handle);

        //LO
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setLOBound((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_LOBOUND, handle);

        //HI
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setHIBound((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_HIBOUND, handle);

        //HIHI
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setHIHIBound((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_HIHIBOUND, handle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        // register handlers to deal with resizes
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.invalidateBackground();
                meterFigure.invalidateNeedle();
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_HEIGHT, handle);
        setPropertyChangeHandler(MeterModel.PROP_WIDTH, handle);
        // register a handler that deals with updates of the "angle" property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setAngle((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_ANGLE, handle);
        // register a handler that deals with updates of the "inner angle" property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setInnerAngle((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_INNANGLE, handle);
        // register a handler that deals with updates of the "visible radius" property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setVisibleRadius((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_RADIUS, handle);
        // register a handler that deals with updates of the "scale radius" property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setScaleRadius((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_SCALERADIUS, handle);
        // register a handler that deals with updates of the "minor step" property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setMinorStep((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_MINSTEP, handle);
        // register a handler that deals with updates of the "major step" property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setMajorStep((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_MAJSTEP, handle);
        // register a handler that deals with updates of the "minimum value" property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setMinValue((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_MINVAL, handle);
        // register a handler that deals with updates of the "maximum value" property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setMaxValue((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_MAXVAL, handle);
        // register a handler that deals with updates of the "value" property
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setValue((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_VALUE, handle);
        //scale line width change handler
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setScaleWidth((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_SCALEWIDTH, handle);
        //scale text area radius change handler
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setTextRadius((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_TEXTRADIUS, handle);
        //transparency change handler
        handle = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.setTransparent((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_TRANSPARENT, handle);

        //values font change handler
        setPropertyChangeHandler(MeterModel.PROP_VALFONT, new FontChangeHandler<RefreshableMeterFigure>(){
            @Override
            protected void doHandle(RefreshableMeterFigure figure, Font font) {
                figure.setValuesFont(font);
            }
        });


        //channel font change handler
        setPropertyChangeHandler(MeterModel.PROP_CHANFONT, new FontChangeHandler<RefreshableMeterFigure>(){
            @Override
            protected void doHandle(RefreshableMeterFigure figure, Font font) {
                figure.setChannelFont(font);
            }
        });

        IWidgetPropertyChangeHandler borderHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue, final Object newValue, final IFigure figure) {
                RefreshableMeterFigure meterFigure = (RefreshableMeterFigure) figure;
                meterFigure.refresh();
                return true;
            }
        };
        setPropertyChangeHandler(MeterModel.PROP_BORDER_WIDTH, borderHandler);
        setPropertyChangeHandler(MeterModel.PROP_BORDER_STYLE, borderHandler);
        registerColorPropertyHandlers();
        registerBoundaryPropertyHandlers();
    }

}
