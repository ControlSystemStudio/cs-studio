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
import org.csstudio.opibuilder.widgets.editparts.AbstractBoolControlEditPart;
import org.csstudio.opibuilder.widgets.symbol.Preferences;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
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
 * Edit part Controller for a Control Boolean Symbol Image widget based on
 * {@link ControlBoolSymbolModel}.
 *
 * @author SOPRA Group
 *
 */
public class ControlBoolSymbolEditpart extends AbstractBoolControlEditPart {

    private IPVListener loadItemsFromPVListener;
    private List<String> meta = null;

    private int maxAttempts;

    @Override
    protected IFigure doCreateFigure() {
        ControlBoolSymbolFigure figure = new ControlBoolSymbolFigure();
        initializeCommonFigureProperties(figure, getWidgetModel());
        return figure;
    }

    /**
     * Sets those properties on the figure that are defined in the
     * {@link ControlBoolSymbolFigure} implementation class. This method is
     * called by {@link #doCreateFigure()}.
     *
     * @param figure
     *            the figure.
     * @param model
     *            the model.
     */
    public void initializeCommonFigureProperties(
            final ControlBoolSymbolFigure figure, ControlBoolSymbolModel model) {
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
                ControlBoolSymbolFigure symbolFigure = (ControlBoolSymbolFigure) figure;
                autoSizeWidget(symbolFigure);
            }
        });

        if (model.getPVName() == null || model.getPVName().isEmpty())
            figure.setUseForegroundColor(true);

        figure.setAnimationDisabled(model.isStopAnimation());
        figure.setSymbolImagePath(model, model.getSymbolImagePath());

        figure.addManualValueChangeListener(new IManualValueChangeListener() {
            @Override
            public void manualValueChanged(double newValue) {
                if (getExecutionMode() == ExecutionMode.RUN_MODE) {
                    // autoSizeWidget(figure);
                }
            }
        });
    }

    @Override
    public void doActivate() {
        super.doActivate();
        registerLoadItemsListener();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ((ControlBoolSymbolFigure) getFigure()).dispose();
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
                                    ((ControlBoolSymbolFigure) getFigure())
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
     * defined in {@link ControlBoolSymbolModel}.
     */
    public void registerSymbolImagePropertyHandlers() {
        // symbol image filename property
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                IPath newImagePath = (IPath) newValue;
                imageFigure.setSymbolImagePath(getWidgetModel(), newImagePath);
                // autoSizeWidget(imageFigure);
                return true;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_SYMBOL_IMAGE_FILE, handler);

        // PV Name ForeColor color rule
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                if (newValue == null || ((String) newValue).isEmpty())
                    ((ControlBoolSymbolFigure) figure).setUseForegroundColor(true);
                else ((ControlBoolSymbolFigure) figure).setUseForegroundColor(false);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, handler);

        // ForeColor Alarm Sensitive
        getPVWidgetEditpartDelegate().addAlarmSeverityListener(new AlarmSeverityListener() {
            @Override
            public boolean severityChanged(AlarmSeverity severity,
                    IFigure refreshableFigure) {
                ControlBoolSymbolFigure figure = (ControlBoolSymbolFigure) refreshableFigure;
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
     * in {@link ControlBoolSymbolModel}.
     */
    public void registerImageSizePropertyHandlers() {
        // image auto-size property
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                imageFigure.setAutoSize((Boolean) newValue);
                ControlBoolSymbolModel model = (ControlBoolSymbolModel) getModel();
                Dimension d = imageFigure.getAutoSizedDimension();
                if ((Boolean) newValue && !model.getStretch() && d != null) {
                    model.setSize(d.width, d.height);
                }
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_AUTOSIZE, handler);

        // changes to the stop animation property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                imageFigure.setAnimationDisabled((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_NO_ANIMATION, handler);

        // changes to the align to nearest second property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                imageFigure.setAlignedToNearestSecond((Boolean) newValue);
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_ALIGN_TO_NEAREST_SECOND, handler);

        // image size (height/width) property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                imageFigure.resizeImage();
                autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_HEIGHT, handler);
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_WIDTH, handler);
    }

    /**
     * Registers image border property change handlers for the properties
     * defined in {@link ControlBoolSymbolModel}.
     */
    public void registerImageBorderPropertyHandlers() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                imageFigure.resizeImage();
                autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_BORDER_WIDTH, handler);
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_BORDER_STYLE, handler);
    }

    /**
     * Registers image stretch property change handlers for the properties
     * defined in {@link ControlBoolSymbolModel}.
     */
    public void registerImageStretchPropertyHandlers() {
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                imageFigure.setStretch((Boolean) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_STRETCH, handler);
    }

    /**
     * Registers image rotation property change handlers for the properties
     * defined in {@link ControlBoolSymbolModel}.
     */
    public void registerImageRotationPropertyHandlers() {
        // degree rotation property
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                if (oldValue == null || newValue == null)
                    return false;
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                int newDegree = (Integer) newValue;
                int oldDegree = (Integer) oldValue;

                PermutationMatrix oldMatrix = new PermutationMatrix(
                        (double[][]) getPropertyValue(ControlBoolSymbolModel.PERMUTATION_MATRIX));
                PermutationMatrix newMatrix = PermutationMatrix
                        .generateRotationMatrix(newDegree - oldDegree);
                PermutationMatrix result = newMatrix.multiply(oldMatrix);
                setPropertyValue(ControlBoolSymbolModel.PERMUTATION_MATRIX, result.getMatrix());

                setPropertyValue(ControlBoolSymbolModel.PROP_DEGREE, newDegree);
                imageFigure.setPermutationMatrix(result);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_DEGREE, handler);

        // flip horizontal rotation property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                if (oldValue == null || newValue == null)
                    return false;
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                PermutationMatrix newMatrix = PermutationMatrix.generateFlipHMatrix();
                PermutationMatrix oldMatrix = imageFigure.getPermutationMatrix();
                PermutationMatrix result = newMatrix.multiply(oldMatrix);

                setPropertyValue(ControlBoolSymbolModel.PROP_FLIP_HORIZONTAL, newValue);
                setPropertyValue(ControlBoolSymbolModel.PERMUTATION_MATRIX, result.getMatrix());
                imageFigure.setPermutationMatrix(result);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_FLIP_HORIZONTAL, handler);

        // flip vertical rotation property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                if (oldValue == null || newValue == null)
                    return false;
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                PermutationMatrix newMatrix = PermutationMatrix.generateFlipVMatrix();
                PermutationMatrix oldMatrix = imageFigure.getPermutationMatrix();
                PermutationMatrix result = newMatrix.multiply(oldMatrix);

                setPropertyValue(ControlBoolSymbolModel.PROP_FLIP_VERTICAL, newValue);
                setPropertyValue(ControlBoolSymbolModel.PERMUTATION_MATRIX, result.getMatrix());
                imageFigure.setPermutationMatrix(result);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_FLIP_VERTICAL, handler);
    }

    /**
     * Registers image crop property change handlers for the properties defined
     * in {@link ControlBoolSymbolModel}.
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
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                imageFigure.setTopCrop((Integer) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_TOPCROP, handler);

        // bottom crop property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                imageFigure.setBottomCrop((Integer) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_BOTTOMCROP, handler);

        // left crop property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                imageFigure.setLeftCrop((Integer) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_LEFTCROP, handler);

        // right crop property
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure figure) {
                ControlBoolSymbolFigure imageFigure = (ControlBoolSymbolFigure) figure;
                imageFigure.setRightCrop((Integer) newValue);
                // autoSizeWidget(imageFigure);
                return false;
            }
        };
        setPropertyChangeHandler(ControlBoolSymbolModel.PROP_RIGHTCROP, handler);
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
    public ControlBoolSymbolModel getWidgetModel() {
        return (ControlBoolSymbolModel) super.getWidgetModel();
    }

    public void autoSizeWidget(final ControlBoolSymbolFigure imageFigure) {
        maxAttempts = 10;
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (maxAttempts-- > 0 && imageFigure.isLoadingImage()) {
                    Display.getDefault().timerExec(100, this);
                    return;
                }
                ControlBoolSymbolModel model = (ControlBoolSymbolModel) getModel();
                Dimension d = imageFigure.getAutoSizedDimension();
                if (model.isAutoSize() && !model.getStretch() && d != null) {
                    model.setSize(d.width, d.height);
                }
            }
        };
        Display.getDefault().timerExec(100, task);
    }

}
