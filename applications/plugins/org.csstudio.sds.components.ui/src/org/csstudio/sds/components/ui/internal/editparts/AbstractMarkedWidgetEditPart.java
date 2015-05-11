package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.AbstractMarkedWidgetModel;
import org.csstudio.sds.components.ui.internal.figures.AbstractMarkedWidgetFigure;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;

/**
 * Base editPart controller for a widget based on
 * {@link AbstractMarkedWidgetModel}.
 *
 * @author Xihui Chen
 *
 */
public abstract class AbstractMarkedWidgetEditPart extends
        AbstractScaledWidgetEditPart {

    /**
     * Sets those properties on the figure that are defined in the
     * {@link AbstractMarkedWidgetFigure} base class. This method is provided
     * for the convenience of subclasses, which can call this method in their
     * implementation of
     * {@link org.csstudio.sds.ui.editparts.AbstractBaseEditPart#doCreateFigure()}
     * .
     *
     * @param figure
     *            the figure.
     * @param model
     *            the model.
     */
    protected final void initializeCommonFigureProperties(
            final AbstractMarkedWidgetFigure figure,
            final AbstractMarkedWidgetModel model) {

        super.initializeCommonFigureProperties(figure, model);
        figure.setShowMarkers(model.isShowMarkers());

        figure.setLoloLevel(model.getLoloLevel());
        figure.setLoLevel(model.getLoLevel());
        figure.setHiLevel(model.getHiLevel());
        figure.setHihiLevel(model.getHihiLevel());

        figure.setShowLolo(model.isShowLolo());
        figure.setShowLo(model.isShowLo());
        figure.setShowHi(model.isShowHi());
        figure.setShowHihi(model.isShowHihi());

        figure
                .setLoloColor(getModelColor(AbstractMarkedWidgetModel.PROP_LOLO_COLOR));
        figure
                .setLoColor(getModelColor(AbstractMarkedWidgetModel.PROP_LO_COLOR));
        figure
                .setHiColor(getModelColor(AbstractMarkedWidgetModel.PROP_HI_COLOR));
        figure
                .setHihiColor(getModelColor(AbstractMarkedWidgetModel.PROP_HIHI_COLOR));

    }

    /**
     * Registers property change handlers for the properties defined in
     * {@link org.csstudio.sds.components.model.AbstractScaledWidgetModel}. This
     * method is provided for the convenience of subclasses, which can call this
     * method in their implementation of
     * {@link #registerPropertyChangeHandlers()}.
     */
    @Override
    // FIXME: Sven Wende: 2010-04-19: Vorsicht Spaghetti-Code. Den Aufruf dieser
    // Methode in den Subklassen optimieren. Besser hier bereits
    // registerPropertyChangeHandlers() implementieren.
    protected final void registerCommonPropertyChangeHandlers() {
        super.registerCommonPropertyChangeHandlers();
        setShowMarkersHandler();
        setLoloLevelHandler();
        setLoLevelHandler();
        setHiLevelHandler();
        setHihiLevelHandler();
        setShowLoloHandler();
        setShowLoHandler();
        setShowHiHandler();
        setShowHihiHandler();
        setLoloColorHandler();
        setLoColorHandler();
        setHiColorHandler();
        setHihiColorHandler();
    }

    /**
     *
     */
    private void setHihiColorHandler() {
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_HIHI_COLOR,
                new ColorChangeHandler<AbstractMarkedWidgetFigure>() {
                    @Override
                    protected void doHandle(
                            final AbstractMarkedWidgetFigure figure,
                            final Color color) {
                        figure.setHihiColor(color);
                    }
                });
    }

    /**
     *
     */
    private void setHiColorHandler() {
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_HI_COLOR,
                new ColorChangeHandler<AbstractMarkedWidgetFigure>() {
                    @Override
                    protected void doHandle(
                            final AbstractMarkedWidgetFigure figure,
                            final Color color) {
                        figure.setHiColor(color);
                    }
                });
    }

    /**
     *
     */
    private void setLoColorHandler() {
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_LO_COLOR,
                new ColorChangeHandler<AbstractMarkedWidgetFigure>() {
                    @Override
                    protected void doHandle(
                            final AbstractMarkedWidgetFigure figure,
                            final Color color) {
                        figure.setLoColor(color);
                    }
                });
    }

    /**
     *
     */
    private void setLoloColorHandler() {
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_LOLO_COLOR,
                new ColorChangeHandler<AbstractMarkedWidgetFigure>() {
                    @Override
                    protected void doHandle(
                            final AbstractMarkedWidgetFigure figure,
                            final Color color) {
                        figure.setLoloColor(color);
                    }
                });
    }

    /**
     *
     */
    private void setShowHihiHandler() {
        IWidgetPropertyChangeHandler showHihiHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
                figure.setShowHihi((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_SHOW_HIHI,
                showHihiHandler);
    }

    /**
     *
     */
    private void setShowHiHandler() {
        IWidgetPropertyChangeHandler showHiHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
                figure.setShowHi((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_SHOW_HI,
                showHiHandler);
    }

    /**
     *
     */
    private void setShowLoHandler() {
        IWidgetPropertyChangeHandler showLoHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
                figure.setShowLo((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_SHOW_LO,
                showLoHandler);
    }

    /**
     *
     */
    private void setShowLoloHandler() {
        IWidgetPropertyChangeHandler showLoloHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
                figure.setShowLolo((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_SHOW_LOLO,
                showLoloHandler);
    }

    /**
     *
     */
    private void setHihiLevelHandler() {
        IWidgetPropertyChangeHandler hihiHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
                figure.setHihiLevel((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_HIHI_LEVEL,
                hihiHandler);
    }

    /**
     *
     */
    private void setHiLevelHandler() {
        IWidgetPropertyChangeHandler hiHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
                figure.setHiLevel((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_HI_LEVEL,
                hiHandler);
    }

    /**
     *
     */
    private void setLoLevelHandler() {
        IWidgetPropertyChangeHandler loHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
                figure.setLoLevel((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_LO_LEVEL,
                loHandler);
    }

    /**
     *
     */
    private void setLoloLevelHandler() {
        // LoLo Level
        IWidgetPropertyChangeHandler loloHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
                figure.setLoloLevel((Double) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_LOLO_LEVEL,
                loloHandler);
    }

    /**
     *
     */
    private void setShowMarkersHandler() {
        IWidgetPropertyChangeHandler showMarkersHandler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                AbstractMarkedWidgetFigure figure = (AbstractMarkedWidgetFigure) refreshableFigure;
                figure.setShowMarkers((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS,
                showMarkersHandler);
    }

}
