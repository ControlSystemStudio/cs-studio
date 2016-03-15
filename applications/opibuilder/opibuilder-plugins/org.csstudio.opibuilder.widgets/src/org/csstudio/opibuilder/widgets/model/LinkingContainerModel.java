/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import java.util.logging.Level;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.model.AbstractLinkingContainerModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.widgets.Activator;
import org.csstudio.opibuilder.widgets.editparts.LinkingContainerEditpart;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalViewer;
import org.osgi.framework.Version;

/**The model for linking container widget.
 * @author Xihui Chen
 *
 */
public class LinkingContainerModel extends AbstractLinkingContainerModel {

    /**
     * The ID of this widget model.
     */
    public static final String ID = "org.csstudio.opibuilder.widgets.linkingContainer"; //$NON-NLS-1$

    /**
     * Versions before this didn't have an updated resize behaviour.
     */
    public static final Version VERSION_CHANGE_OF_RESIZE_BEHAVIOUR = new Version(4, 0, 103);

    /**
     * How should the container behave when the OPI it is wrapping has content of a different size to the widget.
     */
    public enum ResizeBehaviour {
        SIZE_OPI_TO_CONTAINER,
        SIZE_CONTAINER_TO_OPI,
        CROP_OPI,
        SCROLL_OPI;

        public final static String[] stringValues = {
                "Size *.opi to fit the container",
                "Size the container to fit the linked *.opi",
                "Don't resize anything, crop if *.opi too large for container",
                "Don't resize anything, add scrollbars if *.opi too large for container",
                };
    }

    /**
     * The ID of the resource property.
     */
    public static final String PROP_OPI_FILE = "opi_file"; //$NON-NLS-1$

    /**
     * The ID of the auto zoom property.
     */
    @Deprecated
    public static final String PROP_ZOOMTOFITALL = "zoom_to_fit"; //$NON-NLS-1$

    /**
     *  The ID of the auto scale property.
     */
    @Deprecated
    public static final String PROP_AUTO_SIZE = "auto_size"; //$NON-NLS-1$

    /**
     * How the widget should behave when the contents is not the same size as the widget.
     */
    public static final String PROP_RESIZE_BEHAVIOUR = "resize_behaviour"; //$NON-NLS-1$

    /**
     * The default value of the height property.
     */
    private static final int DEFAULT_HEIGHT = 200;

    /**
     * The default value of the width property.
     */
    private static final int DEFAULT_WIDTH = 200;

    /**
     * The geographical size of the children.
     */
    private Dimension childrenGeoSize = null;

    public LinkingContainerModel() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setBorderStyle(BorderStyle.LOWERED);
    }

    @Override
    protected void configureProperties() {
        addProperty(new BooleanProperty(PROP_ZOOMTOFITALL, "Zoom to Fit", WidgetPropertyCategory.Display, true));
        setPropertyVisibleAndSavable(PROP_ZOOMTOFITALL, false, false);

        addProperty(new BooleanProperty(PROP_AUTO_SIZE, "Auto Size", WidgetPropertyCategory.Display, true));
        setPropertyVisibleAndSavable(PROP_AUTO_SIZE, false, false);

        addProperty(new ComboProperty(PROP_RESIZE_BEHAVIOUR, "Resize Behaviour",
                WidgetPropertyCategory.Display, ResizeBehaviour.stringValues, ResizeBehaviour.SIZE_OPI_TO_CONTAINER.ordinal()));
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
        // Catch and convert deprecated properties being set from scripts here
        if (PROP_ZOOMTOFITALL.equals(id)) {
            Activator.getLogger().log(Level.CONFIG,
                    "Using deprecated parameter to setPropertyValue: \"" + PROP_ZOOMTOFITALL + "\"");
            if((Boolean)value) {
                super.setPropertyValue(PROP_RESIZE_BEHAVIOUR, 0);
            }
        }
        else if (PROP_AUTO_SIZE.equals(id)) {
            Activator.getLogger().log(Level.CONFIG,
                    "Using deprecated parameter to setPropertyValue: \"" + PROP_AUTO_SIZE + "\"");
            if((Boolean)value) {
                super.setPropertyValue(PROP_RESIZE_BEHAVIOUR, 1);
            }
        }
        else {
            super.setPropertyValue(id, value);
        }
    }

    @Override
    public String getTypeID() {
        return ID;
    }

    /**
     * Returns the auto zoom state.
     * @return the auto zoom state
     */
    public boolean isAutoFit() {
        return (int)getProperty(PROP_RESIZE_BEHAVIOUR).getPropertyValue() == ResizeBehaviour.SIZE_OPI_TO_CONTAINER.ordinal();
    }

    public boolean isAutoSize() {
        return (int)getProperty(PROP_RESIZE_BEHAVIOUR).getPropertyValue() == ResizeBehaviour.SIZE_CONTAINER_TO_OPI.ordinal();
    }

    public boolean isShowScrollBars() {
        return (int)getProperty(PROP_RESIZE_BEHAVIOUR).getPropertyValue() == ResizeBehaviour.SCROLL_OPI.ordinal();
    }

    @Override
    public boolean isChildrenOperationAllowable() {
        return false;
    }

    @Override
    public void scale(double widthRatio, double heightRatio) {
        super.scale(widthRatio, heightRatio);
        if(!isAutoFit())
            scaleChildren();

    }

    @Override
    public void processVersionDifference(org.osgi.framework.Version boyVersionOnFile) {
        super.processVersionDifference(boyVersionOnFile);
//        if(boyVersionOnFile.compareTo(VERSION_CHANGE_OF_RESIZE_BEHAVIOUR) < 0) {
//            Activator.getLogger().log(Level.CONFIG, "Converting linking container to new style of resizing behaviour.");
//            if((Boolean)getPropertyValue(PROP_AUTO_SIZE)) {
//                setPropertyValue(PROP_RESIZE_BEHAVIOUR, ResizeBehaviour.SIZE_CONTAINER_TO_OPI.ordinal());
//            } else if((Boolean)getPropertyValue(PROP_ZOOMTOFITALL)) {
//                setPropertyValue(PROP_RESIZE_BEHAVIOUR, ResizeBehaviour.SIZE_OPI_TO_CONTAINER.ordinal());
//            } else {
//                setPropertyValue(PROP_RESIZE_BEHAVIOUR, ResizeBehaviour.SCROLL_OPI.ordinal());
//            }
//        }
    };

    /**
     * Scale its children.
     */
    @Override
    public void scaleChildren() {
        if(isAutoFit())
            return;
        //The linking container model doesn't hold its children actually, so it
        // has to ask editpart to get its children.
        GraphicalViewer viewer = getRootDisplayModel().getViewer();
        if(viewer == null)
            return;
        LinkingContainerEditpart editpart =
                (LinkingContainerEditpart) viewer.
                getEditPartRegistry().
                get(this);
        Dimension size = getSize();
        double newWidthRatio = size.width/(double)getOriginSize().width;
        double newHeightRatio = size.height/(double)getOriginSize().height;
        boolean allowScale = true;
        if(getDisplayModel() != null){
            allowScale = getDisplayModel().getDisplayScaleData().isAutoScaleWidgets();
            if(allowScale){
                int minWidth = getDisplayModel().getDisplayScaleData()
                        .getMinimumWidth();

                if (minWidth < 0) {
                    minWidth = getDisplayModel().getWidth();
                }
                int minHeight = getDisplayModel().getDisplayScaleData()
                        .getMinimumHeight();
                if (minHeight < 0) {
                    minHeight = getDisplayModel().getHeight();
                }
                if (getWidth() * newWidthRatio < minWidth)
                    newWidthRatio = minWidth / (double) getOriginSize().width;
                if (getHeight() * newHeightRatio < minHeight)
                    newHeightRatio = minHeight
                            / (double) getOriginSize().height;
            }

        }
        if(allowScale)
            for(Object child : editpart.getChildren())
                ((AbstractBaseEditPart)child).getWidgetModel().scale(newWidthRatio, newHeightRatio);
    }

    @Override
    public Dimension getOriginSize() {
        if(childrenGeoSize == null)
            return super.getOriginSize();
        else
            return childrenGeoSize;
    }

    public void setChildrenGeoSize(Dimension childrenGeoSize) {
        this.childrenGeoSize = childrenGeoSize;
    }
}
