/*******************************************************************************
* Copyright (c) 2010-2016 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.multistate;

import java.util.List;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.AlarmSeverityListener;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.FigureTransparencyHelper;
import org.csstudio.opibuilder.widgets.symbol.Preferences;
import org.csstudio.opibuilder.widgets.symbol.util.SymbolLabelPosition;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.simplepv.VTypeHelper;
import org.csstudio.swt.widgets.symbol.SymbolImageProperties;
import org.csstudio.swt.widgets.symbol.util.IImageListener;
import org.csstudio.swt.widgets.symbol.util.PermutationMatrix;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VBoolean;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VType;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Fred Arnaud (Sopra Group)
 */
public abstract class CommonMultiSymbolEditPart extends AbstractPVWidgetEditPart {

    private IPVListener loadItemsFromPVListener;
    private List<String> meta = null;

    private int maxAttempts;

    /**
     * Returns the casted model. This is just for convenience.
     */
    @Override
    public CommonMultiSymbolModel getWidgetModel() {
        return (CommonMultiSymbolModel) getModel();
    }

    protected void initializeCommonFigureProperties(
            CommonMultiSymbolFigure figure) {
        CommonMultiSymbolModel model = getWidgetModel();

        figure.setExecutionMode(getExecutionMode());

        // Image default parameters
        SymbolImageProperties sip = new SymbolImageProperties();
        sip.setTopCrop(model.getTopCrop());
        sip.setBottomCrop(model.getBottomCrop());
        sip.setLeftCrop(model.getLeftCrop());
        sip.setRightCrop(model.getRightCrop());
        sip.setStretch(model.getStretch());
        sip.setAutoSize(model.isAutoSize());
        sip.setMatrix(model.getPermutationMatrix());
        sip.setAlignedToNearestSecond(model.isAlignedToNearestSecond());
        sip.setBackgroundColor(new Color(Display.getDefault(), model.getBackgroundColor()));
        sip.setColorToChange(new Color(Display.getDefault(), Preferences.getColorToChange()));

        figure.setSymbolProperties(sip);
        figure.setOnColor(model.getOnColor());
        figure.setOffColor(model.getOffColor());

        // Label parameters
        figure.setShowSymbolLabel(model.isShowSymbolLabel());
        figure.setSymbolLabelPosition(model.getSymbolLabelPosition());

        // Resize when new image is loaded
        figure.setImageLoadedListener(new IImageListener() {

            @Override
            public void imageResized(IFigure figure) {
                CommonMultiSymbolFigure symbolFigure = (CommonMultiSymbolFigure) figure;
                autoSizeWidget(symbolFigure);
            }
        });

        if (model.getPVName() == null || model.getPVName().isEmpty())
            figure.setUseForegroundColor(true);

        figure.setAnimationDisabled(model.isStopAnimation());
        figure.setSymbolImagePath(model, model.getSymbolImagePath());

        if (!model.isItemsFromPV()
                && !(getExecutionMode() == ExecutionMode.EDIT_MODE)) {
            List<String> items = getWidgetModel().getItems();
            if (items != null)
                figure.setStates(items);
        }
    }

    protected void registerCommonPropertyChangeHandlers() {
        super.registerBasePropertyChangeHandlers();

        // Image properties handlers
        registerSymbolImagePropertyHandlers();
        registerImageColorPropertyHandlers();
        registerImageSizePropertyHandlers();
        registerImageCropPropertyHandlers();
        registerImageStretchPropertyHandlers();
        registerImageRotationPropertyHandlers();
        registerImageBorderPropertyHandlers();

        // Label change handlers
        registerLabelPropertyChangeHandlers();

        // PV properties handlers
        registerCommonPVChangeHandlers();

        FigureTransparencyHelper.addHandler(this, figure);
    }

    @Override
    protected void doActivate() {
        super.doActivate();
        registerLoadItemsListener();
    }

    @Override
    protected void doDeActivate() {
        super.doDeActivate();
        ((CommonMultiSymbolFigure) getFigure()).disposeAll();
        if (getWidgetModel().isItemsFromPV()) {
            IPV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
            if (pv != null && loadItemsFromPVListener != null) {
                pv.removeListener(loadItemsFromPVListener);
            }
        }
    }

    // -----------------------------------------------------------------
    // PV properties handlers
    // -----------------------------------------------------------------

    private void registerLoadItemsListener() {
        // load items from PV
        if (getExecutionMode() == ExecutionMode.RUN_MODE) {
            if (getWidgetModel().isItemsFromPV()) {
                IPV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
                if (pv != null) {
                    if (loadItemsFromPVListener == null)
                        loadItemsFromPVListener = new IPVListener.Stub() {
                            @Override
                            public void valueChanged(IPV pv) {
                                VType value = pv.getValue();
                                if (value != null && value instanceof VEnum) {
                                    List<String> new_meta = ((VEnum)value).getLabels();
                                    if (meta == null || !meta.equals(new_meta)) {
                                        meta = new_meta;
                                        getWidgetModel()
                                                .setPropertyValue(CommonMultiSymbolModel.PROP_ITEMS, meta);
                                    }
                                }
                            }
                        };
                    pv.addListener(loadItemsFromPVListener);
                }
            }
        }
    }

    private void registerCommonPVChangeHandlers() {
        // PV_Name
        IWidgetPropertyChangeHandler pvNameHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                if (newValue == null || ((String) newValue).isEmpty())
                    ((CommonMultiSymbolFigure) figure).setUseForegroundColor(true);
                else ((CommonMultiSymbolFigure) figure).setUseForegroundColor(false);
                registerLoadItemsListener();
                return false;
            }
        };
        setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, pvNameHandler);

        // PV_Value
        IWidgetPropertyChangeHandler pvhandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                if (newValue != null && newValue instanceof VType) {
                    CommonMultiSymbolFigure symbolFigure = (CommonMultiSymbolFigure) refreshableFigure;
                    if (newValue instanceof VNumber) {
                        Double doubleValue = VTypeHelper.getDouble((VType) newValue);
                        symbolFigure.setState(doubleValue);
                    }else if (newValue instanceof VBoolean) {
                        symbolFigure.setState((VBoolean)newValue);
                    }else {
                        String stringValue = VTypeHelper.getString((VType) newValue);
                        symbolFigure.setState(stringValue);
                    }
                    // autoSizeWidget(symbolFigure);
                }
                return false;
            }
        };
        setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, pvhandler);

        // Items
        IWidgetPropertyChangeHandler itemsHandler = new IWidgetPropertyChangeHandler() {
            @Override
            @SuppressWarnings("unchecked")
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                if (newValue != null && newValue instanceof List) {
                    CommonMultiSymbolFigure symbolFigure = (CommonMultiSymbolFigure) refreshableFigure;
                    symbolFigure.setStates(((List<String>) newValue));
                    if (getWidgetModel().isItemsFromPV()) {
                        symbolFigure.setState(VTypeHelper.getString(getPVValue(AbstractPVWidgetModel.PROP_PVNAME)));
                        // autoSizeWidget(symbolFigure);
                    }
                }
                return true;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_ITEMS, itemsHandler);
    }

    @Override
    public String getValue() {
        return ((CommonMultiSymbolFigure) getFigure()).getCurrentState();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof String)
            ((CommonMultiSymbolFigure) getFigure()).setState((String) value);
        else if (value instanceof Number)
            ((CommonMultiSymbolFigure) getFigure()).setState(((Number) value).intValue());
        else super.setValue(value);
    }

    // -----------------------------------------------------------------
    // Label properties handlers
    // -----------------------------------------------------------------

    private void registerLabelPropertyChangeHandlers() {
        // show symbol label
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                CommonMultiSymbolFigure figure = (CommonMultiSymbolFigure) refreshableFigure;
                figure.setShowSymbolLabel((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_SHOW_SYMBOL_LABEL, handler);

        // symbol label position
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                CommonMultiSymbolFigure figure = (CommonMultiSymbolFigure) refreshableFigure;
                figure.setSymbolLabelPosition(SymbolLabelPosition.values()[(Integer) newValue]);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_SYMBOL_LABEL_POS, handler);
    }

    // -----------------------------------------------------------------
    // Image properties handlers
    // -----------------------------------------------------------------

    /**
     * Registers symbol image property change handler
     */
    private void registerSymbolImagePropertyHandlers() {
        // symbol image filename property
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                IPath newImagePath = (IPath) newValue;
                imageFigure.setSymbolImagePath(getWidgetModel(), newImagePath);
                // autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_SYMBOL_IMAGE_FILE, handler);
    }

    private void registerImageColorPropertyHandlers() {
        // on color
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                CommonMultiSymbolFigure figure = (CommonMultiSymbolFigure) refreshableFigure;
                figure.setOnColor(((OPIColor) newValue).getSWTColor());
                return true;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_ON_COLOR, handler);

        // off color
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                CommonMultiSymbolFigure figure = (CommonMultiSymbolFigure) refreshableFigure;
                figure.setOffColor(((OPIColor) newValue).getSWTColor());
                return true;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_OFF_COLOR, handler);

        // ForeColor Alarm Sensitive
        getPVWidgetEditpartDelegate().addAlarmSeverityListener(new AlarmSeverityListener() {
            @Override
            public boolean severityChanged(AlarmSeverity severity,
                    IFigure refreshableFigure) {
                CommonMultiSymbolFigure figure = (CommonMultiSymbolFigure) refreshableFigure;
                if (!getWidgetModel().isForeColorAlarmSensitve()) {
                    figure.setUseForegroundColor(false);
                } else {
                    if (severity.equals(AlarmSeverity.NONE))
                        figure.setUseForegroundColor(false);
                    else figure.setUseForegroundColor(true);
                }
                return true;
            }
        });
    }

    /**
     * Registers image size property change handlers
     */
    private void registerImageSizePropertyHandlers() {
        // image auto-size property
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                imageFigure.setAutoSize((Boolean) newValue);
                CommonMultiSymbolModel model = getWidgetModel();
                Dimension d = imageFigure.getAutoSizedDimension();
                if ((Boolean) newValue && !model.getStretch() && d != null) {
                    model.setSize(d.width, d.height);
                }
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_AUTOSIZE, handler);

        // changes to the stop animation property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                imageFigure.setAnimationDisabled((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_NO_ANIMATION, handler);

        // changes to the align to nearest second property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                imageFigure.setAlignedToNearestSecond((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_ALIGN_TO_NEAREST_SECOND, handler);

        // image size (height/width) property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                imageFigure.resizeImage();
                autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_HEIGHT, handler);
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_WIDTH, handler);
    }

    /**
     * Registers image border property change handlers
     */
    private void registerImageBorderPropertyHandlers() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                imageFigure.resizeImage();
                autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_BORDER_WIDTH, handler);
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_BORDER_STYLE, handler);
    }

    /**
     * Registers image stretch property change handlers
     */
    private void registerImageStretchPropertyHandlers() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                imageFigure.setStretch((Boolean) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_STRETCH, handler);
    }

    /**
     * Registers image rotation property change handlers
     */
    public void registerImageRotationPropertyHandlers() {
        // degree rotation property
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                int newDegree = (Integer) newValue;
                int oldDegree = (Integer) oldValue;

                PermutationMatrix oldMatrix = new PermutationMatrix(
                        (double[][]) getPropertyValue(CommonMultiSymbolModel.PERMUTATION_MATRIX));
                PermutationMatrix newMatrix = PermutationMatrix
                        .generateRotationMatrix(newDegree - oldDegree);
                PermutationMatrix result = newMatrix.multiply(oldMatrix);
                setPropertyValue(CommonMultiSymbolModel.PERMUTATION_MATRIX, result.getMatrix());

                setPropertyValue(CommonMultiSymbolModel.PROP_DEGREE, newDegree);
                imageFigure.setPermutationMatrix(result);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_DEGREE, handler);

        // flip horizontal rotation property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                PermutationMatrix newMatrix = PermutationMatrix.generateFlipHMatrix();
                PermutationMatrix oldMatrix = imageFigure.getPermutationMatrix();
                PermutationMatrix result = newMatrix.multiply(oldMatrix);

                setPropertyValue(CommonMultiSymbolModel.PROP_FLIP_HORIZONTAL, newValue);
                setPropertyValue(CommonMultiSymbolModel.PERMUTATION_MATRIX, result.getMatrix());
                imageFigure.setPermutationMatrix(result);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_FLIP_HORIZONTAL, handler);

        // flip vertical rotation property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                PermutationMatrix newMatrix = PermutationMatrix.generateFlipVMatrix();
                PermutationMatrix oldMatrix = imageFigure.getPermutationMatrix();
                PermutationMatrix result = newMatrix.multiply(oldMatrix);

                setPropertyValue(CommonMultiSymbolModel.PROP_FLIP_VERTICAL, newValue);
                setPropertyValue(CommonMultiSymbolModel.PERMUTATION_MATRIX, result.getMatrix());
                imageFigure.setPermutationMatrix(result);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_FLIP_VERTICAL, handler);
    }

    /**
     * Registers image crop property change handlers
     */
    private void registerImageCropPropertyHandlers() {
        // top crop property
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                if (newValue == null) {
                    return false;
                }
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                imageFigure.setTopCrop((Integer) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_TOPCROP, handler);

        // bottom crop property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                imageFigure.setBottomCrop((Integer) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_BOTTOMCROP, handler);

        // left crop property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                imageFigure.setLeftCrop((Integer) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_LEFTCROP, handler);

        // right crop property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonMultiSymbolFigure imageFigure = (CommonMultiSymbolFigure) figure;
                imageFigure.setRightCrop((Integer) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonMultiSymbolModel.PROP_RIGHTCROP, handler);
    }

    public void autoSizeWidget(final CommonMultiSymbolFigure imageFigure) {
        maxAttempts = 10;
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (maxAttempts-- > 0 && imageFigure.isLoadingImage()) {
                    Display.getDefault().timerExec(100, this);
                    return;
                }
                CommonMultiSymbolModel model = (CommonMultiSymbolModel) getModel();
                Dimension d = imageFigure.getAutoSizedDimension();
                if (model.isAutoSize() && !model.getStretch() && d != null) {
                    model.setSize(d.width, d.height);
                }
            }
        };
        Display.getDefault().timerExec(100, task);
    }

}
