/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.IPVWidgetModel;
import org.csstudio.opibuilder.model.PVWidgetModelDelegate;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.eclipse.swt.graphics.RGB;
import org.osgi.framework.Version;

/**The model for array widget.
 * @author Xihui Chen
 *
 */
public class ArrayModel extends AbstractContainerModel implements IPVWidgetModel{

    public enum ArrayDataType {
        DOUBLE_ARRAY("double[]"), //$NON-NLS-1$
        STRING_ARRAY("String[]"),        //$NON-NLS-1$
        INT_ARRAY("int[]"),        //$NON-NLS-1$
        BYTE_ARRAY("byte[]"),//$NON-NLS-1$
        LONG_ARRAY("long[]"),//$NON-NLS-1$
        SHORT_ARRAY("short[]"),//$NON-NLS-1$
        FLOAT_ARRAY("float[]"),//$NON-NLS-1$
        OBJECT_ARRAY("Object[]");//$NON-NLS-1$

        private String description;
        private ArrayDataType(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }

        public static String[] stringValues(){
            String[] result = new String[values().length];
            int i =0 ;
            for(ArrayDataType f : values()){
                result[i++] = f.toString();
            }
            return result;
        }
    }

    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.opibuilder.widgets.array"; //$NON-NLS-1$

    /**
     * The key to get unique propId info from other widgets.
     */
    public static final String ARRAY_UNIQUEPROP_ID = "array.uniquePropId.List"; //$NON-NLS-1$

    /**
     * Array Length
     */
    public static final String PROP_ARRAY_LENGTH= "array_length"; //$NON-NLS-1$

    /**
     * If the array widget is layoutted in horizontal.
     */
    public static final String PROP_HORIZONTAL= "horizontal"; //$NON-NLS-1$


    public static final String PROP_SHOW_SPINNER = "show_spinner"; //$NON-NLS-1$

    public static final String PROP_SHOW_SCROLLBAR = "show_scrollbar"; //$NON-NLS-1$

    public static final String PROP_SPINNER_WIDTH = "spinner_width"; //$NON-NLS-1$

    public static final String PROP_VISIBLE_ELEMENTS_COUNT = "vec"; //$NON-NLS-1$

    public static final String PROP_DATA_TYPE = "data_type"; //$NON-NLS-1$

    private PVWidgetModelDelegate delegate;

    public ArrayModel() {
        setSize(150, 122);
        setForegroundColor(new RGB(0, 0, 0));
    }

    public PVWidgetModelDelegate getDelegate(){
        if(delegate == null)
            delegate = new PVWidgetModelDelegate(this);
        return delegate;
    }

    @Override
    public synchronized void addChild(AbstractWidgetModel child,
            boolean changeParent) {
        if(!getChildren().isEmpty())
            return;
        //child should not be scalable because their size are layoutted by the array figure.
        child.setScaleOptions(false, false, false);
        super.addChild(child, changeParent);
        for(int i=1; i<getVisibleElementsCount(); i++){
            try {
                AbstractWidgetModel clone = XMLUtil.XMLElementToWidget(XMLUtil.widgetToXMLElement(child));
                super.addChild(clone, changeParent);
            } catch (Exception e) {
                ErrorHandlerUtil.handleError("Failed to generate copy of the element widget in array widget.", e);
            }
        }
    }

    @Override
    public synchronized void addChild(int index, AbstractWidgetModel child) {
        addChild(child, true);
    }

    @Override
    public synchronized void removeChild(AbstractWidgetModel child) {
        removeAllChildren();
    }

    @Override
    protected void configureBaseProperties() {
        super.configureBaseProperties();
        getDelegate().configureBaseProperties();
    }

    @Override
    protected void configureProperties() {
        addProperty(new IntegerProperty(PROP_ARRAY_LENGTH, "Array Length",
                WidgetPropertyCategory.Behavior, 10, 0, Integer.MAX_VALUE));
        addProperty(new IntegerProperty(PROP_SPINNER_WIDTH, "Spinner Width",
                WidgetPropertyCategory.Display, 40, 0, 1000));
        addProperty(new BooleanProperty(PROP_HORIZONTAL,
                "Horizontal", WidgetPropertyCategory.Display, false));
        addProperty(new BooleanProperty(PROP_SHOW_SPINNER,
                "Show Spinner", WidgetPropertyCategory.Display, true));
        addProperty(new BooleanProperty(PROP_SHOW_SCROLLBAR,
                "Show Scrollbar", WidgetPropertyCategory.Display, true));
        addProperty(new IntegerProperty(PROP_VISIBLE_ELEMENTS_COUNT, "Visible Elements Count",
                WidgetPropertyCategory.Display, 1, 0, 1000));
        addProperty(new ComboProperty(PROP_DATA_TYPE, "Data Type",
                WidgetPropertyCategory.Behavior, ArrayDataType.stringValues(), 0));

        setPropertyVisibleAndSavable(PROP_VISIBLE_ELEMENTS_COUNT, false, true);
        getProperty(PROP_VISIBLE_ELEMENTS_COUNT).addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(getChildren().size() <1)
                    return;
                AbstractWidgetModel child = getChildren().get(0);
                removeAllChildren();
                addChild(child);
            }
        });
    }

    @Override
    public String getTypeID() {
        return ID;
    }

    public int getArrayLength(){
        return (Integer)getPropertyValue(PROP_ARRAY_LENGTH);
    }

    public int getSpinnerWidth(){
        return (Integer)getPropertyValue(PROP_SPINNER_WIDTH);
    }

    public int getVisibleElementsCount(){
        return (Integer)getPropertyValue(PROP_VISIBLE_ELEMENTS_COUNT);
    }

    public boolean isHorizontal() {
        return (Boolean)getPropertyValue(PROP_HORIZONTAL);
    }

    public boolean isShowSpinner() {
        return (Boolean)getPropertyValue(PROP_SHOW_SPINNER);
    }

    public Boolean isShowScrollbar(){
        return (Boolean)getPropertyValue(PROP_SHOW_SCROLLBAR);
    }

    public ArrayDataType getDataType(){
        return ArrayDataType.values()[(Integer)getPropertyValue(PROP_DATA_TYPE)];
    }

    @Override
    public void processVersionDifference(Version boyVersionOnFile) {
        super.processVersionDifference(boyVersionOnFile);
        delegate.processVersionDifference(boyVersionOnFile);
    }



    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.model.AbstractContainerModel#getChildren()
     * Array widget only needs to save the first one child.
     */
    @Override
    public List<AbstractWidgetModel> getChildren() {
        if(super.getChildren().size()>1)
            return super.getChildren().subList(0, 1);
        return super.getChildren();
    }

    public List<AbstractWidgetModel> getAllChildren() {
        return super.getChildren();
    }



    @Override
    public boolean isChildrenOperationAllowable() {
        return false;
    }

    @Override
    public boolean isBorderAlarmSensitve(){
        return getDelegate().isBorderAlarmSensitve();
    }

    @Override
    public boolean isForeColorAlarmSensitve(){
        return getDelegate().isForeColorAlarmSensitve();
    }

    @Override
    public boolean isBackColorAlarmSensitve(){
        return getDelegate().isBackColorAlarmSensitve();
    }

    @Override
    public String getPVName(){
        return getDelegate().getPVName();
    }

    @Override
    public boolean isAlarmPulsing(){
        return getDelegate().isAlarmPulsing();
    }


    public void setArrayLength(int length){
        getProperty(PROP_ARRAY_LENGTH).setPropertyValue(length);
    }

    public void setDataType(ArrayDataType type){
        getProperty(PROP_DATA_TYPE).setPropertyValue(type.ordinal());
    }

    @Override
    public void scaleChildren() {
//        if(!getChildren().isEmpty() && getChildren().get(0) instanceof AbstractContainerModel)
//            for(AbstractWidgetModel child : getAllChildren()){
//                ((AbstractContainerModel) child).scaleChildren();
//            }
    }

}
