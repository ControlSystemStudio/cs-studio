/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.ui.internal.figures;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.components.ui.internal.utils.ShadedDrawing;
import org.csstudio.sds.components.ui.internal.utils.TextPainter;
import org.csstudio.sds.components.ui.internal.utils.Trigonometry;
import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.csstudio.sds.util.AntialiasingUtil;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;

/**
 * The class that draws a meter on the screen. The meter is displayed as a circular sector.
 *
 * @author jbercic, Kai Meyer
 *
 */
public final class RefreshableMeterFigure extends Shape implements IAdaptable {
    /**
     * A border adapter, which covers all border handlings.
     */
    private IBorderEquippedWidget _borderAdapter;
    /**
     * This property defines the central angle of the sector, in degrees.
     */
    private int _angle = 90;
    /**
     * <i>_innerAngle</i> is the angle of the actual display - it must be smaller than
     * <i>_angle</i>.
     */
    private int _innerAngle = 80;
    /**
     * This defines the color, with which the needle is drawn.
     */
    private Color _needleColor = CustomMediaFactory.getInstance().getColor(new RGB(0, 255, 0));
    /**
     * The width of the display as a fraction of the total radius (height of the widget figure).
     */
    private double _visibleRadius = 0.5;
    /**
     * The length of the major scale lines (fraction of the total radius). Major scale lines are
     * twice as long and twice as thick.
     */
    private double _scaleRadius = 0.1;
    /**
     * The width of the scale value display area (as a fraction of the total radius).
     */
    private double _textRadius = 0.1;
    /**
     * Step for the minor scale lines.
     */
    private double _minorStep = 1.0;
    /**
     * Step for the major scale lines.
     */
    private double _majorStep = 5.0;
    /**
     * The minimum value for the scale.
     */
    private double _minValue = 0.0;
    /**
     * The maximum value for the scale.
     */
    private double _maxValue = 10.0;
    /**
     * The current value.
     */
    private double _value = 0.0;
    /**
     * Color value for the level LOLO.
     */
    private Color _loloColor = CustomMediaFactory.getInstance().getColor(new RGB(255, 0, 0));
    /**
     * Color value for the level LO.
     */
    private Color _loColor = CustomMediaFactory.getInstance().getColor(new RGB(255, 81, 81));
    /**
     * Color value for the level M.
     */
    private Color _mColor = CustomMediaFactory.getInstance().getColor(new RGB(0, 255, 0));
    /**
     * Color value for the level HI.
     */
    private Color _hiColor = CustomMediaFactory.getInstance().getColor(new RGB(255, 81, 81));
    /**
     * Color value for the level HIHI.
     */
    private Color _hihiColor = CustomMediaFactory.getInstance().getColor(new RGB(255, 0, 0));
    /**
     * Upper value for the level LOLO.
     */
    private double _loloBound = 2.0;
    /**
     * Upper value for the level LO.
     */
    private double _loBound = 4.0;
    /**
     * Upper value for the level M.
     */
    private double _mBound = 6.0;
    /**
     * Upper value for the level HI.
     */
    private double _hiBound = 8.0;
    /**
     * Upper value for the level HIHI.
     */
    private double _hihiBound = 10.0;
    /**
     * The color for the scale.
     */
    private Color _scaleColor = CustomMediaFactory.getInstance().getColor(new RGB(0, 0, 0));
    /**
     * The width of the scale.
     */
    private int _scaleWidth = 1;
    /**
     * The channel name the meter is connected to and the aliases defined for this meter.
     */
    private String _channelName = "none";
    /**
     * Is the background transparent?
     */
    private boolean _transparent = true;
    /**
     * The current width of the widget.
     */
    private int _imgWidth = 10;
    /**
     * The current height of the widget.
     */
    private int _imgHeight = 10;
    /**
     * The radius of the whole meter.
     */
    private double _r = 1.0;
    /**
     * The radius of the outer frame arc.
     */
    private double _outR = 1.0;
    /**
     * The radius of the inner frame arc.
     */
    private double _innR = 1.0;
    /**
     * The radius of the outer major scale lines.
     */
    private double _scaleMajR = 1.0;
    /**
     * The radius of the outer minor scale lines.
     */
    private double _scaleMinR = 1.0;
    /**
     * The radius of the scale value centers.
     */
    private double _textR = 1.0;
    /**
     * y coordinate of the current value text.
     */
    private int _valY = 10;
    /**
     * double the height of the channel font, so that everything can be moved down.
     */
    private int _topDelta = 0;

    /**
     * Font properties for the values (scale and current) and the channel name.
     */
    private Font _valuesFont = CustomMediaFactory.getInstance().getFont("Arial", 8, SWT.NONE);
    private Font _channelFont = CustomMediaFactory.getInstance().getFont("Arial", 8, SWT.NONE);

    /**
     * The potenz for the precision.
     */
    private int _decimalPlaces = 2;

    /**
     * Subfigures for the background and the needle.
     */
    private MeterBackground _background = null;
    private MeterNeedle _needle = null;

    /**
     * Does anything have to be recalculated?
     */
    private boolean _doCalc = false;
    /**
     * The known aliases.
     */
    private Map<String, String> _aliases;
    private CrossedOutAdapter _crossedOutAdapter;
    private RhombusAdapter _rhombusAdapter;

    public RefreshableMeterFigure() {
        setLayoutManager(new XYLayout());
        _background = new MeterBackground();
        add(_background);
        setConstraint(_background, new Rectangle(0, 0, 100, 100));
        _needle = new MeterNeedle();
        add(_needle);
        setConstraint(_needle, new Rectangle(0, 0, 100, 100));

        addFigureListener(new FigureListener() {
            public void figureMoved(final IFigure figure) {
                refreshConstraints();
                _background.invalidate();
                _needle.invalidate();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean useLocalCoordinates() {
        return true;
    }

    /**
     * Fills the meter. Nothing to do here.
     */
    @Override
    protected void fillShape(final Graphics gfx) {
        // nothing to do.
    }

    /**
     * Draws the outline of the meter. Nothing to do here.
     */
    @Override
    protected void outlineShape(final Graphics gfx) {
        // nothing to do.
    }

    /**
     * Wrapper around the Trigonometry.cos function.
     *
     * @param angl
     *            the angle, in degrees
     * @return the result of the trigonometric function
     */
    private double cosine(final double angl) {
        if (angl % 90 == 0) {
            return Trigonometry.cos(angl - 1.0);
        }
        return Trigonometry.cos(angl);
    }

    /**
     * Wrappers around the Trigonometry.sin function.
     *
     * @param angl
     *            the angle, in degrees
     * @return the result of the trigonometric function
     */
    private double sine(final double angl) {
        if (angl % 180 == 0) {
            return Trigonometry.sin(angl - 1.0);
        }
        return Trigonometry.sin(angl);
    }

    private void refreshConstraints() {
        Rectangle figureBounds = getBounds().getCopy();
        figureBounds.crop(this.getInsets());
        setConstraint(_background, new Rectangle(0, 0, figureBounds.width, figureBounds.height));
        setConstraint(_needle, new Rectangle(0, 0, figureBounds.width, figureBounds.height));
    }

    public void refresh() {
        this.calculateRadii(_valuesFont.getFontData()[0].getHeight() * 2);
    }

    /**
     * Calculates all the needed radii and gets the current dimensions of the widget.
     */
    private void calculateRadii(final int offset) {
        refreshConstraints();
        Rectangle figureBounds = getBounds().getCopy();
        figureBounds.crop(this.getInsets());
        _imgWidth = figureBounds.width;
        _imgHeight = figureBounds.height - 2 * _channelFont.getFontData()[0].getHeight() - offset;

        _r = (_imgHeight)
                / (_visibleRadius + _scaleRadius + _textRadius + (1 - _visibleRadius - _scaleRadius - _textRadius)
                        * (1 - cosine(_angle / 2)));
        if ((double) _imgWidth / 2 < sine(_angle / 2) * _r) {
            _r = (_imgWidth) / (2.0 * sine(_angle / 2));
        }
        if (_angle > 180) {
            _r = (_imgHeight) / (1 - cosine(_angle / 2));
            if (_imgWidth < 2.0 * _r) {
                _r = _imgWidth / 2.0;
            }
        }

        if (_textRadius + _scaleRadius + _visibleRadius > 1.0) {
            if ((_textRadius <= _scaleRadius) && (_textRadius <= _visibleRadius)) {
                double k1 = _scaleRadius / _textRadius;
                double k2 = _visibleRadius / _textRadius;
                _textRadius *= 1.0 / (1.0 + k1 + k2);
                _scaleRadius = k1 * _textRadius;
                _visibleRadius = k2 * _textRadius;
            } else {
                if ((_scaleRadius <= _textRadius) && (_scaleRadius <= _visibleRadius)) {
                    double k1 = _textRadius / _scaleRadius;
                    double k2 = _visibleRadius / _scaleRadius;
                    _scaleRadius *= 1.0 / (1.0 + k1 + k2);
                    _textRadius = k1 * _scaleRadius;
                    _visibleRadius = k2 * _scaleRadius;
                } else {
                    if ((_visibleRadius <= _textRadius) && (_visibleRadius <= _scaleRadius)) {
                        double k1 = _textRadius / _visibleRadius;
                        double k2 = _scaleRadius / _visibleRadius;
                        _visibleRadius *= 1.0 / (1.0 + k1 + k2);
                        _textRadius = k1 * _visibleRadius;
                        _scaleRadius = k2 * _visibleRadius;
                    }
                }
            }
        }

        _topDelta = _channelFont.getFontData()[0].getHeight() * 2;
        _outR = (1 - _textRadius - _scaleRadius) * _r;
        _innR = (1 - _textRadius - _scaleRadius - _visibleRadius) * _r;
        _scaleMinR = (1 - _textRadius - 0.5 * _scaleRadius) * _r;
        _scaleMajR = (1 - _textRadius) * _r;
        _textR = (1 - 0.5 * _textRadius) * _r;
        if (offset != 0) {
            _valY = _topDelta + _valuesFont.getFontData()[0].getHeight();
            return;
        }
        if (_angle > 180) {
            _valY = (int) _r + _topDelta;
        } else {
            if ((int) (_innR - _innR * sine(90.0 - _angle / 2)) < _valuesFont.getFontData()[0]
                    .getHeight() * 2) {
                calculateRadii(_valuesFont.getFontData()[0].getHeight() * 2);
            } else {
                _valY = _topDelta
                        + (int) (_r - _innR + (_innR - _innR * sine(90.0 - _angle / 2)) / 2);
            }
        }
    }

    /**
     * Invalidates the background subfigure.
     */
    public void invalidateBackground() {
        _doCalc = true;
        _background.invalidate();
    }

    /**
     * Invalidates the needle subfigure.
     */
    public void invalidateNeedle() {
        _needle.invalidate();
    }

    /**
     * Sets the displayed channel name.
     *
     * @param channel
     *            The name of the channel
     */
    public void setChannelName(final String channel) {
        _channelName = channel;
    }

    /**
     * Sets the known aliases.
     *
     * @param aliases
     *            A {@link Map} with the aliases
     */
    public void setAliases(final Map<String, String> aliases) {
        if (aliases == null) {
            _aliases = new HashMap<String, String>();
        } else {
            _aliases = aliases;
        }
    }

    public void setAngle(final int angl) {
        _angle = angl;
        invalidateBackground();
    }

    public int getAngle() {
        return _angle;
    }

    public void setInnerAngle(final int angl) {
        _innerAngle = angl;
        invalidateBackground();
        invalidateNeedle();
    }

    public int getInnerAngle() {
        return _innerAngle;
    }

    public void setNeedleColor(final Color color) {
        _needleColor = color;
        invalidateNeedle();
    }

    public void setVisibleRadius(final double newrad) {
        _visibleRadius = newrad;
        invalidateBackground();
        invalidateNeedle();
    }

    public double getVisibleRadius() {
        return _visibleRadius;
    }

    public void setScaleRadius(final double newrad) {
        _scaleRadius = newrad;
        invalidateBackground();
    }

    public double getScaleRadius() {
        return _scaleRadius;
    }

    public void setMinorStep(final double minstep) {
        _minorStep = minstep;
        invalidateBackground();
    }

    public double getMinorStep() {
        return _minorStep;
    }

    public void setMajorStep(final double maxstep) {
        _majorStep = maxstep;
        invalidateBackground();
    }

    public double getMajorStep() {
        return _majorStep;
    }

    public void setMaxValue(final double max) {
        _maxValue = max;
        invalidateBackground();
        invalidateNeedle();
    }

    public double getMaxValue() {
        return _maxValue;
    }

    public void setMinValue(final double min) {
        _minValue = min;
        invalidateBackground();
        invalidateNeedle();
    }

    public double getMinValue() {
        return _minValue;
    }

    public void setValue(final double newval) {
        _value = newval;
        invalidateNeedle();
    }

    public double getValue() {
        return _value;
    }

    public void setScaleColor(final Color newval) {
        _scaleColor = newval;
        invalidateBackground();
    }

    public void setScaleWidth(final int newval) {
        _scaleWidth = newval;
        invalidateBackground();
    }

    public int getScaleWidth() {
        return _scaleWidth;
    }

    public void setTextRadius(final double newval) {
        _textRadius = newval;
        invalidateBackground();
    }

    public double getTextRadius() {
        return _textRadius;
    }

    public void setTransparent(final boolean newval) {
        _transparent = newval;
    }

    public boolean getTransparent() {
        return _transparent;
    }

    public void setMColor(final Color newval) {
        _mColor = newval;
        invalidateBackground();
    }

    public void setLOLOColor(final Color newval) {
        _loloColor = newval;
        invalidateBackground();
    }

    public void setLOColor(final Color newval) {
        _loColor = newval;
        invalidateBackground();
    }

    public void setHIColor(final Color newval) {
        _hiColor = newval;
        invalidateBackground();
    }

    public void setHIHIColor(final Color newval) {
        _hihiColor = newval;
        invalidateBackground();
    }

    public void setMBound(final double newval) {
        _mBound = newval;
        invalidateBackground();
    }

    public double getMBound() {
        return _mBound;
    }

    public void setLOLOBound(final double newval) {
        _loloBound = newval;
        invalidateBackground();
    }

    public double getLOLOBound() {
        return _loloBound;
    }

    public void setLOBound(final double newval) {
        _loBound = newval;
        invalidateBackground();
    }

    public double getLOBound() {
        return _loBound;
    }

    public void setHIBound(final double newval) {
        _hiBound = newval;
        invalidateBackground();
    }

    public double getHIBound() {
        return _hiBound;
    }

    public void setHIHIBound(final double newval) {
        _hihiBound = newval;
        invalidateBackground();
    }

    public double getHIHIBound() {
        return _hihiBound;
    }

    public void setValuesFont(final Font font) {
        _valuesFont = font;
        invalidateBackground();
        invalidateNeedle();
    }

    public Font getValuesFont() {
        return _valuesFont;
    }

    public void setChannelFont(final Font font) {
        _channelFont = font;
        invalidateBackground();
        invalidateNeedle();
    }

    public Font getChannelFont() {
        return _channelFont;
    }

    /**
     * Sets the count of decimal places for this Figure.
     *
     * @param decimalPlaces
     *            The precision
     */
    public void setDecimalPlaces(final int decimalPlaces) {
        _decimalPlaces = decimalPlaces;
        invalidateNeedle();
    }

    /**
     * Gets the precision of this Figure.
     *
     * @return The precision
     */
    public int getDecimalPlaces() {
        return _decimalPlaces;
    }

    /**
     * Subfigure that draws the arched frame and background of the meter.
     *
     * @author jbercic
     *
     */
    class MeterBackground extends Shape {
        /**
         * Fills the background with colors for the five levels.
         */
        @Override
        protected void fillShape(final Graphics gfx) {
            double uppAngle = (_innerAngle / (_maxValue - _minValue))
                    * (_loloBound - _minValue);
            double loAngle = 90.0 + _innerAngle / 2.0;
            if (!_transparent) {
                gfx.setBackgroundColor(getBackgroundColor());
                gfx.fillRectangle(getBounds());
            }

            AntialiasingUtil.getInstance().enableAntialiasing(gfx);
            // lolo area
            gfx.setBackgroundColor(_loloColor);
            gfx.fillArc(calculateRadius(_outR),
                        (int) Math.round(loAngle - uppAngle),
                        (int) Math.round(uppAngle));
            // lo area
            loAngle -= uppAngle;
            uppAngle = (_innerAngle / (_maxValue - _minValue)) * (_loBound - _loloBound);
            gfx.setBackgroundColor(_loColor);
            gfx.fillArc(calculateRadius(_outR),
                        (int) Math.round(loAngle - uppAngle),
                        (int) Math.round(uppAngle));
            // m area
            loAngle -= uppAngle;
            uppAngle = (_innerAngle / (_maxValue - _minValue)) * (_mBound - _loBound);
            gfx.setBackgroundColor(_mColor);
            gfx.fillArc(calculateRadius(_outR),
                        (int) Math.round(loAngle - uppAngle),
                        (int) Math.round(uppAngle));
            // hi area
            loAngle -= uppAngle;
            uppAngle = (_innerAngle / (_maxValue - _minValue)) * (_hiBound - _mBound);
            gfx.setBackgroundColor(_hiColor);
            gfx.fillArc(calculateRadius(_outR),
                        (int) Math.round(loAngle - uppAngle),
                        (int) Math.round(uppAngle));
            // hihi area
            loAngle -= uppAngle;
            uppAngle = (_innerAngle / (_maxValue - _minValue)) * (_hihiBound - _hiBound);
            gfx.setBackgroundColor(_hihiColor);
            gfx.fillArc(calculateRadius(_outR),
                        (int) Math.round(loAngle - uppAngle),
                        (int) Math.round(uppAngle));

            // background color
            gfx.setBackgroundColor(getBackgroundColor());
            gfx.fillArc(calculateRadius(_innR),
                        (int) Math.round(90.0 - _angle / 2),
                        _angle);
        }

        /**
         * @param radius TODO
         * @return
         */
        private Rectangle calculateRadius(final double radius) {
            return new Rectangle((int) (_imgWidth / 2 - radius),
                                 (int) (_r - radius) + _topDelta,
                                 (int) (radius * 2.0),
                                 (int) (radius * 2.0));
        }

        /**
         * The main drawing routine.
         */
        @Override
        public void paintFigure(final Graphics gfx) {
            if (_doCalc) { // helge 1
                _doCalc = false;
                calculateRadii(0);
            }
            fillShape(gfx);
            outlineShape(gfx);
            _crossedOutAdapter.paint(gfx, false);
            _rhombusAdapter.paint(gfx, false);


        }

        /**
         * Draws the outline of the background: the frame, the scale and the channel name.
         */
        @Override
        protected void outlineShape(final Graphics gfx) {
            AntialiasingUtil.getInstance().enableAntialiasing(gfx);
            /**
             * WORKAROUND: rotation does something weird to the Graphics object, so that subsequent
             * font setting does not seem to work. Pushing and popping the state seems to fix this.
             */
            gfx.pushState();
            drawScale(gfx);
            gfx.popState();
            drawChannelName(gfx);
            drawFrame(gfx);
        }

        /**
         * Draws the channel name below the meter.</br> The channel used is the one connected to the
         * value property.
         *
         * @param gfx
         *            The Graphics context.
         */
        private void drawChannelName(final Graphics gfx) {
            gfx.setForegroundColor(getForegroundColor());
            gfx.setFont(_channelFont);
            String toprint;
            try {
                toprint = ChannelReferenceValidationUtil
                        .createCanonicalName(_channelName, _aliases);
            } catch (ChannelReferenceValidationException e) {
                toprint = _channelName;
                // TODO: Should this be logged properly?
                e.printStackTrace();
            }
            TextPainter.drawText(gfx, toprint, _imgWidth / 2, _channelFont.getFontData()[0]
                    .getHeight(), TextPainter.CENTER);
        }

        /**
         * Draws the scale and values at major scale lines.
         *
         * @param gfx
         *            The Graphics context.
         */
        private void drawScale(final Graphics gfx) {
            double curr;
            double currAngle;

            gfx.setForegroundColor(_scaleColor);
            gfx.setLineWidth(_scaleWidth);
            // minor scale lines
            for (curr = _minValue + _minorStep; curr <= _maxValue - _minorStep; curr += _minorStep) {
                currAngle = 90.0 + (double) _innerAngle / 2
                        - (_innerAngle / (_maxValue - _minValue)) * (curr - _minValue);
                ShadedDrawing.drawLineAtAngle(gfx,_outR,_scaleMinR,currAngle,_imgWidth / 2,(int) _r + _topDelta);
            }
            int wdth = gfx.getLineWidth();
            if (wdth == 0) {
                wdth = 1;
            }
            gfx.setLineWidth(wdth * 2);
            // major scale lines
            String val;
            gfx.setFont(_valuesFont);
            for (curr = _minValue; curr <= _maxValue; curr += _majorStep) {
                // the tick mark
                currAngle = 90.0 + (double) _innerAngle / 2
                        - (_innerAngle / (_maxValue - _minValue)) * (curr - _minValue);
                gfx.setForegroundColor(_scaleColor);
                ShadedDrawing.drawLineAtAngle(gfx,
                                              _outR,
                                              _scaleMajR,
                                              currAngle,
                                              _imgWidth / 2,
                                              (int) _r + _topDelta);
                // the value of the tick mark
                try {
                    NumberFormat format = NumberFormat.getInstance();
                    format.setMaximumFractionDigits(_decimalPlaces);
                    val = format.format(curr);
                } catch (Exception e) {
                    val = Double.toString(curr);
                }
                gfx.setForegroundColor(getForegroundColor());
                TextPainter.drawRotatedText(gfx, val, 90.0 - currAngle, _imgWidth / 2
                        + (int) (cosine(currAngle) * _textR), (int) _r
                        - (int) (sine(currAngle) * _textR) + _topDelta, TextPainter.CENTER);
            }
        }

        /**
         * Draws the arched frame of the meter.
         * <p>
         * <b>WARNING:</b> if the starting and ending point of an arc are close together, the arc
         * may not be drawn at all due to rounding errors (the angles given must be integer). For
         * example if start=-89 and angle=359, the endings are only a pixel apart, if the radius is
         * less than about 54 pixels. The ending is further to the left (drawing is
         * counter-clockwise), meaning that the arc is only 1 pixel long, instead of a full circle.
         * </p>
         *
         * @param gfx
         *            The Graphics context.
         */
        private void drawFrame(final Graphics gfx) {
            gfx.setForegroundColor(CustomMediaFactory.getInstance().getColor(0, 0, 0));
            gfx.setLineWidth(1);
            // outer arc
            gfx.drawArc((int) (_imgWidth / 2 - _outR),
                        (int) (_r - _outR) + _topDelta,
                        (int) _outR * 2,
                        (int) _outR * 2,
                        (int) Math.round(90.0 - _angle / 2),
                        _angle);
            // inner arc
            gfx.drawArc((int) (_imgWidth / 2 - _innR),
                        (int) (_r - _innR) + _topDelta,
                        (int) _innR * 2,
                        (int) _innR * 2,
                        (int) Math.round(90.0 - _angle / 2),
                        _angle);
            // left and right lines
            gfx.drawArc(_imgWidth / 2 - (int) _r,
                        _topDelta,
                        (int) (2.0 * _r),
                        (int) (2.0 * _r),
                        (int) Math.round(90.0 - _angle / 2),
                        _angle);
            ShadedDrawing.drawLineAtAngle(gfx,
                                          _innR,/* out_r */
                                          _r,
                                          90 + _angle / 2,
                                          _imgWidth / 2,
                                          (int) _r + _topDelta);
            ShadedDrawing.drawLineAtAngle(gfx, _innR, _r, 90 - _angle / 2, _imgWidth / 2, (int) _r
                    + _topDelta);
        }
    }

    /**
     * Subfigure that draws the needle and current value.
     *
     * @author jbercic
     *
     */
    class MeterNeedle extends Shape {
        /**
         * The main drawing routine.
         */
        @Override
        public void paintFigure(final Graphics gfx) {
            outlineShape(gfx); // helge 2
        }

        /**
         * Fills the subfigure. Nothing to do here.
         */
        @Override
        protected void fillShape(final Graphics gfx) {
            // nothing to do.
        }

        /**
         * Draws the outline. Here this means the needle and current value.
         */
        @Override
        protected void outlineShape(final Graphics gfx) {
            AntialiasingUtil.getInstance().enableAntialiasing(gfx);
            gfx.setForegroundColor(_needleColor);
            gfx.setLineWidth(4);
            gfx.setFont(_channelFont);
            double currAngle = 90.0 + (double) _innerAngle / 2
                    - (_innerAngle / (_maxValue - _minValue)) * (_value - _minValue);
            ShadedDrawing.drawLineAtAngle(gfx, _innR, _outR, currAngle, _imgWidth / 2, (int) _r
                    + _topDelta);

            String val;
            try {
                NumberFormat format = NumberFormat.getInstance();
                format.setMaximumFractionDigits(_decimalPlaces);
                val = format.format(_value);
            } catch (Exception e) {
                val = Double.toString(_value);
            }
            gfx.setFont(_channelFont);
            gfx.setForegroundColor(getForegroundColor());
            TextPainter.drawText(gfx, val, _imgWidth / 2, _valY, TextPainter.CENTER);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter) {
        if (adapter == IBorderEquippedWidget.class) {
            if (_borderAdapter == null) {
                _borderAdapter = new BorderAdapter(this) {
                    @Override
                    protected AbstractBorder createShapeBorder(final int borderWidth,
                                                               final Color borderColor) {
                        MeterBorder meterBorder = new MeterBorder(borderWidth);
                        meterBorder.setBorderColor(borderColor);
                        return meterBorder;
                    }
                };
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
     * The Border for this {@link RefreshableMeterFigure}.
     *
     * @author Kai Meyer
     *
     */
    private final class MeterBorder extends AbstractBorder {

        /**
         * The Color of the border.
         */
        private Color _borderColor;
        /**
         * The width of the border.
         */
        private int _borderWidth = 1;

        /**
         * Constructor.
         *
         * @param borderWidth
         *            The width for the border
         */
        public MeterBorder(final int borderWidth) {
            _borderWidth = borderWidth;
        }

        /**
         * Sets the Color of the border.
         *
         * @param borderColor
         *            The Color for the border
         */
        public void setBorderColor(final Color borderColor) {
            _borderColor = borderColor;
        }

        /**
         * Returns <code>true</code> since this border is opaque. Being opaque it is responsible to
         * fill in the area within its boundaries.
         *
         * @return <code>true</code> since this border is opaque
         */
        @Override
        public boolean isOpaque() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public Insets getInsets(final IFigure figure) {
            return new Insets(0);
        }

        /**
         * {@inheritDoc}
         */
        public void paint(final IFigure figure, final Graphics graphics, final Insets insets) {
            graphics.setForegroundColor(_borderColor);
            graphics.setLineWidth(_borderWidth);
            Rectangle borderBounds = figure.getBounds();
            // outer arc
            graphics.drawArc((int) (_imgWidth / 2 - _innR) + borderBounds.x, (int) (_r - _innR)
                    + _topDelta + borderBounds.y, (int) _innR * 2, (int) _innR * 2, (int) Math
                    .round(90.0 - _angle / 2), _angle);
            // inner arc
            graphics.drawArc(_imgWidth / 2 - (int) _r + borderBounds.x,
                             _topDelta + borderBounds.y,
                             (int) (2.0 * _r),
                             (int) (2.0 * _r),
                             (int) Math.round(90.0 - _angle / 2),
                             _angle);
            // left and right lines
            ShadedDrawing.drawLineAtAngle(graphics, _innR, _r, 90 + _angle / 2, _imgWidth / 2
                    + borderBounds.x, (int) _r + _topDelta + borderBounds.y);
            ShadedDrawing.drawLineAtAngle(graphics, _innR, _r, 90 - _angle / 2, _imgWidth / 2
                    + borderBounds.x, (int) _r + _topDelta + borderBounds.y);
        }
    }

}
