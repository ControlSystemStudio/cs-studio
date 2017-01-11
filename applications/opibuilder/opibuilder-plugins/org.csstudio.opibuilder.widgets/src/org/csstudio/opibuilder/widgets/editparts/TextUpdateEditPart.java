/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.widgets.figures.NativeTextFigure;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.opibuilder.widgets.model.TextUpdateModel;
import org.csstudio.simplepv.FormatEnum;
import org.csstudio.simplepv.VTypeHelper;
import org.csstudio.swt.widgets.figures.ITextFigure;
import org.csstudio.swt.widgets.figures.TextFigure;
import org.csstudio.swt.widgets.figures.TextFigure.H_ALIGN;
import org.csstudio.swt.widgets.figures.TextFigure.V_ALIGN;
import org.csstudio.swt.widgets.figures.WrappableTextFigure;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

/**The editor for text indicator widget.
 * @author Xihui Chen
 *
 */
public class TextUpdateEditPart extends AbstractPVWidgetEditPart {

    public static final String HEX_PREFIX = "0x"; //$NON-NLS-1$

    private TextUpdateModel widgetModel;
    private FormatEnum format;
    private boolean isAutoSize;
    private boolean isPrecisionFromDB;
    private boolean isShowUnits;
    private int precision;


    @Override
    protected IFigure doCreateFigure() {

        initFields();
        //use native text figure to have better performance on RAP.
        if(OPIBuilderPlugin.isRAP() && !widgetModel.isTransparent()){
            int style = SWT.READ_ONLY;
            switch (widgetModel.getHorizontalAlignment()) {
            case CENTER:
                style |= SWT.CENTER;
                break;
            case LEFT:
                style |= SWT.LEFT;
                break;
            case RIGHT:
                style |= SWT.RIGHT;
            }
            if(widgetModel.isWrapWords())
                style |=SWT.WRAP;
            return new NativeTextFigure(this, style);
        }else {
            TextFigure labelFigure = createTextFigure();
            initTextFigure(labelFigure);
            return labelFigure;
        }
    }

    protected void initTextFigure(TextFigure labelFigure) {
        labelFigure.setFont(widgetModel.getFont().getSWTFont());
        labelFigure.setOpaque(!widgetModel.isTransparent());
        labelFigure.setHorizontalAlignment(widgetModel.getHorizontalAlignment());
        labelFigure.setVerticalAlignment(widgetModel.getVerticalAlignment());
        labelFigure.setRotate(widgetModel.getRotationAngle());
    }

    /**
     *
     */
    protected void initFields() {
        //Initialize frequently used variables.
        widgetModel = getWidgetModel();
        format = widgetModel.getFormat();
        isAutoSize = widgetModel.isAutoSize();
        isPrecisionFromDB = widgetModel.isPrecisionFromDB();
        isShowUnits = widgetModel.isShowUnits();
        precision = widgetModel.getPrecision();
    }

    protected TextFigure createTextFigure(){
        if(getWidgetModel().isWrapWords())
            return new WrappableTextFigure(getExecutionMode() == ExecutionMode.RUN_MODE);
        return new TextFigure(getExecutionMode() == ExecutionMode.RUN_MODE);
    }

    @Override
    protected void createEditPolicies() {
        super.createEditPolicies();
        if(getExecutionMode() == ExecutionMode.EDIT_MODE)
            installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new TextUpdateDirectEditPolicy());
    }

    @Override
    public void activate() {
        super.activate();
        setFigureText(getWidgetModel().getText());
        if(getWidgetModel().isAutoSize()){
            performAutoSize();
            figure.revalidate();
        }
    }
    /**
     * @param text
     */
    protected void setFigureText(String text) {
        if(getFigure() instanceof NativeTextFigure)
            ((NativeTextFigure)getFigure()).getSWTWidget().setText(text);
        else
            ((TextFigure) getFigure()).setText(text);
    }
    @Override
    protected void registerPropertyChangeHandlers() {

        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    final IFigure figure) {
                setFigureText((String)newValue);

                if(isAutoSize){
                    Display.getCurrent().timerExec(10, new Runnable() {
                        @Override
                        public void run() {
                                performAutoSize();
                        }
                    });
                }
                return true;
            }
        };
        setPropertyChangeHandler(TextUpdateModel.PROP_TEXT, handler);



        IWidgetPropertyChangeHandler fontHandler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                figure.setFont(CustomMediaFactory.getInstance().getFont(
                        ((OPIFont)newValue).getFontData()));
                return true;
            }
        };
        setPropertyChangeHandler(LabelModel.PROP_FONT, fontHandler);



        handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    final IFigure figure) {
                Display.getCurrent().timerExec(10, new Runnable() {
                    @Override
                    public void run() {
                        if(getWidgetModel().isAutoSize()){
                            performAutoSize();
                            figure.revalidate();
                        }
                    }
                });

                return true;
            }
        };
        setPropertyChangeHandler(LabelModel.PROP_FONT, handler);
        setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE, handler);
        setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH, handler);

        handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                figure.setOpaque(!(Boolean)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(LabelModel.PROP_TRANSPARENT, handler);

        handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                isAutoSize = (Boolean)newValue;
                if((Boolean)newValue){
                    performAutoSize();
                    figure.revalidate();
                }
                return true;
            }
        };
        setPropertyChangeHandler(LabelModel.PROP_AUTOSIZE, handler);

        handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                if(figure instanceof TextFigure)
                    ((TextFigure)figure).setHorizontalAlignment(
                            H_ALIGN.values()[(Integer)newValue]);
                return true;
            }
        };
        setPropertyChangeHandler(LabelModel.PROP_ALIGN_H, handler);

        handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                if(figure instanceof TextFigure)
                    ((TextFigure)figure).setVerticalAlignment(V_ALIGN.values()[(Integer)newValue]);
                return true;
            }
        };
        setPropertyChangeHandler(LabelModel.PROP_ALIGN_V, handler);

        handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    final IFigure figure) {
                if(newValue == null)
                    return false;
                formatValue(newValue, AbstractPVWidgetModel.PROP_PVVALUE);
                return false;
            }
        };
        setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, handler);

        handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    final IFigure figure) {
                format = FormatEnum.values()[(Integer)newValue];
                formatValue(newValue, TextUpdateModel.PROP_FORMAT_TYPE);
                return true;
            }
        };
        setPropertyChangeHandler(TextUpdateModel.PROP_FORMAT_TYPE, handler);

        handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    final IFigure figure) {
                precision = (Integer)newValue;
                formatValue(newValue, TextUpdateModel.PROP_PRECISION);
                return true;
            }
        };
        setPropertyChangeHandler(TextUpdateModel.PROP_PRECISION, handler);

        handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    final IFigure figure) {
                isPrecisionFromDB = (Boolean)newValue;
                formatValue(newValue, TextUpdateModel.PROP_PRECISION_FROM_DB);
                return true;
            }
        };
        setPropertyChangeHandler(TextUpdateModel.PROP_PRECISION_FROM_DB, handler);

        handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    final IFigure figure) {
                isShowUnits = (Boolean)newValue;
                formatValue(newValue, TextUpdateModel.PROP_SHOW_UNITS);
                return true;
            }
        };
        setPropertyChangeHandler(TextUpdateModel.PROP_SHOW_UNITS, handler);

        handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    final IFigure figure) {
                if(figure instanceof TextFigure)
                    ((TextFigure)figure).setRotate((Double)newValue);
                return true;
            }
        };
        setPropertyChangeHandler(TextUpdateModel.PROP_ROTATION, handler);

        handler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                AbstractWidgetModel model = getWidgetModel();
                AbstractContainerModel parent = model.getParent();
                parent.removeChild(model);
                parent.addChild(model);
                parent.selectWidget(model, true);
                return false;
            }
        };
        setPropertyChangeHandler(TextUpdateModel.PROP_WRAP_WORDS, handler);
    }

    @Override
    public TextUpdateModel getWidgetModel() {
        return (TextUpdateModel)getModel();
    }

    protected void performDirectEdit(){
        new TextEditManager(this, new LabelCellEditorLocator((TextFigure)getFigure())).show();
    }

    @Override
    public void performRequest(Request request){
        if (getExecutionMode() == ExecutionMode.EDIT_MODE &&(
                request.getType() == RequestConstants.REQ_DIRECT_EDIT ||
                request.getType() == RequestConstants.REQ_OPEN))
            performDirectEdit();
    }


    /**
     * @param figure
     */
    protected void performAutoSize() {
        if(figure instanceof TextFigure)
            getWidgetModel().setSize(((TextFigure)getFigure()).getAutoSizeDimension());
        else if(figure instanceof NativeTextFigure)
            getWidgetModel().setSize(((NativeTextFigure)getFigure()).getAutoSizeDimension());
    }

    /**
     * @param newValue
     * @return
     */
    protected String formatValue(Object newValue, String propId) {


        if(getExecutionMode() != ExecutionMode.RUN_MODE)
            return null;
        VType value = null;


        int tempPrecision = precision;
        if(isPrecisionFromDB)
            tempPrecision = -1;

        if(propId.equals(AbstractPVWidgetModel.PROP_PVVALUE))
            value = (VType)newValue;
        else
            value = getPVValue(AbstractPVWidgetModel.PROP_PVNAME);

        String text = VTypeHelper.formatValue(
                    format, value, tempPrecision);

        if(isShowUnits && VTypeHelper.getDisplayInfo(value) != null){
            String units =VTypeHelper.getDisplayInfo(value).getUnits();
            if(units != null && units.trim().length()>0)
                text = text + " " + units; //$NON-NLS-1$
        }

        //synchronize the property value without fire listeners.
        widgetModel.getProperty(
                TextUpdateModel.PROP_TEXT).setPropertyValue(text, false);
        setFigureText(text);

        if(isAutoSize)
            performAutoSize();

        return text;
    }




    @Override
    public String getValue() {
        if(getFigure() instanceof NativeTextFigure)
            return ((NativeTextFigure)getFigure()).getText();
        return ((TextFigure)getFigure()).getText();
    }

    @Override
    public void setValue(Object value) {
        String text;
        if(value instanceof Number)
            text = formatValue(ValueFactory.newVDouble(((Number)value).doubleValue()),
                    AbstractPVWidgetModel.PROP_PVVALUE);
        else
            text = value.toString();
        setFigureText(text);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class key) {
        if(key == ITextFigure.class)
            return getFigure();

        return super.getAdapter(key);
    }

}
