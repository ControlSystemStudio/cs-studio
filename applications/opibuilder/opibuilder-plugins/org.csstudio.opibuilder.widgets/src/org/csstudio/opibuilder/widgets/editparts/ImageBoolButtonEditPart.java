/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;


import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgets.FigureTransparencyHelper;
import org.csstudio.opibuilder.widgets.figures.ImageBoolButtonFigure;
import org.csstudio.opibuilder.widgets.model.ImageBoolButtonModel;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
import org.csstudio.swt.widgets.symbol.SymbolImageProperties;
import org.csstudio.swt.widgets.symbol.util.IImageListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * EditPart controller for the image widget.
 *
 * @author Xihui Chen
 *
 */
public final class ImageBoolButtonEditPart extends AbstractBoolControlEditPart {


    private int maxAttempts;

    /**
     * Returns the casted model. This is just for convenience.
     *
     * @return the casted {@link ImageModel}
     */
    @Override
    public ImageBoolButtonModel getWidgetModel() {
        return (ImageBoolButtonModel) getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        ImageBoolButtonModel model = getWidgetModel();
        // create AND initialize the view properly
        final ImageBoolButtonFigure figure = new ImageBoolButtonFigure();
        initializeCommonFigureProperties(figure, model);

        SymbolImageProperties sip = new SymbolImageProperties();
        sip.setStretch(model.isStretch());
        sip.setAutoSize(model.isAutoSize());
        sip.setAnimationDisabled(model.isStopAnimation());
        sip.setAlignedToNearestSecond(model.isAlignedToNearestSecond());
        sip.setBackgroundColor(new Color(Display.getDefault(), model.getBackgroundColor()));
        figure.setSymbolProperties(sip, model);
        figure.setImageLoadedListener(new IImageListener() {

            @Override
            public void imageResized(IFigure figure) {
                ImageBoolButtonFigure symbolFigure = (ImageBoolButtonFigure) figure;
                autoSizeWidget(symbolFigure);
            }
        });
        figure.addManualValueChangeListener(new IManualValueChangeListener() {

            @Override
            public void manualValueChanged(double newValue) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE)
                    autoSizeWidget(figure);
            }
        });
        figure.setOnImagePath(model.getOnImagePath());
        figure.setOffImagePath(model.getOffImagePath());
        return figure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        registerCommonPropertyChangeHandlers();

        //Save CPU usage
        //removeAllPropertyChangeHandlers(AbstractPVWidgetModel.PROP_PVVALUE);
        // value
//        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
//            public boolean handleChange(final Object oldValue,
//                    final Object newValue,
//                    final IFigure refreshableFigure) {
//                if(newValue == null)
//                    return false;
//                ImageBoolButtonFigure figure = (ImageBoolButtonFigure) refreshableFigure;
//                //figure.setValue(ValueUtil.getDouble((IValue)newValue));
//                autoSizeWidget(figure);
//                return true;
//            }
//        };
//        setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, handler);

        // changes to the on image property
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
                IPath absolutePath = (IPath)newValue;
                if(!absolutePath.isAbsolute())
                    absolutePath = ResourceUtil.buildAbsolutePath(
                            getWidgetModel(), absolutePath);
                imageFigure.setOnImagePath(absolutePath);
                autoSizeWidget(imageFigure);
                return true;
            }


        };
        setPropertyChangeHandler(ImageBoolButtonModel.PROP_ON_IMAGE, handle);

        // changes to the off image property
        handle = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
                IPath absolutePath = (IPath)newValue;
                if(!absolutePath.isAbsolute())
                    absolutePath = ResourceUtil.buildAbsolutePath(
                            getWidgetModel(), absolutePath);
                imageFigure.setOffImagePath(absolutePath);
                autoSizeWidget(imageFigure);
                return true;
            }


        };
        setPropertyChangeHandler(ImageBoolButtonModel.PROP_OFF_IMAGE, handle);

        // changes to the stretch property
        handle = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
                imageFigure.setStretch((Boolean)newValue);
                autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(ImageBoolButtonModel.PROP_STRETCH, handle);
        
        FigureTransparencyHelper.addHandler(this, figure);


        // changes to the autosize property
        handle = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
                autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(ImageBoolButtonModel.PROP_AUTOSIZE, handle);

        // changes to the stop animation property
        handle = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
                imageFigure.setAnimationDisabled((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(ImageBoolButtonModel.PROP_NO_ANIMATION, handle);

        // changes to the align to nearest second property
        handle = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
                imageFigure.setAlignedToNearestSecond((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(ImageBoolButtonModel.PROP_ALIGN_TO_NEAREST_SECOND, handle);

        // changes to the border width property
        handle = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
                autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH, handle);
        setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE, handle);

        //size change handlers - so we can stretch accordingly
        handle = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                ImageBoolButtonFigure imageFigure = (ImageBoolButtonFigure) figure;
                autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractWidgetModel.PROP_HEIGHT, handle);
        setPropertyChangeHandler(AbstractWidgetModel.PROP_WIDTH, handle);


    }



    @Override
    public void deactivate() {
        super.deactivate();
        ((ImageBoolButtonFigure) getFigure()).dispose();
    }

    private void autoSizeWidget(final ImageBoolButtonFigure imageFigure) {
        if(!getWidgetModel().isAutoSize())
            return;
        maxAttempts = 10;
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if(maxAttempts-- > 0 && imageFigure.isLoadingImage()){
                    Display.getDefault().timerExec(100, this);
                    return;
                }
                ImageBoolButtonModel model = getWidgetModel();
                Dimension d = imageFigure.getAutoSizedDimension();
                if(model.isAutoSize() && !model.isStretch() && d != null)
                    model.setSize(d.width, d.height);

            }
        };
        Display.getDefault().timerExec(100, task);
    }

}
