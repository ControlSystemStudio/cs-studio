package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.SixteenBinaryBarModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableSixteenBinaryBarFigure;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Edit Part for Sixteen Binary Bar.
 *
 * @author Alen Vrecko
 *
 */
public class SixteenBinaryBarEditPart extends AbstractWidgetEditPart {

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        SixteenBinaryBarModel model = (SixteenBinaryBarModel) getWidgetModel();

        final RefreshableSixteenBinaryBarFigure bar = new RefreshableSixteenBinaryBarFigure();
        bar.setOnColor(getModelColor(SixteenBinaryBarModel.PROP_ON_COLOR));
        bar.setOffColor(getModelColor(SixteenBinaryBarModel.PROP_OFF_COLOR));
        bar.setLabelFont(getModelFont(SixteenBinaryBarModel.PROP_LABEL_FONT));
        bar.setHorizontal(model.getHorizontal());
        bar.setShowLabels(model.getShowLabels());
        bar.setValue(model.getValue());
        bar.setInternalBorderThickness(model.getInternalFrameThickness());
        bar.setInternalBorderColor(getModelColor(SixteenBinaryBarModel.PROP_INTERNAL_FRAME_COLOR));
        bar.setLabelColor(getModelColor(SixteenBinaryBarModel.PROP_LABEL_COLOR));
        bar.setBitRangeFrom(model.getBitRangeFrom());
        bar.setBitRangeTo(model.getBitRangeTo());
        return bar;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        // orientation
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
                rectangle.setHorizontal((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(SixteenBinaryBarModel.PROP_HORIZONTAL, handler);

        // labels
        handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
                rectangle.setShowLabels((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(SixteenBinaryBarModel.PROP_SHOW_LABELS,
                handler);

        // value
        handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
                rectangle.setValue((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(SixteenBinaryBarModel.PROP_VALUE, handler);

        // font
        setPropertyChangeHandler(SixteenBinaryBarModel.PROP_LABEL_FONT, new FontChangeHandler<RefreshableSixteenBinaryBarFigure>(){
            @Override
            protected void doHandle(RefreshableSixteenBinaryBarFigure figure, Font font) {
                figure.setLabelFont(font);
            }
        });

        // on color
        setPropertyChangeHandler(SixteenBinaryBarModel.PROP_ON_COLOR, new ColorChangeHandler<RefreshableSixteenBinaryBarFigure>(){
            @Override
            protected void doHandle(RefreshableSixteenBinaryBarFigure figure, Color color) {
                figure.setOnColor(color);
            }
        });

        // off color
        setPropertyChangeHandler(SixteenBinaryBarModel.PROP_OFF_COLOR, new ColorChangeHandler<RefreshableSixteenBinaryBarFigure>(){
            @Override
            protected void doHandle(RefreshableSixteenBinaryBarFigure figure, Color color) {
                figure.setOffColor(color);
            }
        });

        // i frame thickness
        handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                RefreshableSixteenBinaryBarFigure rectangle = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
                rectangle.setInternalBorderThickness((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(
                SixteenBinaryBarModel.PROP_INTERNAL_FRAME_THICKNESS, handler);

        // label color
        setPropertyChangeHandler(SixteenBinaryBarModel.PROP_LABEL_COLOR,
                new ColorChangeHandler<RefreshableSixteenBinaryBarFigure>(){
            @Override
            protected void doHandle(RefreshableSixteenBinaryBarFigure figure, Color color) {
                figure.setLabelColor(color);
            }
        });

        // frame color
        setPropertyChangeHandler(
                SixteenBinaryBarModel.PROP_INTERNAL_FRAME_COLOR, new ColorChangeHandler<RefreshableSixteenBinaryBarFigure>(){
                    @Override
                    protected void doHandle(RefreshableSixteenBinaryBarFigure figure, Color color) {
                        figure.setInternalBorderColor(color);
                    }
                });

        // bit range from
        handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure refreshableFigure) {
                RefreshableSixteenBinaryBarFigure figure = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
                figure.setBitRangeFrom((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(SixteenBinaryBarModel.PROP_BITS_FROM, handler);

        // bit range to
        handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure refreshableFigure) {
                RefreshableSixteenBinaryBarFigure figure = (RefreshableSixteenBinaryBarFigure) refreshableFigure;
                figure.setBitRangeTo((Integer) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(SixteenBinaryBarModel.PROP_BITS_TO, handler);
}

}