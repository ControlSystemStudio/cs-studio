/*******************************************************************************
* Copyright (c) 2010-2016 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.bool;

import java.util.List;

import org.csstudio.opibuilder.editparts.AlarmSeverityListener;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.FigureTransparencyHelper;
import org.csstudio.opibuilder.widgets.editparts.AbstractBoolEditPart;
import org.csstudio.opibuilder.widgets.symbol.Preferences;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.swt.widgets.symbol.SymbolImageProperties;
import org.csstudio.swt.widgets.symbol.util.IImageListener;
import org.csstudio.swt.widgets.symbol.util.PermutationMatrix;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VType;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Base edit part controller for a Boolean Symbol Image widget based on
 * {@link CommonBoolSymbolModel}.
 *
 * @author SOPRA Group
 *
 */
public abstract class CommonBoolSymbolEditpart extends AbstractBoolEditPart {

    private IPVListener loadItemsFromPVListener;
    private List<String> meta = null;

    private int maxAttempts;

    /**
     * Sets those properties on the figure that are defined in the
     * {@link CommonBoolSymbolFigure} base class. This method is called by
     * {@link #doCreateFigure()}.
     *
     * @param figure
     *            the figure.
     * @param model
     *            the model.
     */
    public void initializeCommonFigureProperties(CommonBoolSymbolFigure figure,
            CommonBoolSymbolModel model) {
        super.initializeCommonFigureProperties(figure, model);
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

        // Resize when new image is loaded
        figure.setImageLoadedListener(new IImageListener() {

            @Override
            public void imageResized(final IFigure figure) {
                CommonBoolSymbolFigure symbolFigure = (CommonBoolSymbolFigure) figure;
                autoSizeWidget(symbolFigure);
            }
        });

        if (model.getPVName() == null || model.getPVName().isEmpty())
            figure.setUseForegroundColor(true);

        figure.setAnimationDisabled(model.isStopAnimation());
        figure.setSymbolImagePath(model, model.getSymbolImagePath());
    }

    @Override
    public void doActivate() {
        super.doActivate();
        registerLoadItemsListener();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ((CommonBoolSymbolFigure) getFigure()).dispose();
        IPV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
        if (pv != null && loadItemsFromPVListener != null) {
            pv.removeListener(loadItemsFromPVListener);
        }
    }

    // -----------------------------------------------------------------
    // PV properties handlers
    // -----------------------------------------------------------------

    private void registerLoadItemsListener() {
        // load items from PV
        if (getExecutionMode() == ExecutionMode.RUN_MODE) {
            IPV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
            if (pv != null) {
                if (loadItemsFromPVListener == null)
                    loadItemsFromPVListener = new IPVListener.Stub() {
                        @Override
                        public void valueChanged(IPV pv) {
                            VType value = pv.getValue();
                            if (value != null && value instanceof VEnum) {
                                List<String> new_meta = ((VEnum) value).getLabels();
                                if (meta == null || !meta.equals(new_meta)) {
                                    meta = new_meta;
                                    ((CommonBoolSymbolFigure) getFigure())
                                            .updateImagesPathFromMeta(meta);
                                }
                            }
                        }
                    };
                pv.addListener(loadItemsFromPVListener);
            }
        }
    }

    // -----------------------------------------------------------------
    // Image properties handlers
    // -----------------------------------------------------------------

    /**
     * Registers symbol image property change handlers for the properties
     * defined in {@link MonitorBoolSymbolModel}.
     */
    public void registerSymbolImagePropertyHandlers() {
        // symbol image filename property
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                IPath newImagePath = (IPath) newValue;
                imageFigure.setSymbolImagePath(getWidgetModel(), newImagePath);
                // autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_SYMBOL_IMAGE_FILE, handler);

        // PV Name ForeColor color rule
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                if (newValue == null || ((String) newValue).isEmpty())
                    ((CommonBoolSymbolFigure) figure).setUseForegroundColor(true);
                else ((CommonBoolSymbolFigure) figure).setUseForegroundColor(false);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, handler);

        // ForeColor Alarm Sensitive
        getPVWidgetEditpartDelegate().addAlarmSeverityListener(new AlarmSeverityListener() {
            @Override
            public boolean severityChanged(AlarmSeverity severity,
                    IFigure refreshableFigure) {
                CommonBoolSymbolFigure figure = (CommonBoolSymbolFigure) refreshableFigure;
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
     * Registers image size property change handlers for the properties defined
     * in {@link MonitorBoolSymbolModel}.
     */
    public void registerImageSizePropertyHandlers() {
        // image auto-size property
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                imageFigure.setAutoSize((Boolean) newValue);
                CommonBoolSymbolModel model = getWidgetModel();
                Dimension d = imageFigure.getAutoSizedDimension();
                if ((Boolean) newValue && !model.getStretch() && d != null) {
                    model.setSize(d.width, d.height);
                }
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_AUTOSIZE, handler);

        // changes to the stop animation property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                imageFigure.setAnimationDisabled((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_NO_ANIMATION, handler);

        // changes to the align to nearest second property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                imageFigure.setAlignedToNearestSecond((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_ALIGN_TO_NEAREST_SECOND, handler);

        // image size (height/width) property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                imageFigure.resizeImage();
                autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_HEIGHT, handler);
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_WIDTH, handler);
    }

    /**
     * Registers image border property change handlers for the properties
     * defined in {@link MonitorBoolSymbolModel}.
     */
    public void registerImageBorderPropertyHandlers() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                imageFigure.resizeImage();
                autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_BORDER_WIDTH, handler);
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_BORDER_STYLE, handler);
    }

    /**
     * Registers image stretch property change handlers for the properties
     * defined in {@link MonitorBoolSymbolModel}.
     */
    public void registerImageStretchPropertyHandlers() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                imageFigure.setStretch((Boolean) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_STRETCH, handler);
    }

    /**
     * Registers image rotation property change handlers for the properties
     * defined in {@link MonitorBoolSymbolModel}.
     */
    public void registerImageRotationPropertyHandlers() {
        // degree rotation property
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                int newDegree = (Integer) newValue;
                int oldDegree = (Integer) oldValue;

                PermutationMatrix oldMatrix = new PermutationMatrix(
                        (double[][]) getPropertyValue(CommonBoolSymbolModel.PERMUTATION_MATRIX));
                PermutationMatrix newMatrix = PermutationMatrix.generateRotationMatrix(newDegree - oldDegree);
                PermutationMatrix result = newMatrix.multiply(oldMatrix);
                setPropertyValue(CommonBoolSymbolModel.PERMUTATION_MATRIX, result.getMatrix());

                setPropertyValue(CommonBoolSymbolModel.PROP_DEGREE, newDegree);
                imageFigure.setPermutationMatrix(result);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_DEGREE, handler);

        // flip horizontal rotation property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                PermutationMatrix newMatrix = PermutationMatrix.generateFlipHMatrix();
                PermutationMatrix oldMatrix = imageFigure.getPermutationMatrix();
                PermutationMatrix result = newMatrix.multiply(oldMatrix);

                setPropertyValue(CommonBoolSymbolModel.PERMUTATION_MATRIX, result.getMatrix());
                setPropertyValue(CommonBoolSymbolModel.PROP_FLIP_HORIZONTAL, newValue);
                imageFigure.setPermutationMatrix(result);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_FLIP_HORIZONTAL, handler);

        // flip vertical rotation property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                PermutationMatrix newMatrix = PermutationMatrix.generateFlipVMatrix();
                PermutationMatrix oldMatrix = imageFigure.getPermutationMatrix();
                PermutationMatrix result = newMatrix.multiply(oldMatrix);

                setPropertyValue(CommonBoolSymbolModel.PERMUTATION_MATRIX, result.getMatrix());
                setPropertyValue(CommonBoolSymbolModel.PROP_FLIP_VERTICAL, newValue);
                imageFigure.setPermutationMatrix(result);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_FLIP_VERTICAL, handler);
    }

    /**
     * Registers image crop property change handlers for the properties defined
     * in {@link MonitorBoolSymbolModel}.
     */
    public void registerImageCropPropertyHandlers() {
        // top crop property
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                if (newValue == null) {
                    return false;
                }
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                imageFigure.setTopCrop((Integer) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_TOPCROP, handler);

        // bottom crop property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                imageFigure.setBottomCrop((Integer) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_BOTTOMCROP, handler);

        // left crop property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                imageFigure.setLeftCrop((Integer) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_LEFTCROP, handler);

        // right crop property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                CommonBoolSymbolFigure imageFigure = (CommonBoolSymbolFigure) figure;
                imageFigure.setRightCrop((Integer) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(CommonBoolSymbolModel.PROP_RIGHTCROP, handler);
    }

    @Override
    public void registerPropertyChangeHandlers() {
        registerCommonPropertyChangeHandlers();
        registerSymbolImagePropertyHandlers();
        registerImageSizePropertyHandlers();
        registerImageStretchPropertyHandlers();
        registerImageRotationPropertyHandlers();
        registerImageBorderPropertyHandlers();
        registerImageCropPropertyHandlers();

        FigureTransparencyHelper.addHandler(this, figure);
    }

    /**
     * Get the control widget model.
     *
     * @return the control widget model.
     */
    @Override
    public CommonBoolSymbolModel getWidgetModel() {
        return (CommonBoolSymbolModel) getModel();
    }

    public void autoSizeWidget(final CommonBoolSymbolFigure imageFigure) {
        maxAttempts = 10;
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (maxAttempts-- > 0 && imageFigure.isLoadingImage()) {
                    Display.getDefault().timerExec(100, this);
                    return;
                }
                CommonBoolSymbolModel model = getWidgetModel();
                Dimension d = imageFigure.getAutoSizedDimension();
                if (model.isAutoSize() && !model.getStretch() && d != null) {
                    model.setSize(d.width, d.height);
                }
            }
        };
        Display.getDefault().timerExec(100, task);
    }

}
