/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

import org.csstudio.sds.components.ui.internal.utils.TextPainter;
import org.csstudio.sds.ui.figures.BorderAdapter;
import org.csstudio.sds.ui.figures.CrossedOutAdapter;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.ICrossedFigure;
import org.csstudio.sds.ui.figures.IRhombusEquippedWidget;
import org.csstudio.sds.ui.figures.ITextFigure;
import org.csstudio.sds.ui.figures.RhombusAdapter;
import org.csstudio.sds.util.AntialiasingUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

/**
 * A label figure.
 *
 * @author jbercic
 *
 */
public final class RefreshableLabelFigure extends Shape implements IAdaptable, ITextFigure {

    /**
     * A border adapter, which covers all border drawing.
     */
    private IBorderEquippedWidget _borderAdapter;

    /**
     * Default label font.
     */
    private Font _font = CustomMediaFactory.getInstance().getFont("Arial", 8, SWT.NONE);

    /**
     * An Array, which contains the PositionConstants for Center, Top, Bottom, Left, Right.
     */
    private final int[] _alignments = new int[] {PositionConstants.CENTER, PositionConstants.TOP, PositionConstants.BOTTOM, PositionConstants.LEFT, PositionConstants.RIGHT};

    /**
     * The alignment of the text.
     */
    private int _alignment=0;
    /**
     * The rotation of the text.
     */
    private double _rotation=90.0;
    /**
     * The x offset of the text.
     */
    private int _xOff=0;
    /**
     * The x offset of the text.
     */
    private int _yOff=0;
    /**
     * Value fields.
     */
    private String _textValue="";
    /**
     * Is the background transparent or not?
     */
    private boolean _transparent=true;

    private RhombusAdapter _rhombusAdapter;

    private CrossedOutAdapter _crossedOutAdapter;

    /**
     * Fills the image. Nothing to do here.
     * @param gfx The {@link Graphics} to use.
     */
    @Override
    protected void fillShape(final Graphics gfx) {/* Nothing to do here.*/}

    /**
     * Draws the outline of the image. Nothing to do here.
     * @param gfx The {@link Graphics} to use.
     */
    @Override
    protected void outlineShape(final Graphics gfx) {/* Nothing to do here.*/}


    public RefreshableLabelFigure() {
    }
    /**
     * The main drawing routine.
     * @param gfx The {@link Graphics} to use.
     */
    @Override
    public void paintFigure(final Graphics gfx) {

        Rectangle bound = getBounds().getCopy();
        bound.crop(this.getInsets());
        gfx.translate(bound.x, bound.y);

        if (!_transparent) {
            gfx.setBackgroundColor(getBackgroundColor());
            gfx.fillRectangle(0, 0, bound.width, bound.height);
        }
        gfx.setFont(_font);
        gfx.setForegroundColor(getForegroundColor());
        AntialiasingUtil.getInstance().enableAntialiasing(gfx);

        Point textPoint;
        int alignment;
        switch (_alignment) {
            case 0: //center
                textPoint = new Point(bound.width / 2 + _xOff, bound.height / 2 + _yOff);
                alignment = TextPainter.CENTER;
                break;
            case 1: //top
                textPoint = new Point(bound.width / 2 + _xOff, _yOff);
                alignment = TextPainter.TOP_CENTER;
                break;
            case 2: //bottom
                textPoint = new Point(bound.width / 2 + _xOff, bound.height + _yOff);
                alignment = TextPainter.BOTTOM_CENTER;
                break;
            case 3: //left
                textPoint = new Point(_xOff, bound.height / 2 + _yOff);
                alignment = TextPainter.LEFT;
                break;
            case 4: //right
                textPoint = new Point(bound.width + _xOff, bound.height / 2 + _yOff);
                alignment = TextPainter.RIGHT;
                break;
            default: //default
                textPoint = new Point(bound.width / 2 + _xOff, bound.height / 2 + _yOff);
                alignment = TextPainter.CENTER;
                break;
        }
        if (!isEnabled()) {
            gfx.setForegroundColor(ColorConstants.buttonLightest);
            if (Math.round(_rotation) == 90) {
                TextPainter.drawText(gfx, _textValue, textPoint.x, textPoint.y, alignment);
            } else {
                TextPainter.drawRotatedText(gfx,
                                            _textValue,
                                            90.0 - _rotation,
                                            textPoint.x + 1,
                                            textPoint.y + 1,
                                            alignment);
            }

            gfx.setForegroundColor(ColorConstants.buttonDarker);
        }
        if (Math.round(_rotation) == 90) {
            TextPainter.drawText(gfx, _textValue, textPoint.x, textPoint.y, alignment);
        } else {
            TextPainter.drawRotatedText(gfx,
                                        _textValue,
                                        90.0 - _rotation,
                                        textPoint.x,
                                        textPoint.y,
                                        alignment);
        }
        gfx.setAntialias(SWT.OFF);

        _crossedOutAdapter.paint(gfx,false);
        _rhombusAdapter.paint(gfx,false);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFont(final Font newval) {
        _font=newval;
    }

    /**
     * Sets the alignment for the text.
     * @param newval The alignment for the text
     */
    public void setTextAlignment(final int newval) {
        if ((newval>=0) && (newval<_alignments.length)) {
            _alignment=newval;
        }
    }

    /**
     * Sets the transparent state of the background.
     * @param newval The transparent state
     */
    public void setTransparent(final boolean newval) {
        _transparent=newval;
    }

    /**
     * Sets the rotation for the text.
     * @param newval The rotation for the text
     */
    public void setRotation(final double newval) {
        _rotation=newval;
    }

    /**
     * Sets the x offset for the text.
     * @param newval The x offset
     */
    public void setXOff(final int newval) {
        _xOff=newval;
    }

    /**
     * Sets the y offset for the text.
     * @param newval The y offset
     */
    public void setYOff(final int newval) {
        _yOff=newval;
    }

    /**
     * Sets the value for the text.
     * @param newval The value for the text
     */
    public void setTextValue(final String newval) {
        _textValue=newval;
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

}
