/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.scriptUtil.ConsoleUtil;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.ByteMonitorModel;
import org.csstudio.simplepv.VTypeHelper;
import org.csstudio.swt.widgets.figures.ByteMonitorFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.osgi.util.NLS;
import org.diirt.vtype.VType;

/**
 * This class implements the widget edit part for the Byte Monitor widget.  This
 * displays the bits in a value as s series of LEDs
 * @author hammonds
 * @author Takashi Nakamoto - added labels property
 */
public class ByteMonitorEditPart extends AbstractPVWidgetEditPart {


    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart#getValue()
     */
    @Override
    public Object getValue() {
        return ((ByteMonitorFigure)getFigure()).getValue();
    }

    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Object value) {
        if(value instanceof Integer)
            ((ByteMonitorFigure)getFigure()).setValue((Integer)value);
        else if (value instanceof Long)
            ((ByteMonitorFigure)getFigure()).setValue((Long)value);
        else if (value instanceof Double)
            ((ByteMonitorFigure)getFigure()).setValue((Double)value);
        else if (value instanceof Number)
            ((ByteMonitorFigure)getFigure()).setValue(((Number)value).longValue());
        else
            super.setValue(value);
    }

    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.editparts.AbstractBaseEditPart#doCreateFigure()
     */
    @Override
    protected IFigure doCreateFigure() {
        ByteMonitorModel model = (ByteMonitorModel) getWidgetModel();

        ByteMonitorFigure fig = new ByteMonitorFigure();
        setModel(model);
        setFigure(fig);
        fig.setStartBit(((Integer)model.getPropertyValue(ByteMonitorModel.PROP_START_BIT)) );
        fig.setNumBits(((Integer)model.getPropertyValue(ByteMonitorModel.PROP_NUM_BITS)) );
        fig.setHorizontal(((Boolean)model.getPropertyValue(ByteMonitorModel.PROP_HORIZONTAL)) );
        fig.setReverseBits(((Boolean)model.getPropertyValue(ByteMonitorModel.PROP_BIT_REVERSE)) );
        fig.setPackedLEDs(((Boolean)model.getPropertyValue(ByteMonitorModel.PROP_PACK_LEDS)) );
        fig.setLedBorderColor(((OPIColor)model.getPropertyValue(ByteMonitorModel.PROP_LED_BORDER_COLOR)).getSWTColor());
        fig.setLedBorderWidth(((Integer)model.getPropertyValue(ByteMonitorModel.PROP_LED_BORDER)) );
        fig.setSquareLED(((Boolean)model.getPropertyValue(ByteMonitorModel.PROP_SQUARE_LED)) );
        fig.setOnColor(((OPIColor)model.getPropertyValue(ByteMonitorModel.PROP_ON_COLOR)).getSWTColor() );
        fig.setOffColor(((OPIColor)model.getPropertyValue(ByteMonitorModel.PROP_OFF_COLOR)).getSWTColor() );
        fig.setEffect3D((Boolean)getPropertyValue(ByteMonitorModel.PROP_EFFECT3D));
        fig.setLabels(model.getLabels());
        fig.setValue(0x1111);
        fig.drawValue();

        return fig;
    }

    @Override
    public ByteMonitorModel getWidgetModel() {
        return (ByteMonitorModel) super.getWidgetModel();
    }

    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.editparts.AbstractBaseEditPart#registerPropertyChangeHandlers()
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        super.registerBasePropertyChangeHandlers();
        getFigure().setEnabled(getWidgetModel().isEnabled() &&
                (getExecutionMode() == ExecutionMode.RUN_MODE));

        removeAllPropertyChangeHandlers(AbstractWidgetModel.PROP_ENABLED);

        //enable
        IWidgetPropertyChangeHandler enableHandler = new IWidgetPropertyChangeHandler(){
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                if(getExecutionMode() == ExecutionMode.RUN_MODE)
                    figure.setEnabled((Boolean)newValue);
                return false;
            }
        };
        setPropertyChangeHandler(AbstractWidgetModel.PROP_ENABLED, enableHandler);

        // PV_Value
        IWidgetPropertyChangeHandler pvhandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                boolean succeed = true;
                if((newValue != null) && (newValue instanceof VType) ){
                    Number number = VTypeHelper.getNumber(((VType) newValue));
                    if (number != null) {
                        setValue(number);
                    } else
                        succeed = false;

                }
                else {
                    succeed = false;
                }
                if(!succeed){
                    setValue(0);
                    ConsoleUtil.writeError(NLS.bind(
                            "{0} does not accept non-numeric value.",
                            getWidgetModel().getName()));
                }
                return false;
            }
        };
        setPropertyChangeHandler(ByteMonitorModel.PROP_PVVALUE, pvhandler);

        // on color
        IWidgetPropertyChangeHandler colorHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ByteMonitorFigure figure = (ByteMonitorFigure) refreshableFigure;
                figure.setOnColor(((OPIColor) newValue).getSWTColor());
                figure.drawValue();
                return true;
            }
        };
        setPropertyChangeHandler(ByteMonitorModel.PROP_ON_COLOR, colorHandler);

        // off color
        colorHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ByteMonitorFigure figure = (ByteMonitorFigure) refreshableFigure;
                figure.setOffColor(((OPIColor) newValue).getSWTColor());
                figure.drawValue();
                return true;
            }
        };
        setPropertyChangeHandler(ByteMonitorModel.PROP_OFF_COLOR, colorHandler);


        //change orientation of the bit display
        IWidgetPropertyChangeHandler horizontalHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure refreshableFigure) {
                ByteMonitorFigure figure = (ByteMonitorFigure)refreshableFigure;
                figure.setHorizontal((Boolean)newValue);
                ByteMonitorModel model = getWidgetModel();
                if((Boolean) newValue) //from vertical to horizontal
                    model.setLocation(model.getLocation().x - model.getSize().height/2 + model.getSize().width/2,
                        model.getLocation().y + model.getSize().height/2 - model.getSize().width/2);
                else  //from horizontal to vertical
                    model.setLocation(model.getLocation().x + model.getSize().width/2 - model.getSize().height/2,
                        model.getLocation().y - model.getSize().width/2 + model.getSize().height/2);

                model.setSize(model.getSize().height, model.getSize().width);

                figure.drawValue();
                return true;
            }
        };

        setPropertyChangeHandler(ByteMonitorModel.PROP_HORIZONTAL, horizontalHandler);

        //change the display order of the bits
        IWidgetPropertyChangeHandler reverseBitsHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure refreshableFigure) {
                ByteMonitorFigure figure = (ByteMonitorFigure)refreshableFigure;
                figure.setReverseBits((Boolean)newValue);
                figure.drawValue();
                return true;
            }
        };

        setPropertyChangeHandler(ByteMonitorModel.PROP_BIT_REVERSE, reverseBitsHandler);

        //Set the bit to use as a starting point
        IWidgetPropertyChangeHandler startBitHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure refreshableFigure) {
                ByteMonitorFigure figure = (ByteMonitorFigure)refreshableFigure;
                figure.setStartBit((Integer)newValue);
                figure.drawValue();
                return true;
            }
        };

        setPropertyChangeHandler(ByteMonitorModel.PROP_START_BIT, startBitHandler);

        //Set the number of bits to display
        IWidgetPropertyChangeHandler numBitsHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure refreshableFigure) {
                ByteMonitorFigure figure = (ByteMonitorFigure)refreshableFigure;
                figure.setNumBits((Integer)newValue);
                figure.drawValue();
                return true;
            }
        };

        setPropertyChangeHandler(ByteMonitorModel.PROP_NUM_BITS, numBitsHandler);

        //Square LED
        IWidgetPropertyChangeHandler squareLEDHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ByteMonitorFigure bm = (ByteMonitorFigure) refreshableFigure;
                bm.setSquareLED((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ByteMonitorModel.PROP_SQUARE_LED, squareLEDHandler);

        //LED spacing
        IWidgetPropertyChangeHandler ledBorderWidthHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ByteMonitorFigure bm = (ByteMonitorFigure) refreshableFigure;
                bm.setLedBorderWidth((int) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ByteMonitorModel.PROP_LED_BORDER, ledBorderWidthHandler);

        //LED border color
        IWidgetPropertyChangeHandler ledBorderColorHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ByteMonitorFigure bm = (ByteMonitorFigure) refreshableFigure;
                bm.setLedBorderColor(((OPIColor) newValue).getSWTColor());
                return true;
            }
        };
        setPropertyChangeHandler(ByteMonitorModel.PROP_LED_BORDER_COLOR, ledBorderColorHandler);

        //effect 3D
        IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ByteMonitorFigure bmFig = (ByteMonitorFigure) refreshableFigure;
                bmFig.setEffect3D((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(ByteMonitorModel.PROP_EFFECT3D, effect3DHandler);

        // labels
        IWidgetPropertyChangeHandler labelsHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                ByteMonitorFigure bmFig = (ByteMonitorFigure) refreshableFigure;
                ByteMonitorModel model = getWidgetModel();
                bmFig.setLabels(model.getLabels());
                return true;
            }
        };
        setPropertyChangeHandler(ByteMonitorModel.PROP_LABELS, labelsHandler);


        //Set the LED rendering style
        IWidgetPropertyChangeHandler packHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure refreshableFigure) {
                ByteMonitorFigure figure = (ByteMonitorFigure)refreshableFigure;
                figure.setPackedLEDs((Boolean)newValue);
                figure.drawValue();
                return true;
            }
        };

        setPropertyChangeHandler(ByteMonitorModel.PROP_PACK_LEDS, packHandler);
    }

}
