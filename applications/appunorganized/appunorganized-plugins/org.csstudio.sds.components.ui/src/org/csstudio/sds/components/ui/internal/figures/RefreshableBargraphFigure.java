/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.ui.internal.figures;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * A bargraph figure.
 *
 * @author Kai Meyer
 *
 */
public final class RefreshableBargraphFigure extends RectangleFigure implements
        IAdaptable {
    /**
     * Height of the text.
     */
    private static final int TEXTHEIGHT = 14;
    /**
     * Width of the text.
     */
    private static final int TEXTWIDTH = 46;
    /**
     * Width of the text.
     */
    private int _textWidth = 10;
    /**
     * The Strings, which are displayed in this figure.
     */
    private static final String[] LABELS = new String[] { "LOLO", "LO", "HI", "HIHI" };
    /**
     * Show markers at bottom or right.
     */
    private static final int BOTTOM_RIGHT = 1;
    /**
     * Show markers at top or left.
     */
    private static final int TOP_LEFT = 2;
    /**
     * Minimum value for this figure.
     */
    private double _minimum = 0.0;
    /**
     * Maximum value for this figure.
     */
    private double _maximum = 1.0;
    /**
     * The current value.
     */
    private double _value;
    /**
     * The boolean, which indicates, if the values should be shown or not.
     */
    private boolean _showValues = false;
    /**
     * The integer, which indicates, how the marks should be shown.
     */
    private int _showMarks = BOTTOM_RIGHT;
    /**
     * The integer, which indicates, how the scale should be shown.
     */
    private int _showScale = BOTTOM_RIGHT;
    /**
     * The count of sections in the scale.
     */
    private int _scaleSectionCount = 1;
    /**
     * The orientation (horizontal==true | vertical==false).
     */
    private boolean _orientationHorizontal = true;
    /**
     * The Color for the border.
     */
    private Color _borderColor;
    /**
     * The Map for the levels.
     */
    private final Map<String, Double> _levelMap = new HashMap<String, Double>();
    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;
    /**
     * The Scale of this figure.
     */
    private final Scale _scale;
    /**
     * The MarkerPanel of this figure.
     */
    private final MarkerPanel _markerPanel;
    /**
     * The FillRectangle of this figure.
     */
    private final FillRectangleFigure _fillRectangleFigure;
    /**
     * The Rectangle for the FillRectangleFigure.
     */
    private Rectangle _barRectangle;
    /**
     * The wideness of the Scale.
     */
    private final int _scaleWideness = 10;
    /**
     * The wideness of the Tickmarks.
     */
    private final int _tickMarkWideness = 10;
    /**
     * The boolean, which indicates, if the figure has a transparent background.
     */
    private boolean _transparent = true;
    /**
     * The boolean, which indicates, if only the fill value should be shown.
     */
    private boolean _showOnlyValue = false;
    private CrossedOutAdapter _crossedOutAdapter;
    private RhombusAdapter _rhombusAdapter;

    /**
     * Constructor.
     */
    public RefreshableBargraphFigure() {
        super();
        this.setSize(200, 30);
        this.initLevelMap();
        this.setLayoutManager(new XYLayout());
        _fillRectangleFigure = new FillRectangleFigure();
        _markerPanel = new MarkerPanel(_showMarks == TOP_LEFT,
                _orientationHorizontal);
        _scale = new Scale();
        _scale.setShowNegativeSections(true);
        this.add(_fillRectangleFigure);
        this.add(_markerPanel);
        this.add(_scale);
        this.refreshConstraints();
        // listen to figure movement events
        addFigureListener(new FigureListener() {
            public void figureMoved(final IFigure source) {
                refreshConstraints();
            }
        });
    }

    /**
     * Initializes the Map of levels.
     */
    private void initLevelMap() {
        _levelMap.put(LABELS[0], Double.valueOf(0.1));
        _levelMap.put(LABELS[1], Double.valueOf(0.3));
        _levelMap.put(LABELS[2], Double.valueOf(0.7));
        _levelMap.put(LABELS[3], Double.valueOf(0.9));
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        _crossedOutAdapter.paint(graphics);
        _rhombusAdapter.paint(graphics);
     }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void paintFigure(final Graphics graphics) {
        if (!_transparent) {
            graphics.setBackgroundColor(this.getBackgroundColor());
            Rectangle bounds = this.getBounds().getCopy();
            bounds.crop(this.getInsets());
            graphics.fillRectangle(bounds);
            graphics.setBackgroundColor(this.getBackgroundColor());
            graphics.setForegroundColor(_borderColor);
        }
    }

//    /**
//     * Gets the IFigure for the tooltip.
//     * @return IFigure The IFigure for the tooltip
//     */
//    private IFigure getToolTipFigure() {
//        Panel panel = new Panel();
//        panel.setLayoutManager(new ToolbarLayout(false));
//        panel.add(new Label("Value: " + _value));
//        panel.add(new Label("Minimum value: " + _minimum));
//        panel.add(new Label("Maximum value: " + _maximum));
//        for (int i = 0; i < LABELS.length; i++) {
//            panel.add(new Label(LABELS[i] + " level: "
//                    + _levelMap.get(LABELS[i])));
//        }
//        panel.setBackgroundColor(ColorConstants.tooltipBackground);
//        return panel;
//    }

    /**
     * Refreshes the Constraints.
     */
    public void refreshConstraints() {
        this.approximateTextWidth();
        _barRectangle = this.getBarRectangle();
        this.setConstraint(_fillRectangleFigure, _barRectangle);
        Rectangle bounds = this.getBounds().getCopy().crop(this.getInsets());
        this.setConstraint(_markerPanel, this.getMarkerPanelConstraint(bounds));
        this.setConstraint(_scale, this.getScaleConstraint(bounds));
        //this.setToolTip(this.getToolTipFigure());
    }

    /**
     * Gets the Constraints for the MarkerPanel.
     * @param bounds
     *            The bounds for the MarkerPanel
     * @return The constraints for the MarkerPanel
     */
    private Rectangle getMarkerPanelConstraint(final Rectangle bounds) {
        if (_orientationHorizontal) {
            _markerPanel.setReferencePositions(_barRectangle.x, _barRectangle.x
                    + _barRectangle.width);
            if (_showMarks == BOTTOM_RIGHT) {
                return new Rectangle(1, bounds.height - (TEXTHEIGHT+_tickMarkWideness),
                        bounds.width - 2, (TEXTHEIGHT+_tickMarkWideness) - 2);
            }
            if (_showMarks == TOP_LEFT) {
                return new Rectangle(1, 1, bounds.width - 2, (TEXTHEIGHT+_tickMarkWideness) - 1);
            }
        } else {
            _markerPanel.setReferencePositions(_barRectangle.y, _barRectangle.y
                    + _barRectangle.height);
            if (_showMarks == BOTTOM_RIGHT) {
                return new Rectangle(_barRectangle.width+(_barRectangle.x), 1, TEXTWIDTH - 1,
                        bounds.height - 2);
            }
            if (_showMarks == TOP_LEFT) {
                return new Rectangle(1, 1, TEXTWIDTH - 1, bounds.height - 2);
            }
        }
        return new Rectangle(0, 0, 0, 0);
    }

    /**
     * Gets the constraints for the Scale.
     * @param bounds
     *            The bounds for the Scale
     * @return Rectangle The Constraints for the Scale
     */
    private Rectangle getScaleConstraint(final Rectangle bounds) {
        _scale.setHorizontalOrientation(_orientationHorizontal);
        _scale.setIncrement((_maximum-_minimum)/Math.max(1, _scaleSectionCount));
        _scale.setStartValue(_minimum);
        if (_orientationHorizontal) {
            return getHorizontalScaleConstraint(bounds);
        } else {
            return getVerticalScaleConstraint(bounds);
        }
    }

    /**
     * Gets the constraints for the Vertical Scale.
     * @param bounds
     *            The bounds for the Vertical Scale
     * @return Rectangle The Constraints for the Vertical Scale
     */
    private Rectangle getVerticalScaleConstraint(final Rectangle bounds) {
        int length = _barRectangle.height/Math.max(1, _scaleSectionCount);
        if (_scaleSectionCount==1) {
            length--;
        }
        _scale.setLength(length);
        _scale.setReferencePositions(_barRectangle.y+_barRectangle.height);
        _scale.setRegion(_barRectangle.y-5, _barRectangle.y+_barRectangle.height);
        int width = _scaleWideness;
        if (_showValues) {
            width = width + _textWidth;
        }
        if (_showScale == BOTTOM_RIGHT) {
            return new Rectangle(_barRectangle.x + _barRectangle.width
                    - _scaleWideness/2, 0, width, bounds.height);
        }
        if (_showScale == TOP_LEFT) {
            int x = _barRectangle.x-_scaleWideness/2;
            if (_showValues) {
                x = x- _textWidth;
            }
            return new Rectangle(x, 0, width, bounds.height);
        }
        return new Rectangle(0, 0, 0, 0);
    }

    /**
     * Gets the constraints for the Horizontal Scale.
     * @param bounds
     *            The bounds for the Horizontal Scale
     * @return Rectangle The Constraints for the Horizontal Scale
     */
    private Rectangle getHorizontalScaleConstraint(final Rectangle bounds) {
        _scale.setReferencePositions(_barRectangle.x);
        int length = _barRectangle.width/Math.max(1, _scaleSectionCount);
        if (_scaleSectionCount==1) {
            length--;
        }
        _scale.setLength(length);
        _scale.setRegion(_barRectangle.x, _barRectangle.x+_barRectangle.width+5);
        int height = _scaleWideness;
        if (_showValues) {
            height = height + TEXTHEIGHT;
        }
        if (_showScale == BOTTOM_RIGHT) {
            return new Rectangle(0, _barRectangle.y + _barRectangle.height
                    - _scaleWideness/2 - 1, bounds.width, height);
        }
        if (_showScale == TOP_LEFT) {
            int y = _barRectangle.y-_scaleWideness/2;
            if (_showValues) {
                y = y - TEXTHEIGHT;
            }
            return new Rectangle(0, y, bounds.width, height);
        }
        return new Rectangle(0, 0, 0, 0);
    }

    /**
     * Calculate the real length of this bargraph. The value is calculated, to
     * fit the scale completely into the bargraph
     * @param length
     *            The given length
     * @return int The new length
     */
    private int calculateRealLength(final int length) {
        if ((_showScale == BOTTOM_RIGHT) || (_showScale == TOP_LEFT)) {
            int neededScaleLines = _scaleSectionCount + 1;
            return length - ((length - neededScaleLines) % _scaleSectionCount);
        }
        return length;
    }

    /**
     * Gets the rectangle for the bargraph.
     * @return Rectangle The rectangle for the bargraph
     */
    private Rectangle getBarRectangle() {
        Rectangle bounds = this.getBounds().getCopy();
        bounds.crop(this.getInsets());
        if (_orientationHorizontal) {
            return getHorizontalBarRectangle(bounds);
        } else {
            return getVerticalBarRectangle(bounds);
        }

    }

    /**
     * Gets the rectangle for the vertical bargraph.
     * @return Rectangle The rectangle for the vertical bargraph
     */
    private Rectangle getVerticalBarRectangle(final Rectangle bounds) {
        int yCorrection = 0;
        int xCorrection = 0;
        int widthCorrection = 0;
        if (_showMarks == TOP_LEFT) {
            xCorrection = TEXTWIDTH;
            yCorrection = (TEXTHEIGHT+_tickMarkWideness) / 2;
        }
        if (_showMarks == BOTTOM_RIGHT) {
            widthCorrection = TEXTWIDTH;
            yCorrection = (TEXTHEIGHT+_tickMarkWideness) / 2;
        }
        if (_showScale == TOP_LEFT) {
            if (xCorrection==0) {
                xCorrection = _scaleWideness/2;
                if (_showValues) {
                    xCorrection = xCorrection + _textWidth;
                    yCorrection = (TEXTHEIGHT+_scaleWideness) / 2;
                }
            }
        }
        if (_showScale == BOTTOM_RIGHT) {
            if (widthCorrection==0) {
                widthCorrection = _scaleWideness/2;
                if (_showValues) {
                    widthCorrection = widthCorrection + _textWidth;
                    yCorrection = (TEXTHEIGHT+_scaleWideness) / 2;
                }
            }
        }
        return new Rectangle(xCorrection, yCorrection, bounds.width
                - (xCorrection + widthCorrection), this
                .calculateRealLength(bounds.height - 2 * yCorrection));
    }

    /**
     * Gets the rectangle for the horizontal bargraph.
     * @return Rectangle The rectangle for the horizontal bargraph
     */
    private Rectangle getHorizontalBarRectangle(final Rectangle bounds) {
        int yCorrection = 0;
        int heightCorrection = 0;
        int xCorrection = 0;
        if (_showMarks == TOP_LEFT) {
            yCorrection = (TEXTHEIGHT+_tickMarkWideness);
            xCorrection = (TEXTWIDTH / 2);
        } else if (_showMarks == BOTTOM_RIGHT) {
            heightCorrection = (TEXTHEIGHT+_tickMarkWideness);
            xCorrection = (TEXTWIDTH / 2);
        }
        if (_showScale == TOP_LEFT) {
            if (yCorrection==0) {
                yCorrection = _scaleWideness/2;
                if (_showValues) {
                    yCorrection = yCorrection + TEXTHEIGHT;
                    xCorrection = (_textWidth / 2);
                }
            }
        }
        if (_showScale == BOTTOM_RIGHT) {
            if (heightCorrection==0) {
                heightCorrection = _scaleWideness/2;
                if (_showValues) {
                    heightCorrection = heightCorrection + TEXTHEIGHT;
                    xCorrection = (_textWidth / 2);
                }
            }
        }
        return new Rectangle(xCorrection, yCorrection, this
                .calculateRealLength(bounds.width - 2 * xCorrection),
                bounds.height - (yCorrection + heightCorrection));
    }

    /**
     * Gets the weight (0.0 - 1.0) for the value.
     * @param value
     *            The value, which weight should be calculated.
     * @return double The weight for the value
     */
    private double getWeight(final double value) {
        double max = _maximum - _minimum;
        if (max == 0) {
            max = 0.001;
        }
        return (value - _minimum) / max;
    }

    /**
     * Calculates and also sets the fill grade for the bar based on the current value and
     * min/max borders.
     */
    private void updateFillRectangle() {
        double fillgrade = (_value - _minimum) / (_maximum - _minimum);
        _fillRectangleFigure.setFill(fillgrade);
    }


    /**
     * This method is a tribute to unit tests, which need a way to test the
     * performance of the figure implementation. Implementors should produce
     * some random changes and refresh the figure, when this method is called.
     *
     */
    public void randomNoiseRefresh() {
        this.setFill(Math.random() * 100);
        repaint();
    }

    /**
     * Sets if only the value should be shown instead of the area from minimum to fill level.
     * @param showOnlyValue True if only the value should be shown, false otherwise
     */
    public void setShowOnlyValue(final boolean showOnlyValue) {
        _showOnlyValue = showOnlyValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setForegroundColor(final Color fg) {
        _scale.setForegroundColor(fg);
        super.setForegroundColor(fg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBackgroundColor(final Color bg) {
        _markerPanel.setBackgroundColor(bg);
        super.setBackgroundColor(bg);
    }

    /**
     * Sets the color for the border.
     * @param borderRGB
     *            The RGB-value of the Color for the border
     */
    public void setBorderColor(final Color borderRGB) {
        _borderColor = borderRGB;
        _fillRectangleFigure.setBorderColor(_borderColor);
    }

    /**
     * Sets the default fill Color.
     * @param color
     *            the color
     */
    public void setDefaultFillColor(final Color color) {
        _fillRectangleFigure.setDefaultFillColor(color);
    }

    /**
     * Gets the default fill Color.
     * @return Color The color default fill Color
     */
    public Color getDefaultFillColor() {
        return _fillRectangleFigure.getDefaultFillColor();
    }

    /**
     * Sets the color for the background of the fill-area.
     * @param color
     *            the background color
     */
    public void setFillBackgroundColor(final Color color) {
        _fillRectangleFigure.setBackgroundColor(color);
    }

    /**
     * Sets the fill grade.
     * @param value
     *            The fill grade.
     */
    public void setFill(final double value) {
        _value = value;
        updateFillRectangle();
    }


    /**
     * Gets the fill grade.
     * @return double The fill grade
     */
    public double getFill() {
        return _fillRectangleFigure.getFill();
    }

    /**
     * Sets the orientation (horizontal==true | vertical==false).
     * @param horizontal
     *            The orientation.
     */
    public void setOrientation(final boolean horizontal) {
        _orientationHorizontal = horizontal;
        _scale.setHorizontalOrientation(_orientationHorizontal);
        _markerPanel.setHorizontalOrientation(horizontal);
        this.refreshConstraints();
    }

    /**
     * Gets the orientation (horizontal==true | vertical==false).
     * @return boolean The orientation
     */
    public boolean getOrientation() {
        return _orientationHorizontal;
    }

    /**
     * Sets the minimum value.
     * @param min
     *            The minimum value
     */
    public void setMinimum(final double min) {
        _minimum = min;
        this.refreshConstraints();
        updateFillRectangle();
    }

    /**
     * Gets the minimum value.
     * @return double The minimum value
     */
    public double getMinimum() {
        return _minimum;
    }

    /**
     * Sets the lolo level.
     * @param loloLevel
     *            The lolo level
     */
    public void setLoloLevel(final double loloLevel) {
        _levelMap.put(LABELS[0], loloLevel);
        this.refreshConstraints();
    }

    /**
     * Gets the lolo level.
     * @return double The lolo level
     */
    public double getLoloLevel() {
        return _levelMap.get(LABELS[0]);
    }

    /**
     * Sets the lo level.
     * @param loLevel
     *            The lo level
     */
    public void setLoLevel(final double loLevel) {
        _levelMap.put(LABELS[1], loLevel);
        this.refreshConstraints();
    }

    /**
     * Gets the lo level.
     * @return double The lo level
     */
    public double getLoLevel() {
        return _levelMap.get(LABELS[1]);
    }

    /**
     * Sets the hi level.
     * @param hiLevel
     *            The hi level
     */
    public void setHiLevel(final double hiLevel) {
        _levelMap.put(LABELS[2], hiLevel);
        this.refreshConstraints();
    }

    /**
     * Gets the hi level.
     * @return double The hi level
     */
    public double getHiLevel() {
        return _levelMap.get(LABELS[2]);
    }

    /**
     * Sets the hihi level.
     * @param hihiLevel
     *            The hihi level
     */
    public void setHihiLevel(final double hihiLevel) {
        _levelMap.put(LABELS[3], hihiLevel);
        this.refreshConstraints();
    }

    /**
     * Gets the hihi level.
     * @return double The hihi level
     */
    public double getHihiLevel() {
        return _levelMap.get(LABELS[3]);
    }

    /**
     * Sets the maximum value.
     * @param max
     *            The maximum value
     */
    public void setMaximum(final double max) {
        _maximum = max;
        this.refreshConstraints();
        updateFillRectangle();
    }

    public void approximateTextWidth() {
        float width = 14;
        if ((this.getFont()!=null) && (this.getFont().getFontData().length>0) && (this.getFont().getFontData()[0]!=null)) {
            width = this.getFont().getFontData()[0].height;
        }
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        int minLength = (int) (format.format(_minimum).length()*width);
        _textWidth = minLength;
        if ((_showScale==BOTTOM_RIGHT) || (_showScale==TOP_LEFT)) {
            double increment = (_maximum-_minimum)/Math.max(1, _scaleSectionCount);
            for (int i=0;i<_scaleSectionCount;i++) {
                int value = (int) (format.format(minLength + (i*increment)).length()*width);
                _textWidth = Math.max(minLength, value);
            }
        }
        int maxLength = (int) (format.format(_maximum).length()*width);
        _textWidth = Math.max(_textWidth, maxLength);
    }

    /**
     * Gets the maximum value.
     * @return double The maximum value
     */
    public double getMaximum() {
        return _maximum;
    }

    /**
     * Sets, if the values should be shown.
     * @param showValues
     *            True, if the values should be shown, false otherwise
     */
    public void setShowValues(final boolean showValues) {
        _showValues = showValues;
        _scale.setShowValues(showValues);
        this.refreshConstraints();
    }

    /**
     * Gets, if the values should be shown.
     * @return boolean True, if the values should be shown, false otherwise
     */
    public boolean getShowValues() {
        return _showValues;
    }

    /**
     * Sets, how the marks should be shown.
     * @param showMarks
     *            0 = don't show, 1 = show Bottom/Right, 2 = show Top/Left
     */
    public void setShowMarks(final int showMarks) {
        _showMarks = showMarks;
        _markerPanel.setTopLeftAlignment(showMarks == TOP_LEFT);
        this.refreshConstraints();
    }

    /**
     * Gets, how the marks should be shown.
     * @return boolean 0 = don't show, 1 = show Bottom/Right, 2 = show Top/Left
     */
    public int getShowMarks() {
        return _showMarks;
    }

    /**
     * Sets, how the scale should be shown.
     *
     * @param showScale
     *            0 = don't show, 1 = show Bottom/Right, 2 = show Top/Left
     */
    public void setShowScale(final int showScale) {
        _showScale = showScale;
        _scale.setAlignment(showScale == TOP_LEFT);
        this.refreshConstraints();
    }

    /**
     * Gets, how the scale should be shown.
     *
     * @return int 0 = don't show, 1 = show Bottom/Right, 2 = show Top/Left
     */
    public int getShowScale() {
        return _showScale;
    }

    /**
     * Sets the count of sections in the scale.
     *
     * @param scaleSectionCount
     *            The count of sections in the scale
     */
    public void setScaleSectionCount(final int scaleSectionCount) {
        _scaleSectionCount = scaleSectionCount;
        this.refreshConstraints();
    }

    /**
     * Gets the count of sections in the scale.
     *
     * @return int The count of sections in the scale.
     */
    public int getScaleSectionCount() {
        return _scaleSectionCount;
    }

    /**
     * Sets, if this widget should have a transparent background.
     * @param transparent
     *                 The new value for the transparent property
     */
    public void setTransparent(final boolean transparent) {
        _transparent = transparent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if (_borderAdapter == null) {
                _borderAdapter = new BorderAdapter(this);
            }
            return _borderAdapter;
        } else if(adapter == ICrossedFigure.class) {
            if(_crossedOutAdapter==null) {
                _crossedOutAdapter = new CrossedOutAdapter(this);
            }
            return _crossedOutAdapter;
        } else if(adapter == IRhombusEquippedWidget.class) {
            if(_rhombusAdapter==null) {
                _rhombusAdapter = new RhombusAdapter(this);
            }
            return _rhombusAdapter;
        }

        return null;
    }

    /**
     * This class represents the bargraph.
     *
     * @author Kai Meyer
     */
    private final class FillRectangleFigure extends RectangleFigure {
        /**
         * The fill grade (0 - 1).
         */
        private double _fillGrade = 0.5;
        /**
         * The default fill Color.
         */
        private Color _defaultFillColor;
        /**
         * The Color for the border.
         */
        private Color _borderColor;

        /**
         * {@inheritDoc}
         */
        @Override
        public void paintFigure(final Graphics graphics) {
            Rectangle bounds = this.getBounds();
            graphics.setForegroundColor(this.getForegroundColor());
            graphics.setBackgroundColor(this.getBackgroundColor());
            graphics.fillRectangle(bounds);
            graphics.setBackgroundColor(this.getDefaultFillColor());
            if (_showOnlyValue) {
                PointList list = new PointList();
                if (_orientationHorizontal) {
                    int valueX = (int) Math.round(bounds.width * (this.getFill())) + bounds.x;
                    list.addPoint(valueX, bounds.y);
                    list.addPoint(valueX-4, bounds.y+(bounds.height/2));
                    list.addPoint(valueX, bounds.y+bounds.height);
                    list.addPoint(valueX+4, bounds.y+(bounds.height/2));
                } else {
                    int valueY = (int) Math.round(bounds.y + bounds.height - (bounds.height * (this.getFill())));
                    list.addPoint(bounds.x, valueY);
                    list.addPoint(bounds.x+(bounds.width/2), valueY-4);
                    list.addPoint(bounds.x+bounds.width, valueY);
                    list.addPoint(bounds.x+(bounds.width/2), valueY+4);
                }
                graphics.fillPolygon(list);
                graphics.drawPolygon(list);
            } else {
                Rectangle fillRectangle = this.getFillLevelRectangle(bounds);
                graphics.fillRectangle(fillRectangle);
                graphics.setForegroundColor(this.getBorderColor());
                graphics.drawRectangle(new Rectangle(bounds.x, bounds.y,
                        bounds.width - 1, bounds.height - 1));
                graphics.drawRectangle(new Rectangle(fillRectangle.x,
                        fillRectangle.y, fillRectangle.width - 1,
                        fillRectangle.height - 1));
            }
        }

        /**
         * Gets the rectangle for the fill level.
         *
         * @param area
         *            The rectangle of the bargraph
         * @return Rectangle The rectangle for the fill level
         */
        private Rectangle getFillLevelRectangle(final Rectangle area) {
            if (_orientationHorizontal) {
                int newW = (int) Math.round(area.width * (this.getFill()));
                return new Rectangle(area.getLocation(), new Dimension(newW+1,
                        area.height));
            }
            int newH = (int) Math.round(area.height * (this.getFill())) +1;
            return new Rectangle(area.x, area.y + area.height - newH,
                    area.width, newH);
        }

        /**
         * Sets the fill grade.
         * @param fill
         *            The fill grade.
         */
        public void setFill(final double fill) {
            _fillGrade = fill;
        }

        /**
         * Gets the fill grade.
         * @return double The fill grade
         */
        public double getFill() {
            return _fillGrade;
        }

        /**
         * Sets the default fill Color.
         * @param defaultFillColor
         *            The default fill Color
         */
        public void setDefaultFillColor(final Color defaultFillColor) {
            _defaultFillColor = defaultFillColor;
        }

        /**
         * Gets the default fill Color.
         * @return Color The color default fill Color
         */
        public Color getDefaultFillColor() {
            return _defaultFillColor;
        }

        /**
         * Sets the color for the border.
         * @param borderColor
         *            The Color for the border
         */
        public void setBorderColor(final Color borderColor) {
            _borderColor = borderColor;
        }

        /**
         * Gets the color for the border.
         * @return Color The color for the border
         */
        public Color getBorderColor() {
            return _borderColor;
        }
    }

    /**
     * This Figure contains the Markers.
     * @author Kai Meyer
     */
    private final class MarkerPanel extends Panel {
        /**
         * The List of Markers.
         */
        private final List<Marker> _markerList = new LinkedList<Marker>();
        /**
         * The alignment for the Markers.
         */
        private boolean _topLeft;
        /**
         * The orientation of this figure.
         */
        private boolean _isHorizontal;
        /**
         * The start position.
         */
        private int _start = 10;
        /**
         * The end position.
         */
        private int _end = 100;

        /**
         * Constructor.
         *
         * @param topLeft
         *            true, if the Marker is on top/left of the bargraph, false
         *            otherwise
         * @param isHorizontal
         *            true, if the Marker has a horizontal orientation
         */
        public MarkerPanel(final boolean topLeft, final boolean isHorizontal) {
            this.setLayoutManager(new XYLayout());
            _topLeft = topLeft;
            _isHorizontal = isHorizontal;
            Marker marker = null;
            for (String element : LABELS) {
                marker = new Marker(element, _topLeft, _isHorizontal);
                marker.setForegroundColor(this.getForegroundColor());
                this.add(marker);
                _markerList.add(marker);
            }
            // listen to figure movement events
            addFigureListener(new FigureListener() {
                public void figureMoved(final IFigure source) {
                    refreshConstraints();
                }
            });

            this.refreshConstraints();
        }

        /**
         * Refreshes the constraints.
         */
        private void refreshConstraints() {
            if (_isHorizontal) {
                for (int i = 0; i < _markerList.size(); i++) {
                    double weight = getWeight(_levelMap.get(LABELS[i]));
                    if (Double.isNaN(weight) || (weight < 0) || (weight > 1)) {
                        _markerList.get(i).setVisible(false);
                    } else {
                        _markerList.get(i).setVisible(true);
                        int x = _start + (int) ((_end - _start) * weight) - 1
                                - TEXTWIDTH / 2;
                        this.setConstraint(_markerList.get(i), new Rectangle(x,
                                    0, TEXTWIDTH, bounds.height));
                    }
                }
            } else {
                for (int i = 0; i < _markerList.size(); i++) {
                    double weight = getWeight(_levelMap.get(LABELS[i]));
                    if (Double.isNaN(weight) || (weight < 0) || (weight > 1)) {
                        _markerList.get(i).setVisible(false);
                    } else {
                        _markerList.get(i).setVisible(true);
                        int y = _start -1 + (int) ((_end - _start) * (1 - weight))
                                - (TEXTHEIGHT+_tickMarkWideness)/2;
                        this.setConstraint(_markerList.get(i), new Rectangle(1,
                                y, bounds.width, (TEXTHEIGHT+_tickMarkWideness)));
                    }
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paintFigure(final Graphics graphics) {
            if (!_transparent) {
                Rectangle bounds = this.getBounds();
                graphics.setBackgroundColor(this.getBackgroundColor());
                graphics.setForegroundColor(ColorConstants.black);
                graphics.fillRectangle(bounds);
            }
        }

        /**
         * Sets the reference values for this figure.
         *
         * @param start
         *            The start value
         * @param end
         *            The end value
         */
        public void setReferencePositions(final int start, final int end) {
            _start = start;
            _end = end;
            this.refreshConstraints();
        }

        /**
         * Sets the orientation of this figure.
         *
         * @param isHorizontal
         *            The orientation of this figure
         *            (true=horizontal;false=vertical)
         */
        public void setHorizontalOrientation(final boolean isHorizontal) {
            _isHorizontal = isHorizontal;
            for (Marker marker : _markerList) {
                marker.setHorizontalOrientation(isHorizontal);
            }
            this.refreshConstraints();
        }

        /**
         * Sets the alignment of this figure.
         *
         * @param topLeft
         *            The alignment of this figure
         *            (true=top/left;false=bottom/right)
         */
        public void setTopLeftAlignment(final boolean topLeft) {
            _topLeft = topLeft;
            for (Marker marker : _markerList) {
                marker.setTopLeftAlignment(topLeft);
            }
            this.refreshConstraints();
        }
    }

    /**
     * This class represents a Marker.
     *
     * @author Kai Meyer
     *
     */
    private final class Marker extends RectangleFigure {
        /**
         * The orientation of this Marker.
         */
        private boolean _isHorizontal;

        /**
         * The alignment of this Marker.
         */
        private boolean _topLeft;
        /**
         * The needed space of a {@link TickMark}.
         */
        private final int _tickMarkSpace = 9;
        /**
         * The Label for the text.
         */
        private final Label _textLabel;

        /**
         * The Tickmark.
         */
        private final TickMark _tickMark;

        /**
         * Constructor.
         *
         * @param key
         *            The text to display
         * @param topLeft
         *            True, if the marker should be above the y value, false
         *            otherwise
         * @param isHorizontal
         *            True, if the marker should have a horizontal orientation,
         *            false otherwise
         */
        public Marker(final String key, final boolean topLeft,
                final boolean isHorizontal) {
            this.setLayoutManager(new XYLayout());
            _textLabel = new Label(key.toString());
            _textLabel.setForegroundColor(this.getForegroundColor());
            _tickMark = new TickMark();
            _tickMark.setForegroundColor(this.getForegroundColor());
            _tickMark.setBackgroundColor(getDefaultFillColor());
            this.add(_tickMark);
            this.add(_textLabel);
            this.refreshConstraints();
            addFigureListener(new FigureListener() {
                public void figureMoved(final IFigure source) {
                    refreshConstraints();
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paintFigure(final Graphics graphics) {
            // nothing to do;
        };

        /**
         * {@inheritDoc}
         */
        @Override
        public void setVisible(final boolean visible) {
            _textLabel.setVisible(visible);
            _tickMark.setVisible(visible);
        }

        /**
         * Sets the orientation of this figure.
         *
         * @param isHorizontal
         *            The orientation of this figure
         *            (true=horizontal;false=vertical)
         */
        public void setHorizontalOrientation(final boolean isHorizontal) {
            _isHorizontal = isHorizontal;
            _tickMark.setHorizontalOrientation(isHorizontal);
            this.refreshLabel();
        }

        /**
         * Sets the alignment of this figure.
         *
         * @param topLeft
         *            The alignment of this figure
         *            (true=top/left;false=bottom/right)
         */
        public void setTopLeftAlignment(final boolean topLeft) {
            _topLeft = topLeft;
            _tickMark.setTopLeftAlignment(topLeft);
            this.refreshLabel();
        }

        private void refreshConstraints() {
            Rectangle bounds = this.getBounds();
            if (_isHorizontal) {
                if (_topLeft) {
                    this.setConstraint(_tickMark, new Rectangle(0, bounds.height-_tickMarkSpace, bounds.width, _tickMarkSpace));
                    this.setConstraint(_textLabel, new Rectangle(0, 0, bounds.width, bounds.height-_tickMarkSpace));
                } else {
                    this.setConstraint(_tickMark, new Rectangle(0, 0, bounds.width, _tickMarkSpace));
                    this.setConstraint(_textLabel, new Rectangle(0, _tickMarkSpace, bounds.width, bounds.height-_tickMarkSpace));
                }
            } else {
                if (_topLeft) {
                    this.setConstraint(_tickMark, new Rectangle(bounds.width-_tickMarkSpace, 0, _tickMarkSpace, bounds.height));
                    this.setConstraint(_textLabel, new Rectangle(0, 0, bounds.width-_tickMarkSpace, bounds.height));
                } else {
                    this.setConstraint(_tickMark, new Rectangle(0, 0, _tickMarkSpace, bounds.height));
                    this.setConstraint(_textLabel, new Rectangle(_tickMarkSpace, 0, bounds.width-_tickMarkSpace, bounds.height));
                }
            }
            refreshLabel();
        }

        /**
         * Refreshes the Label.
         */
        private void refreshLabel() {
            if (_isHorizontal) {
                _textLabel.setTextPlacement(PositionConstants.WEST);
                if (_topLeft) {
                    _textLabel.setTextAlignment(PositionConstants.BOTTOM);
                } else {
                    _textLabel.setTextAlignment(PositionConstants.TOP);
                }
            } else {
                _textLabel.setTextPlacement(PositionConstants.NORTH);
                if (_topLeft) {
                    _textLabel.setTextAlignment(PositionConstants.RIGHT);
                } else {
                    _textLabel.setTextAlignment(PositionConstants.LEFT);
                }
            }
        }

        /**
         * This class represents a tigmark.
         *
         * @author Kai Meyer
         */
        private final class TickMark extends RectangleFigure {
            /**
             * The width of this marker.
             */
            private int _width;

            /**
             * The height of this marker.
             */
            private int _height;

            /**
             * The direction of this Marker.
             */
            private int _direction = 1;

            /**
             * The orientation of this Marker.
             */
            private boolean _isHorizontal;

            /**
             * {@inheritDoc}
             */
            @Override
            public void paintFigure(final Graphics graphics) {
                Rectangle bounds = this.getBounds();
                graphics.setForegroundColor(this.getForegroundColor());
                graphics.setBackgroundColor(getDefaultFillColor());
                PointList pointList = new PointList();
                int x;
                int y;
                if (_isHorizontal) {
                    x = bounds.x + bounds.width / 2;
                    y = bounds.y;
                    if (_direction < 0) {
                        y = y + bounds.height;
                    }
                    pointList.addPoint(x, y);
                    pointList.addPoint(x - _width, y + _height * _direction);
                    pointList.addPoint(x + _width, y + _height * _direction);
                    pointList.addPoint(x, y);
                } else {
                    x = bounds.x;
                    y = bounds.y + bounds.height / 2;
                    if (_direction < 0) {
                        x = x + bounds.width - 2;
                    }
                    pointList.addPoint(x, y);
                    pointList.addPoint(x + _width * _direction, y - _height);
                    pointList.addPoint(x + _width * _direction, y + _height);
                    pointList.addPoint(x, y);
                }
                graphics.fillPolygon(pointList);
                graphics.drawPolyline(pointList);
            }

            /**
             * Sets the orientation of this figure.
             *
             * @param isHorizontal
             *            The orientation of this figure
             *            (true=horizontal;false=vertical)
             */
            public void setHorizontalOrientation(final boolean isHorizontal) {
                _isHorizontal = isHorizontal;
                if (isHorizontal) {
                    _width = 3;
                    _height = 5;
                } else {
                    _width = 5;
                    _height = 3;
                }
            }

            /**
             * Sets the alignment of this figure.
             *
             * @param topLeft
             *            The alignment of this figure
             *            (true=top/left;false=bottom/right)
             */
            public void setTopLeftAlignment(final boolean topLeft) {
                if (topLeft) {
                    _direction = -1;
                } else {
                    _direction = 1;
                }
            }
        }
    }

    /**
     * This class represents a scale.
     *
     * @author Kai Meyer
     */
    private final class Scale extends RectangleFigure {
        /**
         * The length of this Scale.
         */
        private int _length;
        /**
         * The direction of this Scale.
         */
        private boolean _isHorizontal;
        /**
         * The Alignment for the {@link ScaleMarker}s.
         */
        private boolean _isTopLeft;
        /**
         * The start position.
         */
        private int _refPos = 10;
        /**
         * The begin of the region, which surrounds the Markers.
         */
        private int _begin;
        /**
         * The end of the region, which surrounds the Markers.
         */
        private int _end;
        /**
         * True, if the negativ sections should be draan, false otherwise.
         */
        private boolean _showNegativSections = false;
        /**
         * The length of the lines.
         */
        private final int _wideness = 10;
        /**
         * True, if the first Marker should be shown, false otherwise.
         */
        private final boolean _showFirst = true;
        /**
         * True, if the values of the Markers should be shown, false otherwise.
         */
        private boolean _showValues = false;
        /**
         * The size of one step in a Scale.
         */
        private double _increment = 1;
        /**
         * The start-value for the markers.
         */
        private double _startValue = 0;

        /**
         * The List of positive ScaleMarkers.
         */
        private final List<ScaleMarker> _posScaleMarkers = new LinkedList<ScaleMarker>();
        /**
         * The List of negative ScaleMarkers.
         */
        private final List<ScaleMarker> _negScaleMarkers = new LinkedList<ScaleMarker>();

        /**
         * Constructor.
         */
        public Scale() {
            this.setLayoutManager(new XYLayout());
            this.refreshConstraints();
            // listen to figure movement events
            addFigureListener(new FigureListener() {
                public void figureMoved(final IFigure source) {
                    refreshConstraints();
                }
            });
        }

        /**
         * Refreshes the Constraints.
         */
        private void refreshConstraints() {
            if ((_length==0) || (this.getBounds().height==0) || (this.getBounds().width==0)) {
                _posScaleMarkers.clear();
                _negScaleMarkers.clear();
                this.removeAll();
                return;
            }
            if (_isHorizontal) {
                refreshHorizontalConstraints();
            } else {
                refreshVerticalConstraints();
            }
        }

        /**
         * Refreshes the Constraints at Vertical orientation.
         */
        private void refreshVerticalConstraints() {
            int index = 0;
            int pos = _refPos;
            pos = pos - 1;
            int width = _wideness;
            if (_showValues) {
                width = _textWidth + _wideness;
            }
            double value = _startValue;
            while ((pos >= 0) && (pos >= _begin)) {
                if (pos<=_end) {
                    if (index>=_posScaleMarkers.size()) {
                        this.addScaleMarker(index, _posScaleMarkers);
                    }
                    this.setConstraint(_posScaleMarkers.get(index), new Rectangle(0,pos-TEXTHEIGHT/2,width,TEXTHEIGHT));
                    this.refreshScaleMarker(_posScaleMarkers.get(index), value, (((index>0) || _showFirst) && _showValues));
                    index++;
                }
                value = value + _increment;
                pos = pos - _length;
            }
            this.removeScaleMarkers(index, _posScaleMarkers);
            if (_showNegativSections) {

                pos = _refPos + _length - 1;
                index = 0;
                value = _startValue - _increment;
                while ((pos < this.getBounds().height) && (pos <= _end)) {
                    if (pos>=_begin) {
                        if (index>=_negScaleMarkers.size()) {
                            this.addScaleMarker(index, _negScaleMarkers);
                        }
                        this.setConstraint(_negScaleMarkers.get(index), new Rectangle(0,pos-TEXTHEIGHT/2,width,TEXTHEIGHT));
                        this.refreshScaleMarker(_negScaleMarkers.get(index), value, _showValues);
                        index++;
                    }
                    value = value - _increment;
                    pos = pos + _length;
                }
                this.removeScaleMarkers(index, _negScaleMarkers);
            }
        }

        /**
         * Refreshes the Constraints at horizontal orientation.
         */
        private void refreshHorizontalConstraints() {
            int index = 0;
            int pos = _refPos;
            int height = _wideness;
            if (_showValues) {
                height = TEXTHEIGHT + _wideness;
            }
            double value = _startValue;
            while ((pos <= this.getBounds().width) && (pos <= _end)) {
                if (pos>=_begin) {
                    if (index>=_posScaleMarkers.size()) {
                        this.addScaleMarker(index, _posScaleMarkers);
                    }
                    this.setConstraint(_posScaleMarkers.get(index), new Rectangle(pos-_textWidth/2,0,_textWidth,height));
                    this.refreshScaleMarker(_posScaleMarkers.get(index), value, (((index>0) || _showFirst) && _showValues));
                    index++;
                }
                value = value + _increment;
                pos = pos + _length;
            }
            this.removeScaleMarkers(index, _posScaleMarkers);
            if (_showNegativSections) {
                pos = _refPos - _length;
                index = 0;
                value = _startValue - _increment;
                while ((pos > 0) && (pos >= _begin)) {
                    if (pos<=_end) {
                        if (index>=_negScaleMarkers.size()) {
                            this.addScaleMarker(index, _negScaleMarkers);
                        }
                        this.setConstraint(_negScaleMarkers.get(index), new Rectangle(pos-_textWidth/2,0,_textWidth,height));
                        this.refreshScaleMarker(_negScaleMarkers.get(index), value, _showValues);
                        index++;
                    }
                    value = value - _increment;
                    pos = pos - _length;
                }
                this.removeScaleMarkers(index, _negScaleMarkers);
            }
        }

        /**
         * Refreshes the given ScaleMarker.
         * @param marker
         *                 The ScaleMarker, which should be refreshed
         * @param labelValue
         *                 The new value for the displayed text
         * @param showValue
         *                 True, if the value should be shown, false otherwise
         */
        private void refreshScaleMarker(final ScaleMarker marker, final double labelValue, final boolean showValue) {
            marker.setTopLeftAlignment(_isTopLeft);
            marker.setHorizontalOrientation(!_isHorizontal);
            NumberFormat format = NumberFormat.getInstance();
            format.setMaximumFractionDigits(2);
            String text = format.format(labelValue);
            marker.setText(text);
            marker.setShowValues(showValue);
            marker.setWideness(_wideness);
        }

        /**
         * Adds a new ScaleMarker into the given List at the given index.
         * @param index
         *                 The index
         * @param scaleMarkers
         *                 The List of ScaleMarkers
         */
        private void addScaleMarker(final int index, final List<ScaleMarker> scaleMarkers) {
            ScaleMarker marker = new ScaleMarker();
            scaleMarkers.add(index, marker);
            this.add(marker);
        }

        /**
         * Removes all ScaleMarkers in the given List, beginning by the given index.
         * @param index
         *                 The index
         * @param scaleMarkers
         *                 The List of ScaleMarkers
         */
        private void removeScaleMarkers(final int index, final List<ScaleMarker> scaleMarkers) {
            if (!scaleMarkers.isEmpty() && (index<=scaleMarkers.size())) {
                while (index<scaleMarkers.size()) {
                    this.remove(scaleMarkers.remove(index));
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paintFigure(final Graphics graphics) {
            //Do nothing
        }

        /**
         * Sets the length of this Scale.
         *
         * @param length
         *            The length of this Scale
         */
        public void setLength(final int length) {
            _length = length;
        }

        /**
         * Sets the orientation of this Scale.
         *
         * @param isHorizontal
         *            The orientation of this Scale
         *            (true=horizontal;false=vertical)
         */
        public void setHorizontalOrientation(final boolean isHorizontal) {
            _isHorizontal = isHorizontal;
            this.refreshConstraints();
        }

        /**
         * Sets the alignment for the ScaleMarker.
         * @param isTopLeft
         *               The alignment for the ScaleMarker
         *            (true=top/left; false=bottom/right)
         *
         */
        public void setAlignment(final boolean isTopLeft) {
            _isTopLeft = isTopLeft;
            this.refreshConstraints();
        }

        /**
         * Sets the reference values for this figure.
         *
         * @param refPos
         *            The start value
         */
        public void setReferencePositions(final int refPos) {
            _refPos = refPos;
            if (_refPos<0) {
                if (_isHorizontal) {
                    _refPos = _refPos + 1;
                } else {
                    _refPos = _refPos - 1;
                }
            }
            this.refreshConstraints();
        }

        /**
         * The begin and the end of the region, which surrounds the Markers.
         * @param begin
         *              The begin
         * @param end
         *              The end
         */
        public void setRegion(final int begin, final int end) {
            _begin = begin;
            _end = end;
            this.refreshConstraints();
        }

        /**
         * Sets if the negative sections should be drawn.
         *
         * @param showNegativ
         *            True, if the negativ sections should be drawn, false
         *            otherwise.
         */
        public void setShowNegativeSections(final boolean showNegativ) {
            _showNegativSections = showNegativ;
        }

        /**
         * Sets, if the values of the Markers should be shown.
         * @param showValues
         *                 True if the values of the Markers should be shown, false otherwise
         */
        public void setShowValues(final boolean showValues) {
            _showValues = showValues;
            this.refreshConstraints();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setForegroundColor(final Color fg) {
            super.setForegroundColor(fg);
            for (ScaleMarker marker : _posScaleMarkers) {
                marker.setForegroundColor(fg);
            }
            for (ScaleMarker marker : _negScaleMarkers) {
                marker.setForegroundColor(fg);
            }
        }

        /**
         * Sets the increment for the Scale.
         * @param value
         *             The value for the increment
         */
        public void setIncrement(final double value) {
            _increment = value;
            this.refreshConstraints();
        }

        /**
         * Sets the start value for the Markers.
         * @param startValue
         *             The start value
         */
        public void setStartValue(final double startValue) {
            _startValue = startValue;
        }

        /**
         * This class represents a marker for the scale.
         * @author Kai Meyer
         */
        private final class ScaleMarker extends RectangleFigure {
            /**
             * The Label of this ScaleMarker.
             */
            private final Label _textLabel;
            /**
             * The hyphen of this ScaleMarker.
             */
            private final ScaleHyphen _scaleHyphen;
            /**
             * The orientation of this Marker.
             */
            private boolean _isHorizontal;
            /**
             * The alignment of this Marker.
             */
            private boolean _topLeft;
            /**
             * True, if the values of the Markers should be shown, false otherwise.
             */
            private boolean _showValues = false;
            /**
             * The needed space of the hyphen.
             */
            private final int _hyphenSpace = 10;

            /**
             * Constructor.
             */
            public ScaleMarker() {
                this.setLayoutManager(new XYLayout());
                _textLabel = new Label("");
                _textLabel.setForegroundColor(this.getForegroundColor());
                _scaleHyphen = new ScaleHyphen();
                _scaleHyphen.setForegroundColor(this.getForegroundColor());
                this.add(_scaleHyphen);
                this.add(_textLabel);
                refreshConstraints();
                addFigureListener(new FigureListener() {
                    public void figureMoved(final IFigure source) {
                        refreshConstraints();
                    }
                });
            }

            private void refreshConstraints() {
                Rectangle bounds = this.getBounds();
                if (_isHorizontal) {
                    if (_topLeft) {
                        this.setConstraint(_scaleHyphen, new Rectangle(0, bounds.height-_hyphenSpace, bounds.width, _hyphenSpace));
                        this.setConstraint(_textLabel, new Rectangle(0, 0, bounds.width, bounds.height-_hyphenSpace));
                    } else {
                        this.setConstraint(_scaleHyphen, new Rectangle(0, 0, bounds.width, _hyphenSpace));
                        this.setConstraint(_textLabel, new Rectangle(0, _hyphenSpace, bounds.width, bounds.height-_hyphenSpace));
                    }
                } else {
                    if (_topLeft) {
                        this.setConstraint(_scaleHyphen, new Rectangle(bounds.width-_hyphenSpace, 0, _hyphenSpace, bounds.height));
                        this.setConstraint(_textLabel, new Rectangle(0, 0, bounds.width-_hyphenSpace, bounds.height));
                    } else {
                        this.setConstraint(_scaleHyphen, new Rectangle(0, 0, _hyphenSpace, bounds.height));
                        this.setConstraint(_textLabel, new Rectangle(_hyphenSpace, 0, bounds.width-_hyphenSpace, bounds.height));
                    }
                }
                refreshLabel();
            }

            /**
             * Refreshes the Label.
             */
            private void refreshLabel() {
                if (_showValues) {
                    _textLabel.setVisible(true);
                    if (_isHorizontal) {
                        _textLabel.setTextPlacement(PositionConstants.WEST);
                        if (_topLeft) {
                            _textLabel.setTextAlignment(PositionConstants.BOTTOM);
                        } else {
                            _textLabel.setTextAlignment(PositionConstants.TOP);
                        }
                    } else {
                        _textLabel.setTextPlacement(PositionConstants.NORTH);
                        if (_topLeft) {
                            _textLabel.setTextAlignment(PositionConstants.RIGHT);
                        } else {
                            _textLabel.setTextAlignment(PositionConstants.LEFT);
                        }
                    }
                } else {
                    _textLabel.setVisible(false);
                }
            }


            /**
             * {@inheritDoc}
             */
            @Override
            public void paintFigure(final Graphics graphics) {
                // nothing to do
            }

            /**
             * Sets the orientation of this figure.
             * @param isHorizontal
             *            The orientation of this figure
             *            (true=horizontal;false=vertical)
             */
            public void setHorizontalOrientation(final boolean isHorizontal) {
                _isHorizontal = !isHorizontal;
                _scaleHyphen.setHorizontalOrientation(isHorizontal);
                this.refreshLabel();
            }

            /**
             * Sets the alignment of this figure.
             *
             * @param topLeft
             *            The alignment of this figure
             *            (true=top/left;false=bottom/right)
             */
            public void setTopLeftAlignment(final boolean topLeft) {
                _topLeft = topLeft;
                _scaleHyphen.setAlignment(_topLeft);
                this.refreshLabel();
            }

            /**
             * Sets the displayed text.
             * @param text
             *             The text to display
             */
            public void setText(final String text) {
                _textLabel.setText(text);
                this.refreshLabel();
            }

            /**
             * Sets, if the values of the Markers should be shown.
             * @param showValues
             *                 True if the values of the Markers should be shown, false otherwise
             */
            public void setShowValues(final boolean showValues) {
                _showValues = showValues;
                this.refreshLabel();
            }

            /**
             * Sets the wideness of the Hyphen.
             * @param wideness
             *                 The wideness
             */
            public void setWideness(final int wideness) {
                _scaleHyphen.setWideness(wideness);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void setForegroundColor(final Color fg) {
                super.setForegroundColor(fg);
                _scaleHyphen.setForegroundColor(fg);
                _textLabel.setForegroundColor(fg);
            }

            /**
             * This class represents a hyphen for the scale.
             *
             * @author Kai Meyer
             */
            private final class ScaleHyphen extends RectangleFigure {
                /**
                 * The height of the line.
                 */
                private int _height = 0;
                /**
                 * The width of the line.
                 */
                private int _width = 10;
                /**
                 * The orientation of the line.
                 */
                private boolean _isHorizontal;
                /**
                 * The wideness of this Hyphen.
                 */
                private int _wideness = 10;
                /**
                 * The Alignment of this Hyphen.
                 */
                private boolean _isTopLeft;

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void paintFigure(final Graphics graphics) {
                    graphics.setForegroundColor(this.getForegroundColor());
                    //vertical
                    int x = this.getBounds().x+this.getBounds().width/2;
                    int y = this.getBounds().y;
                    if (_isHorizontal) {
                        if (_isTopLeft) {
                            x = this.getBounds().x + this.getBounds().width-_width;
                            y = this.getBounds().y + this.getBounds().height/2;
                        } else {
                            x = this.getBounds().x;
                            y = this.getBounds().y + this.getBounds().height/2;
                        }
                    }
                    graphics.drawLine(x, y,    x + _width,    y + _height);
                }

                /**
                 * Sets the wight and height of this Hyphen.
                 */
                private void setHeightAndWidth() {
                    if (_isHorizontal) {
                        _height = 0;
                        _width = _wideness;
                    } else {
                        _height = _wideness;
                        _width = 0;
                    }
                }

                /**
                 * Sets the orientation of this Hyphen.
                 * @param isHorizontal
                 *                 The Orientation of this Hyphen
                 *                 true=horizontal; false = vertical
                 */
                public void setHorizontalOrientation(final boolean isHorizontal) {
                    _isHorizontal = isHorizontal;
                    this.setHeightAndWidth();
                }

                /**
                 * Sets the wideness of the Hyphen.
                 * @param wideness
                 *                 The wideness
                 */
                public void setWideness(final int wideness) {
                    _wideness = wideness;
                    this.setHeightAndWidth();
                }

                /**
                 * Sets the alignment of this Hyphen.
                 * @param isTopLeft
                 *                 The alignment (true=top/left; false = bottom/right)
                 */
                public void setAlignment(final boolean isTopLeft) {
                    _isTopLeft = isTopLeft;
                }
            }
        }
    }

}
